package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class Util {
	public static final String PATH_STRING="PATHNOW";
	
	public static void setPath(String path){
		System.setProperty(PATH_STRING,path);
	}
	
	public static double[][] load(String location) throws Exception{
		String path=System.getProperty(PATH_STRING);
		BufferedReader reader = new BufferedReader(new FileReader(path+"/"+location));
		
		String tmp=null;
		boolean widthGet=false;
		int width=0;
		int height=0;
		
		ArrayList<double[]> list=new ArrayList<double[]>();
		while((tmp=reader.readLine())!=null){
			String[] nums=tmp.trim().split("\\s");
			if(!widthGet){
				width=nums.length;
			}
			
			double[] row=new double[width];
			for(int i=0;i<nums.length;i++){
				row[i]=Double.parseDouble(nums[i]);
			}
			list.add(row);
			height++;
		}
		
		double[][] data=new double[height][width];
		for(int i=0;i<height;i++){
			double[] row=list.get(i);
			
			for(int j=0;j<width;j++){
				data[i][j]=row[j];
			}
		}
		return data;
	}
}
