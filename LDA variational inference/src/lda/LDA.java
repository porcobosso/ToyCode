package lda;

import lda.bean.Corpus;
import lda.bean.Document;
import lda.util.LDAUtil;

public class LDA {
	private double[] alpha;
	private double[][] beta;
	private double[][] gammas;
	
	private int k;
	private Corpus cps;
	private int maxEM;
	private int maxNT;
	private int v;
	private int m;
	private int maxEs;
	
	public LDA(int k,Corpus cps,int maxEM,int maxEs,int maxNT) {
		this.cps=cps;
		this.maxEM=maxEM;
		this.maxNT=maxNT;
		this.maxEs=maxEs;
		this.k=k;
		this.v=cps.getVocLen();
		this.m=cps.len();
		
	}
	
	private void init(){
		alpha = new double[k];
		for(int i=0;i<k;i++){
			alpha[i]=Math.random();
		}
		
		LDAUtil.normalize(alpha);
		
		beta = new double[k][v];
		for(int i=0;i<k;i++){
			for(int j=0;j<v;j++)
				beta[i][j]=Math.random();
			LDAUtil.normalize(beta[i]);
		}
		
		gammas = new double[m][k];
	}
	
	public void train(){
		init();
		double ppl=0;
		double pppl=0;
		for(int i=0;i<maxEM;i++){
			double[][] betas = new double[k][v];
			for(int j=0;j<m;j++){
				Document d = cps.getDocument(j);
				
				//e-step
				//estep返回类型为void，在函数中修改gamma和phi
				double[] gamma = new double[k];
				int n = d.uniwordLen();
				double[][] phi = new double[n][k];
				estep(d,gamma,phi);
				
				//将gamma并入gammas
				for(int z=0;z<k;z++){
					gammas[j][z]=gamma[z];
				}
				//将beta并入 betas
				for(int p=0;p<n;p++){
					int[] word=d.getWord(p);
					for(int q=0;q<k;q++){
						betas[q][word[0]]+=phi[p][q]*word[1];
					}
				}
			}
			
			//m-step
			//更新beta，其实就是一个正规化过程
			for(int z=0;z<k;z++){
				LDAUtil.normalize(betas[z]);
				beta[z] = betas[z];
			}
			//使用newton法更新alpha
			int level = 0;
			boolean success=false;
			while(!success){
				success=newtonAlpha(level++);
			}
			
			//判断是否收敛：通过计算likelihood判断
			ppl=likelihood();
			if(i>1&&Math.abs((ppl-pppl)/pppl)<0.01)
				if(i<5){
					train();
					return;
				}else {
					break;
				}
			pppl=ppl;
		}
	}
	
	private boolean newtonAlpha(int level) {
		double[] palpha = new double[k];
		for(int i=0;i<k;i++){
			alpha[i] = 0;
			for(int j=0;j<m;j++)
				alpha[i] += gammas[j][i];
			alpha[i] /= m*k*Math.pow(10, level);
			palpha[i] = alpha[i];
		}
		
		double pg = 0;
		for(int i=0;i<m;i++){
			double sumg = 0;
			for(int j=0;j<k;j++)
				sumg += gammas[i][j];
			
			pg += LDAUtil.psi(sumg, 0);
		}
		
		double[] psg = new double[k];
		for(int i=0;i<k;i++){
			double z=0;
			for(int j=0;j<m;j++){
				z += LDAUtil.psi(gammas[j][i], 0);
			}
			psg[i] = z-pg;
		}
		
		for(int t=0;t<maxNT;t++){
			double sumalpha=0;
			double[] g = new double[k];
			for(int i=0;i<k;i++){
				sumalpha += alpha[i];
			}
			double digammaSumAlpha = LDAUtil.psi(sumalpha, 0);
			for(int i=0;i<k;i++){
				g[i] += m*(digammaSumAlpha-LDAUtil.psi(alpha[i], 0))+psg[i];
			}
			double z=1.0/LDAUtil.psi(sumalpha, 1);
			double[] h = new double[k];
			double sumh=0;
			for(int i=0;i<k;i++){
				h[i]=-1.0/LDAUtil.psi(alpha[i], 1);
				sumh += h[i];
			}
			
			double c=0;
			for(int i=0;i<k;i++){
				c+=h[i]*g[i];
			}
			c /= (z+sumh);
			
			boolean fail=false;
			for(int i=0;i<k;i++){
				alpha[i]-=h[i]*(g[i]-c)/m;
				if(alpha[i]<0)
					fail=true;
			}
			
			if(fail)
				return false;
			
			if(t>0&&LDAUtil.converged(alpha, palpha, 1.0e-4)){
				return true;
			}
			
			for(int i=0;i<k;i++){
				palpha[i]=alpha[i];
			}
		}
		return true;
	}

	private void estep(Document d,double[] gamma,double[][] phi){
		int n = d.uniwordLen();
		//gamma = alpha+nt
		double[] nt = new double[k];
		//用于保存前一次的nt，在判断是否收敛时用到
		double[] pnt = new double[k];
		for(int i=0;i<k;i++){
			//初始化为  N/k
			nt[i]=n*1.0/k;
		}
		
		double[] ap = new double[k];
		
		for(int t=0;t<maxEs;t++){
			for(int i=0;i<k;i++){
				//更新phi，用到的公共部分 digamma的值
				ap[i] = LDAUtil.psi(alpha[i]+nt[i],0);
			}
			
			for(int i=0;i<n;i++){
				for(int j=0;j<k;j++){
					int[] tword = d.getWord(i);
					phi[i][j] = beta[j][tword[0]]*ap[j];
				}
				LDAUtil.normalize(phi[i]);
			}
			
			for(int i=0;i<k;i++){
				nt[i]=0;
				for(int j=0;j<n;j++){
					int[] tword = d.getWord(j);
					nt[i]+=phi[j][i]*tword[1];
				}
			}
			
			if(t>0&&LDAUtil.converged(nt, pnt, 1.0e-2))
				break;
			for(int i=0;i<k;i++)
				pnt[i]=nt[i];
		}
		for(int i=0;i<k;i++){
			gamma[i]=nt[i]+alpha[i];
		}
	}
	
	private double likelihood(){
		int totalwords = 0;
		for(int i=0;i<m;i++)
			totalwords += cps.getDocument(i).len();
		
		double[][] egammas = new double[m][k];
		for(int i=0;i<m;i++){
			for(int j=0;j<k;j++){
				egammas[i][j] = gammas[i][j];
			}
			LDAUtil.normalize(egammas[i]);
		}
		
		double lik=0;
		for(int i=0;i<m;i++){
			Document d = cps.getDocument(i);
			for(int j=0;j<d.uniwordLen();j++){
				double z=0;
				for(int p=0;p<k;p++){
					z+=beta[p][d.getWord(j)[0]]*egammas[i][p];
				}
				lik+=Math.log(z)*d.getWord(j)[1];
			}
		}
		
		return Math.exp(-lik/totalwords);
	}
}
