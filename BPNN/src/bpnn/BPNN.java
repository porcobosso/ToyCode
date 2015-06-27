package bpnn;

import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import util.MMath;
import util.MRandom;

public class BPNN {
	private BlockRealMatrix[] ws;
	
	public BPNN(BlockRealMatrix x,BlockRealMatrix y,double r,double eta,int[] hiddenLayers,int iterations) {
		initWeights(x.getColumnDimension(), hiddenLayers, r);
		gradientDecent(x,y,eta,iterations);
	}
	
	public BlockRealMatrix predict(BlockRealMatrix x){
		int n = this.ws.length;
		BlockRealMatrix tmpMatrix = MMath.addColumn(x,0,1);
		for(int i=0;i<n;i++){
			tmpMatrix = tmpMatrix.multiply(this.ws[i].transpose());
			tmpMatrix = MMath.tanh(tmpMatrix);
			if(i<n-1){
				tmpMatrix = MMath.addColumn(tmpMatrix,0,1);
			}
		}
		return tmpMatrix;
	}
	
	public double predict(BlockRealMatrix x,BlockRealMatrix y){
		BlockRealMatrix pred = predict(x);
		double[][] pd = pred.getData();
		double[][] yd = y.getData();
		int n=pd.length;
		int count=0;
		for(int i=0;i<n;i++){
			if(pd[i][0]>=0&&yd[i][0]==1)
				count++;
			else if(pd[i][0]<0&&yd[i][0]==-1)
				count++;
		}
		return count*1.0/n;
	}
	
	private void gradientDecent(BlockRealMatrix x, BlockRealMatrix y,
			double eta, int iterations) {
		int n = x.getRowDimension();
		for(int i=0;i<iterations;i++){
			int j=(int)(Math.random()*n);
			BlockRealMatrix xi=x.getRowMatrix(j);
			BlockRealMatrix yi = y.getRowMatrix(j);
			decent(xi,yi,eta);
		}
		
	}

	private void decent(BlockRealMatrix xi, BlockRealMatrix yi, double eta) {
		int n = this.ws.length;
		BlockRealMatrix[] ts = new BlockRealMatrix[n];
		BlockRealMatrix[] ttanh = new BlockRealMatrix[n];
		
		BlockRealMatrix tmpMatrix = MMath.addColumn(xi,0,1);
		ttanh[0]=tmpMatrix;
		for(int i=0;i<n;i++){
			tmpMatrix = tmpMatrix.multiply(this.ws[i].transpose());
			ts[i] = tmpMatrix;
			tmpMatrix = MMath.tanh(tmpMatrix);
			if(i<n-1){
				tmpMatrix = MMath.addColumn(tmpMatrix,0,1);
				ttanh[i+1] = tmpMatrix;
			}
		}
		
		RealMatrix[] substractMatrixs = new RealMatrix[n];
		RealMatrix prefail = yi.subtract(tmpMatrix).multiply(MMath.dtanh(ts[n-1])).scalarMultiply(-2);
		substractMatrixs[n-1]= prefail.transpose().multiply(ttanh[n-1]).scalarMultiply(eta);
		
		for(int i=n-2;i>=0;i--){
			BlockRealMatrix noOnew=ws[i+1].getSubMatrix(0, ws[i+1].getRowDimension()-1, 1, ws[i+1].getColumnDimension()-1);
			prefail = MMath.mapMultiply(prefail.multiply(noOnew), MMath.dtanh(ts[i]));
			substractMatrixs[i]= prefail.transpose().multiply(ttanh[i]).scalarMultiply(eta);
		}
		
		for(int i=0;i<n;i++){
			this.ws[i]=this.ws[i].subtract(substractMatrixs[i]);
		}
	}

	private void initWeights(int featureSize,int[] hiddenLayers,double r){
		int hLayerSize=hiddenLayers.length;
		this.ws = new BlockRealMatrix[hLayerSize+1];
		
		for(int i=0;i<hLayerSize;i++){
			int n = hiddenLayers[i];
			this.ws[i]=MRandom.randUniformMtrix(-r, r, n, featureSize);
			featureSize=n;
		}
		this.ws[hLayerSize]=MRandom.randUniformMtrix(-r, r, 1, featureSize);
	}
}
