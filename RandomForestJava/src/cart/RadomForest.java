package cart;

import util.Util;

public class RadomForest {
	private Cart[] trees;
	
	private RadomForest(){}
	
	public static RadomForest build(double[][] x,double[][] y,double[][] thetas,int treenums,int bootstrpnum){
		RadomForest rf=new RadomForest();
		rf.trees=new Cart[treenums];
		
		int n = Util.size(x, true);
		int m = Util.size(x, false);
		int[] cols = Util.fromToIndex(0, m-1);
		int[] rows = Util.fromToIndex(0, bootstrpnum-1);
		for(int i=0;i<treenums;i++){
			int[] bagging = Util.randi(n, bootstrpnum);
			double[][] tmpx = Util.getByIndex(x, bagging, cols);
			double[][] tmpy = Util.getByIndex(y, bagging, new int[]{0});
			
			rf.trees[i] = Cart.build(tmpx, tmpy, thetas,rows);
		}
		
		return rf;
	}
	
	public static RadomForest build(double[][] x,double[][] y,int treenums,int bootstrpnum){
		double[][] thetas=Cart.buildThetas(x);
		return build(x, y, thetas, treenums, bootstrpnum);
	}
	
	public double predictErr(double[][] x,double[][] y){
		int n = Util.size(y, true);
		int err = 0;
		for(int i=0;i<n;i++){
			err+=predict(x[i])!=y[i][0]? 1:0;
		}
		return err*1.0/n;
	}
	
	public double predict(double[] x){
		double predict=0;
		for(int i=0;i<trees.length;i++){
			predict+=trees[i].predict(x);
		}
		
		return predict>=0? 1.0:-1.0;
	}
}
