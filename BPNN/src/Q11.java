import org.apache.commons.math3.linear.BlockRealMatrix;

import bpnn.BPNN;

import util.Util;


public class Q11 {

	public static void main(String[] args) throws Exception {
		long start = System.currentTimeMillis();
		Util.setPath("A:/study/...");
		double[][] tr_d=Util.load("hw4_nnet_train.dat");
		BlockRealMatrix trdMatrix=new BlockRealMatrix(tr_d);
		BlockRealMatrix tr_x=trdMatrix.getSubMatrix(0, trdMatrix.getRowDimension()-1, 0, trdMatrix.getColumnDimension()-2);
		BlockRealMatrix tr_y=trdMatrix.getColumnMatrix(trdMatrix.getColumnDimension()-1);
		
		double[][] te_d=Util.load("hw4_nnet_test.dat");
		BlockRealMatrix tedMatrix=new BlockRealMatrix(te_d);
		BlockRealMatrix te_x=tedMatrix.getSubMatrix(0, tedMatrix.getRowDimension()-1, 0, tedMatrix.getColumnDimension()-2);
		BlockRealMatrix te_y=tedMatrix.getColumnMatrix(tedMatrix.getColumnDimension()-1);
		
		double estimate=0.0;
		for(int i=0;i<500;i++){
			BPNN bpnn = new BPNN(tr_x, tr_y, 0.1, 0.01, new int[]{8,3}, 50000);
			estimate+=bpnn.predict(te_x, te_y);
		}
		System.out.println(estimate/500);
		System.out.println((System.currentTimeMillis()-start)/1000);
	}
}
