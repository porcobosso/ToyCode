package util;

import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.linear.BlockRealMatrix;

public class MRandom {
	public static double[][] randUniform(double min,double max,int n,int m) {
		UniformRealDistribution uf = max==min? null:new UniformRealDistribution(min, max);
		double[][] rand=new double[n][m+1];
		for(int i=0;i<n;i++){
			for(int j=0;j<m+1;j++){
				rand[i][j]=uf==null? max:uf.sample();
			}
		}
		return rand;
	}
	
	public static BlockRealMatrix randUniformMtrix(double min,double max,int n,int m) {
		double[][] rand=randUniform(min, max, n, m);
		return new BlockRealMatrix(rand);
	}
}
