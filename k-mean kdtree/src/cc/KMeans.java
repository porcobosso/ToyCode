package cc;


public class KMeans {
	private double[][] centroids;
	
	private KMeans(){}
	
	public static class UtilZ{
		static double[][] randomCentroids(double[][] data,int k){
			double[][] res = new double[k][];
			for(int i=0;i<k;i++){
				res[i] = data[(int)(Math.random()*data.length)];
			}
			return res;
		}
		
		static boolean converged(double[][] c1,double[][] c2,double c){
			for(int i=0;i<c1.length;i++){
				if(changed(c1[i],c2[i])>c){
					return false;
				}
			}
			return true;
		}
		private static double changed(double[] c1,double[] c2){
			double change=0;
			double total=0;
			for(int i=0;i<c1.length;i++){
				total+=Math.pow(c1[i], 2);
				change+=Math.pow(c1[i]-c2[i], 2);
			}
			return Math.sqrt(change/total);
		}
		
		static double distance(double[] c1,double[] c2){
	    	double sum = 0;
	    	for(int i=0;i<c1.length;i++){
	    		sum+=Math.pow(c1[i]-c2[i], 2);
	    	}
	    	return sum;
	    }
	}
	public static KMeans build(double[][] input,int k,double c,double[][] cs){
		long start = System.currentTimeMillis();
		MRKDTree tree = MRKDTree.build(input);
		System.out.println("treeConstruct:"+(System.currentTimeMillis()-start));
		
		double[][] csnew = tree.updateCentroids(cs);
		while(!UtilZ.converged(cs, csnew, c)){
			cs=csnew;
			csnew=tree.updateCentroids(cs);
		}
		KMeans km = new KMeans();
		km.centroids=csnew;
		return km;
	}
	
	public static KMeans buildOri(double[][] input,int k,double c,double[][] cs){
		
		double[][] csnew = updateOri(input,cs);
		while(!UtilZ.converged(cs, csnew, c)){
			cs=csnew;
			csnew=updateOri(input,cs);
		}
		KMeans km = new KMeans();
		km.centroids=csnew;
		return km;
	}
	
    
    private static double[][] updateOri(double[][] input,double[][] cs){
    	int[] center = new int[input.length];
    	for(int i=0;i<input.length;i++){
    		double dismin = Double.MAX_VALUE;
    		for(int j=0;j<cs.length;j++){
    			double dis = UtilZ.distance(input[i], cs[j]);
    			if(dis<dismin){
    				dismin=dis;
    				center[i]=j;
    			}
    		}
    	}
    	
    	double[][] nct =new double[cs.length][cs[0].length];
    	int[] datacount = new int[cs.length];
    	for(int i=0;i<input.length;i++){
    		double[] n = input[i];
    		int belong = center[i];
    		for(int j=0;j<cs[0].length;j++){
    			nct[belong][j]+=n[j];
    		}
    		datacount[belong]++;
    	}
    	
    	for(int i=0;i<nct.length;i++){
    		for(int j=0;j<nct[0].length;j++){
    			nct[i][j]/=datacount[i];
    		}
    	}
    	return nct;
    }
    
    public void printCentroids(){
    	java.text.DecimalFormat df=new java.text.DecimalFormat("0.00"); 
    	for(int i=0;i<centroids.length;i++){
    		for(int j=0;j<centroids[i].length;j++)
    			System.out.print(df.format(centroids[i][j])+",");
    		System.out.println();
    	}
    }
}
