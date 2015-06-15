package wj;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PLSA {
	/**
	 * 主题个数
	 */
	private int topics;
	/**
	 * EM算法的最大迭代次数
	 */
	private int maxIter;
	/**
	 * topics*vocabulary 主体个数*字典长度 矩阵
	 */
	private double[][] w;
	/**
	 * document*topics 文档个数*主体个数 矩阵
	 */
	private double[][] z;
	private PLSA(){}
	
	private HashMap<String, Integer> voc = new HashMap<String, Integer>();
	
	/**
	 * @param topics 主体个数
	 * @param maxIter EM算法最大迭代次数
	 */
	public static PLSA train(Cropus cp,int topics,int maxIter){
		Document[] doc = cp.doc;
		HashMap<String, Integer> voc = cp.voc;
		
		PLSA plsa = new PLSA();
		plsa.topics = topics;
		plsa.maxIter = maxIter;
		plsa.voc = voc;
		
		int d = doc.length;
		int m = voc.size();
		
		plsa.initParams(d,m);
		plsa.em(doc,d,m);
		return plsa;
	}

	private void em(Document[] doc, int d, int m) {
		int itr = 0;
		double[][][] pzk = new double[d][][];
		for(int i=0;i<d;i++)
			pzk[i] = new double[doc[i].words.length][topics];
		while(itr++<maxIter){
			for(int i=0;i<d;i++){
				//计算每篇文章每个单词所属主题的后验分布
				int n = pzk[i].length;
				for(int j=0;j<n;j++){
					double sum = 0;
					for(int p=0;p<topics;p++){
						pzk[i][j][p] = z[i][p]*w[p][doc[i].words[j]];
						sum += pzk[i][j][p];
					}
					for(int p=0;p<topics;p++){
						pzk[i][j][p] = sum==0? 0:pzk[i][j][p]/sum;
					}
				}
			}
				
			//清空w(给定主题，选取某单词概率)中数据
			for(int p=0;p<topics;p++){
				for(int i=0;i<m;i++){
					w[p][i] = 0;
				}
			}
			for(int p=0;p<topics;p++){
				double pd = 0;
				for(int i=0;i<d;i++){
					int n = pzk[i].length;
					z[i][p] = 0;//z(每篇文章主题分布)数据清空
					for(int j=0;j<n;j++){
						z[i][p] += doc[i].count[j]*pzk[i][j][p];
						w[p][doc[i].words[j]] += z[i][p];
					}
					pd += z[i][p];
					z[i][p]/=doc[i].totalWords;
				}
				for(int i=0;i<m;i++){
					w[p][i] /= pd;
				}
			}
			
			System.out.println(itr+"/"+maxIter);
		}
	}

	private void initParams(int d, int m) {
		z = new double[d][topics];
		
		int i=0,j=0;
		for(;i<d;++i){
			double sum = 0;
			for(j=0;j<topics;++j){
				z[i][j] = Math.random();
				sum += z[i][j];
			}
			for(j=0;j<topics;++j)
				z[i][j] /= sum;
		}
		
		w = new double[topics][m];
		for(i=0;i<topics;++i){
			double sum = 0;
			for(j=0;j<m;++j){
				w[i][j] = Math.random();
				sum += w[i][j];
			}
			for(j=0;j<m;++j){
				w[i][j] /= sum;
			}
		}
	}
	
	public void print(){
		BufferedWriter bw = null;
		try{
			bw = new BufferedWriter(new FileWriter("res/document_topic.txt"));
			StringBuilder sb = new StringBuilder();
			for(double[] topics:z){
				sb.setLength(0);
				for(double topic:topics){
					sb.append((int)(topic*100)).append('%').append(' ');
				}
				bw.write(sb.toString());
				bw.newLine();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(bw!=null){
				try{
					bw.close();
				}catch (Exception e){}
			}
		}
		try{
			bw = new BufferedWriter(new FileWriter("res/topic_word.txt"));
			StringBuilder sb = new StringBuilder();
			for(double[] ws:w){
				sb.setLength(0);
				
				HashMap<String, Double> wordImp = new HashMap<String, Double>();
				for(String word:this.voc.keySet()){
					wordImp.put(word, ws[this.voc.get(word)]);
				}
				
				List<Map.Entry<String, Double>> list = new LinkedList<Map.Entry<String, Double>>();
				list.addAll(wordImp.entrySet());
				Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
				   public int compare(Map.Entry<String, Double> obj1, Map.Entry<String, Double> obj2) {//从高往低排序
				       
					   if(obj1.getValue()<obj2.getValue())
				           return 1;
				       if(obj1.getValue()==obj2.getValue())
				           return 0;
				       else
				          return -1;
				   }
				});
				
				int k = 0;
				for(Iterator<Map.Entry<String, Double>> ite = list.iterator(); ite.hasNext();) {
				     Map.Entry<String, Double> map = ite.next();
				     if(k>=30) break;
				     sb.append(map.getKey()).append(';');
				}
				
				bw.write(sb.toString());
				bw.newLine();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(bw!=null){
				try{
					bw.close();
				}catch (Exception e){}
			}
		}
	}
}