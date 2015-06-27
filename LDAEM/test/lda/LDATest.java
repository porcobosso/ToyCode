package lda;

import lda.bean.Document;
import lda.util.LDAUtil;

import org.junit.Test;

public class LDATest{
	@Test
	public void estep(){
		double[] alpha=new double[]{0.2,0.8};
		double[][] beta = new double[][]{
				{0.25,0.25,0.5},
			    {0.3,0.4,0.3}
		};
		int[][] d=new int[][]{
				{0,2},
				{1,1},
				{2,1}
		};
		
		int n = 3;
		int k = 2;
		double[][] phi = new double[n][k];
		//gamma = alpha+nt
		double[] nt = new double[k];
		//用于保存前一次的nt，在判断是否收敛时用到
		double[] pnt = new double[k];
		for(int i=0;i<k;i++){
			//初始化为  N/k
			nt[i]=n*1.0/k;
		}
		
		double[] ap = new double[k];
		
		for(int i=0;i<k;i++){
			//更新phi，用到的公共部分 digamma的值
			ap[i] = Math.exp(LDAUtil.psi(alpha[i]+nt[i],0));
		}
		
		for(int i=0;i<n;i++){
			for(int j=0;j<k;j++){
				int[] tword = d[i];
				phi[i][j] = beta[j][tword[0]]*ap[j];
			}
			LDAUtil.normalize(phi[i]);
		}
		
		for(int i=0;i<k;i++){
			nt[i]=0;
			for(int j=0;j<n;j++){
				int[] tword = d[j];
				nt[i]+=phi[j][i]*tword[1];
			}
		}
		System.out.println(nt);
	}
}
