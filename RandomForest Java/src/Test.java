import util.Util;
import cart.Cart;
import cart.RFRunner;
import cart.RadomForest;


public class Test {

	public static void main(String[] args) throws Exception{
		long start = System.currentTimeMillis();
		
		Util.setPath("A:/study/...");
		double[][] tr_d=Util.load("hw3_train.dat");
		int tr_N=Util.size(tr_d, true);
		int tr_M=Util.size(tr_d, false);
		
		int[] rowIndexs=Util.fromToIndex(0, tr_N-1);
		int[] columnIndexs=Util.fromToIndex(0, tr_M-2);
		double[][] tr_X=Util.getByIndex(tr_d, rowIndexs, columnIndexs);
		columnIndexs=Util.fromToIndex(tr_M-1, tr_M-1);
		double[][] tr_y=Util.getByIndex(tr_d, rowIndexs, columnIndexs);
		
//		double[][] te_d=Util.load("hw3_test.dat");
//		int te_N=Util.size(te_d, true);
//		int te_M=Util.size(te_d, false);
//		
//		int[] rowIndexs_te=Util.fromToIndex(0, te_N-1);
//		int[] columnIndexs_te=Util.fromToIndex(0, te_M-2);
//		double[][] te_X=Util.getByIndex(te_d, rowIndexs_te, columnIndexs_te);
//		columnIndexs_te=Util.fromToIndex(te_M-1, te_M-1);
//		double[][] te_y=Util.getByIndex(te_d, rowIndexs_te, columnIndexs_te);
		
		double[][] thetas=Cart.buildThetas(tr_X);
		
		int expnum = 100;
		
		RadomForest[] rfs = new RadomForest[expnum];
		
		for(int i=0;i<10;i++){
			RFRunner rfRunner = new RFRunner(rfs, i*10, i*10+9, tr_X, tr_y, thetas, 300, tr_N);
			new Thread(rfRunner).start();
		}
		
//		double Ein=0;
//		double Eout=0;
//		for(int i=0;i<expnum;i++){
//			rfs[i] = RadomForest.build(tr_X, tr_y, thetas, 300, tr_N);
//			Ein+=rfs[i].predictErr(tr_X, tr_y);
//			Eout+=rfs[i].predictErr(te_X, te_y);
//		}
		
//		System.out.println(Ein/expnum);
//		System.out.println(Eout/expnum);
		
		while (true)
        {
            if ( Thread.activeCount() == 1 ) break;
        }
		System.out.println((System.currentTimeMillis()-start));
		
	}

}
