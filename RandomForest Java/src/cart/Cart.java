package cart;

import util.Util;

public class Cart {
	public int feature;
	public double theta;
	public int size = 0;
	public boolean isLeaf;
	public double prediction;
	public Cart leftNode;
	public Cart rightNode;
	
	private Cart(){}
	
	public static Cart build(double[][] x,double[][] y,double[][] thetas,int[] index){
		int eq1=Util.sizeeq(y, 1.0, 0,index);
		int n=index.length;
		
		Cart cart = new Cart();
		cart.isLeaf=false;
		if(eq1==0){
			cart.isLeaf=true;
			cart.prediction=-1.0;
		}else if (eq1==n) {
			cart.isLeaf=true;
			cart.prediction=1;
		}
		
		if(cart.isLeaf) return cart;
		
		cart.isLeaf=true;
		int m = Util.size(x, false);
		for(int i=0;i<m;i++){
			int eq2 = Util.sizeeq(x, x[index[0]][i], i,index);
			if(eq2!=0||eq2!=n){
				cart.isLeaf=false;
				break;
			}
		}
		
		if(cart.isLeaf){
			cart.prediction=Util.sum(y, 0, index)>=0? 1.0:-1.0;
			return cart;
		}
		
		double[] map = selectFeature(x, y, thetas,index);
		cart.feature=(int)map[0];
		cart.theta=map[1];
		
		int[] index1=Util.findLess(x,  cart.theta, cart.feature, index);
		int[] index2=Util.except(index, index1);
		
		cart.leftNode=build(x,y, thetas,index1);
		cart.rightNode=build(x,y, thetas,index2);
		cart.size = 1+cart.leftNode.size+cart.rightNode.size;
		return cart;
	}
	
	public double predictErr(double[][] x,double[][] y){
		int n = Util.size(x, true);
		
		int err=0;
		for(int i=0;i<n;i++){
			if(predict(x[i])!=y[i][0]){
				err++;
			}
		}
		
		return err*1.0/n;
	}
	
	public double predict(double[] x){
		Cart cart = this;
		while(!cart.isLeaf){
			cart=x[cart.feature]<cart.theta? cart.leftNode:cart.rightNode;
		}
		
		return cart.prediction;
	}
	
	public static double[][] buildThetas(double[][] x){
		int row=Util.size(x, true);
		int col=Util.size(x, false);
		
		int[] rowIndex=Util.fromToIndex(0, row-1);
		double[][] thetas=new double[row-1][col];
		for(int j=0;j<col;j++){
			double[][] tmpx = Util.getByIndex(x, rowIndex, new int[]{j});
			Util.quicksort(tmpx);
			
			for(int i=0;i<row-1;i++){
				thetas[i][j]=(tmpx[i][0]+tmpx[i+1][0])/2.0;
			}
		}
		
		return thetas;
	}
	
	private static  double[] selectFeature(double[][] x,double[][] y,double[][] thetas,int[] index){
		int row=Util.size(thetas, true);
		int col=Util.size(thetas, false);
		
		int featuresel=0;
		double ginimin=Double.MAX_VALUE;
		double thetasel=0;
		
		double tmptehta=0;
		for(int i=0;i<col;i++){
			double[] maxmin=Util.maxmin(x, i,index);
			
			for(int j=0;j<row;j++){
				tmptehta=thetas[j][i];
				if(tmptehta>maxmin[1]){
					break;
				}else if (tmptehta<=maxmin[0]) {
					continue;
				}
				
				double gini= gini(x, y, tmptehta, i,index);
				
				if(gini<ginimin){
					ginimin=gini;
					featuresel=i;
					thetasel=tmptehta;
				}
			}
		}
		double[] map = new double[]{featuresel,thetasel};
		 
		return map;
	}
	
	public static double gini(double[][] x,double[][] y,double theta,int col,int[] index){
		int n = index.length;
		
		int y1=0;
		int y1c=0;
		int y2c=0;
		for(int i=0;i<n;i++){
			if(x[index[i]][col]<theta){
				y1++;
				if(y[index[i]][0]==-1.0){
					y1c++;
				}
			}else if(y[index[i]][0]==-1.0){
					y2c++;
			}
		}
		double c1y1=y1c*1.0/y1;
		double c2y1=1-c1y1;
		
		int y2=n-y1;
		double c1y2=y2c*1.0/y2;
		double c2y2=1-c1y2;
		
		return (y1*(1-c1y1*c1y1-c2y1*c2y1)+y2*(1-c1y2*c1y2-c2y2*c2y2));
	}
}
