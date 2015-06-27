package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

public class Util {
	public static final String PATH_STRING="PATHNOW";
	public static final int EQUAL = 0;
	public static final int LESS = 1;
	public static final int GREATEROREQUAL = 2;
	
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
	
	public static double[][] getByIndex(double[][] x,int[] rowIndexs,int[] columnIndexs){
		double[][] y= new double[rowIndexs.length][columnIndexs.length];
		for(int i=0;i<rowIndexs.length;i++){
			for(int j=0;j<columnIndexs.length;j++){
				y[i][j]=x[rowIndexs[i]][columnIndexs[j]];
			}
		}
		return y;
	}
	
	public static int[] fromToIndex(int i,int j){
		int[] indexs=new int[j-i+1];
		int c=0;
		for(;i<=j;i++,c++){
			indexs[c]=i;
		}
		return indexs;
	}
	
	public static int size(double[][] x,boolean row){
		return row? x.length:x[0].length;
	}

	public static int[] randi(int max, int num) {
		int[] rand=new int[num];
		for(int i=0;i<num;i++){
			rand[i]=(int)(Math.random()*max);
		}
		return rand;
	}
	
	public static int[] findLess(double[][] y,double compare,int col, int[] index){
		int size = index.length;
		int[] list = new int[size];
		int num = 0;
		for(int i=0;i<size;i++){
			if(y[index[i]][col]<compare)
				list[num++]=index[i];
		}
		
		int[] finded = new int[num];
		for(int i=0;i<num;i++){
			finded[i]=list[i];
		}
		return finded;
	}
	
	public static int sizeeq(double[][] y,double compare,int col, int[] index){
		int num=0;
		int size = index.length;
		for(int i=0;i<size;i++){
			if(y[index[i]][col]==compare)
				num++;
		}

		return num;
	}
	
	public static double[] maxmin(double[][] x,int col,int[] index){
		double max=x[index[0]][col];
		double min=x[index[0]][col];
		for(int i=1;i<index.length;i++){
			double v =x[index[i]][col];
			if(v>max){
				max=v;
			}else if(v<min){
				min=v;
			}
		}
		
		return new double[]{min,max};
	}
	
	public static void quicksort(double[][] x){
		int size=size(x, true);
		if(size>1)
			sort(x, 0, size-1);
	}
	
	private static void sort(double[][] x,int low,int high){
		if(low<high){
			int middle=middle(x,low,high);
			sort(x, low, middle-1);
			sort(x, middle+1, high);
		}
	}
	
	private static int middle(double[][] x,int low,int high){
		double key = x[low][0];
		while(low<high){
			while(low<high&&key<x[high][0])
				high--;
			
			x[low][0]=x[high][0];
			while(low<high&&key>x[low][0])
				low++;
			x[high][0]=x[low][0];
		}
		x[low][0]=key;
		return low;
	}
	
	public static double major(double[][] y,int col){
		HashMap<Double, Integer> map = new HashMap<Double, Integer>(5);
		int row = Util.size(y, true);
		for(int i=0;i<row;i++){
			double key = y[i][col];
			map.put(key, map.containsKey(key)? map.get(key)+1:1);
		}
		
		double major = 0;
		int max = Integer.MIN_VALUE;
		for(Double key:map.keySet()){
			int num = map.get(key);
			if(num>max){
				major = key;
				max=num;
			}
		}
		
		return major;
	}
	
	public static double sum(double[][] y,int col,int[] index){
		double sum=0;
		int row = index.length;
		for(int i=0;i<row;i++){
			sum += y[index[i]][col];
		}
		
		return sum;
	}

	public static int[] except(int[] index, int[] index1) {
		int n1=index1.length;
		int n = index.length;
		int[] index2 = new int[n-n1];
		
		int j=0;
		int maxnow=index1[j];
		
		int i2=0;
		for(int i=0;i<n;i++){
			if(index[i]<maxnow){
				index2[i2++]=index[i];
			}else {
				maxnow = (++j==n1)? Integer.MAX_VALUE:index1[j];
			}
		}
		                       
		return index2;
	}
}
