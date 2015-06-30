package cc;
import java.io.BufferedReader;
import java.io.FileReader;


public class Test {
	static void compare(double[][] input){
		double[][] cs = KMeans.UtilZ.randomCentroids(input, 20);
		int t=1;
    	long start = System.currentTimeMillis();
    	while(t-->0)
    		KMeans.build(input, 20, 0.001,cs);
    	long kdtree = System.currentTimeMillis()-start;
    	t=1;
    	start = System.currentTimeMillis();
    	while(t-->0)
    		KMeans.buildOri(input, 20, 0.001,cs);
    	long ori = System.currentTimeMillis()-start;
    	
    	System.out.println("kdtree:"+kdtree);
    	System.out.println("linear:"+ori);
    	System.out.println(ori*1.0/kdtree);
	}
	
    public static void main(String[] args) throws Exception{
    	BufferedReader reader = new BufferedReader(new FileReader("d.txt"));
    	String line=null;
    	double[][] input = new double[600000][10];
    	int i=0;
    	while((line=reader.readLine())!=null){
    		String[] numstrs=line.split("\t");
    		for(int j=0;j<10;j++)
    			input[i][j] = Double.parseDouble(numstrs[j]);
    		i++;
    	}
    	
    	compare(input);
    }
}