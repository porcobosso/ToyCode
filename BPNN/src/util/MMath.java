package util;

import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

public class MMath {
	
	private static MatrixEachEntryOperator tanhOp = new MatrixEachEntryOperatorImp() {
		
		public double operate(double d) {
			return Math.tanh(d);
		}
	};
	private static MatrixEachEntryOperator dtanhOp = new MatrixEachEntryOperatorImp() {
		
		public double operate(double d) {
			return 4/Math.pow(Math.exp(d)+Math.exp(-d), 2);
		}
	};
	
	private static MatrixEachEntryOperator multiplyOp = new MatrixEachEntryOperatorImp() {
		
		public double operate(double d,double...params) {
			return d*params[0];
		}
	};
	
	public static BlockRealMatrix operateMatrix(BlockRealMatrix s,MatrixEachEntryOperator op){
		double[][] data = s.getData();
		int n = data.length;
		int m = data[0].length;
		
		for(int i=0;i<n;i++){
			for(int j=0;j<m;j++){
				data[i][j] = op.operate(data[i][j]);
			}
		}
		
		return new BlockRealMatrix(data);
	}
	
	public static BlockRealMatrix operateMatrixWithParam(BlockRealMatrix s,MatrixEachEntryOperator op,double...params){
		double[][] data = s.getData();
		int n = data.length;
		int m = data[0].length;
		
		for(int i=0;i<n;i++){
			for(int j=0;j<m;j++){
				data[i][j] = op.operate(data[i][j],params);
			}
		}
		
		return new BlockRealMatrix(data);
	}
	
	public static BlockRealMatrix tanh(BlockRealMatrix s) {
		return operateMatrix(s, tanhOp);
	}
	
	public static BlockRealMatrix multipy(BlockRealMatrix s,double k) {
		return operateMatrixWithParam(s, multiplyOp,k);
	}
	
	public static BlockRealMatrix dtanh(BlockRealMatrix s) {
		return operateMatrix(s, dtanhOp);
	}

	public static BlockRealMatrix addColumn(BlockRealMatrix xi, int index,double num) {
		double[][] data = xi.getData();
		int n = data.length;
		int m = data[0].length;
		
		double[][] trans = new double[n][m+1];
		for(int i=0;i<n;i++){
			int j=0;
			for(;j<index;j++){
				trans[i][j]=data[i][j];
			}
			trans[i][j]=num;
			for(;j<m;j++){
				trans[i][j+1]=data[i][j];
			}
		}
		
		return new BlockRealMatrix(trans);
	}
	
	public static RealMatrix mapMultiply(RealMatrix a,RealMatrix b){
		double[][] ad=a.getData();
		double[][] bd=b.getData();
		int n=ad.length;
		int m=ad[0].length;
		
		for(int i=0;i<n;i++){
			for(int j=0;j<m;j++){
				ad[i][j]*=bd[i][j];
			}
		}
		
		return new BlockRealMatrix(ad);
	}
	
	public static void main(String[] args) {
		int[] tj = new int[11];
		for(int i=0;i<1000;i++)
			tj[(int)(Math.random()*10)]++;
		for(int i=0;i<tj.length;i++){
			System.out.println(tj[i]);
		}
	}
}
