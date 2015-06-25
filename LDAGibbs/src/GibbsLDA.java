import java.util.Random;


public class GibbsLDA {
	
	private static Random rd = new Random();
	
	public static void testMode(long seed){
		rd.setSeed(seed);
	}
	
	public static void train(Cropus cp,int itr,int k,double beta,double alpha,boolean ct){
		int[][] nmk = new int[cp.doc.length][k];
		int[][] nkt = new int[k][cp.voc.size()];
		int[] nk = new int[k];
		init(cp, k, nmk, nkt, nk,ct);
		
		double[] probs = new double[k];
		int count = 0;
		while(count++<itr){
			for(int i=0;i<cp.doc.length;i++){
				int[] word = cp.doc[i].word;
				int[] topic = cp.doc[i].wordTopic;
				for(int j=0;j<word.length;j++){
					int tpc = topic[j];
					int v = word[j];
					
					nmk[i][tpc]--;
					nkt[tpc][v]--;
					nk[tpc]--;
					
					for(int p=0;p<k;p++){
						probs[p] = (nkt[p][v]+beta)*(nmk[i][p]+alpha)/(nk[p]+cp.voc.size()*beta);
					}
					int ttpc = multinomial(probs);
					topic[j] = ttpc;
					nmk[i][ttpc]++;
					nkt[ttpc][v]++;
					nk[ttpc]++;
				}
			}
			System.out.println("iteration:"+count);
		}
		
		updateParam(cp, k, nmk, nkt, nk, beta, alpha);
	}
	
	public static void predict(Document dc,Cropus cp,int itr,int k,double beta,double alpha){
		int[][] nmk = new int[cp.doc.length][k];
		int[][] nkt = new int[k][cp.voc.size()];
		int[] nk = new int[k];
		init(cp, k, nmk, nkt, nk, true);
		
		int[][] nkt_m = new int[k][cp.voc.size()];
		int[] nk_m = new int[k];
		initDc(dc, k, nkt_m, nk_m);
		
		double[] probs = new double[k];
		int count = 0;
		int[] word = dc.word;
		int[] topic = dc.wordTopic;
		while(count++<itr){
			for(int i=0;i<word.length-1;i++){
				int tpc = topic[i];
				nkt_m[tpc][word[i]]--;
				nk_m[tpc]--;
				
				for(int p=0;p<k;p++){
					probs[p] = (nkt[p][word[i]]+nkt_m[p][word[i]]+beta)*(nk_m[p]+alpha)
						/(nk[p]+nk_m[p]+cp.voc.size()*beta);
				}
				int ttpc = multinomial(probs);
				topic[i] = ttpc;
				nkt_m[ttpc][word[i]]++;
				nk_m[ttpc]++;
			}
		}
		dc.topic = new double[k];
		for(int p=0;p<k;p++){
			dc.topic[p] = (nk_m[p]+alpha)/(word.length+k*alpha);
		}
	}
	
	
	private static void updateParam(Cropus cp,int k,int[][] nmk,int[][] nkt,int[] nk,double beta,double alpha){
		for(int i=0;i<cp.doc.length;i++){
			cp.doc[i].topic = new double[k];
			double[] topic = cp.doc[i].topic;
			for(int p=0;p<k;p++){
				topic[p] = (nmk[i][p]+alpha)/(cp.doc[i].word.length+k*alpha);
			}
		}
		
		cp.weight = new double[k][cp.voc.size()];
		for(int i=0;i<cp.weight.length;i++){
			for(int p=0;p<k;p++){
				cp.weight[p][i] = (nkt[p][i]+beta)/(nk[p]+cp.weight.length*beta);
			}
		}
	}
	
	private static void initDc(Document dc,int k,int[][] nkt_m,int[] nk_m){
		double[] initProbs = new double[k];
		for(int i=0;i<k;i++) initProbs[i] = 1;
		for(int i = 0;i<dc.word.length;i++){
			int tpc = multinomial(initProbs);
			dc.wordTopic[i] = tpc;
			nkt_m[tpc][dc.word[i]]++;
			nk_m[tpc]++;
		}
	}
	
	private static void init(Cropus cp,int k,int[][] nmk,int[][] nkt,int[] nk, boolean ct){
		double[] initProbs = new double[k];
		for(int i=0;i<k;i++) initProbs[i] = 1;
		for(int i=0;i<cp.doc.length;i++){
			int[] word = cp.doc[i].word;
			int[] topic = cp.doc[i].wordTopic;
			for(int j=0;j<word.length;j++){
				int tpc = ct? topic[j]:multinomial(initProbs);
				topic[j] = tpc;
				nmk[i][tpc]++;
				nkt[tpc][word[j]]++;
				nk[tpc]++;
			}
		}
	}
	
	private static int multinomial(double[] probs){
		double sum = 0;
		for(double prob:probs) sum += prob;
		double pre = rd.nextDouble()*sum;
		
		double accum = 0;
		int value = -1;
		for(int i=0;i<probs.length-1;i++){
			accum += probs[i];
			if(pre<accum){
				value = i;
				break;
			}
		}
		
		return value==-1? (probs.length-1):value;
	}
}
