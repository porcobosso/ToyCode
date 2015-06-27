package cart;

public class RFRunner implements Runnable {
	
	private int start;
	private int end;
	private RadomForest[] rfs;
	private double[][] x;
	private double[][] y;
	private double[][] thetas;
	private int treenums;
	private int bootstrapnum;
	
	public RFRunner(RadomForest[] rfs,int start,int end,double[][] x,double[][] y,double[][] thetas,int treenums,int bootstrpnum) {
		this.rfs=rfs;
		this.start=start;
		this.end=end;
		this.x=x;
		this.y=y;
		this.thetas=thetas;
		this.treenums=treenums;
		this.bootstrapnum=bootstrpnum;
	}
	
	public void run() {
		for(int i=start;i<=end;i++){
			rfs[i]=RadomForest.build(x, y, thetas, treenums, bootstrapnum);
		}
	}

}
