package lda.bean;
import java.util.HashMap;


public class Document {
	
	private int[][] wordCount;
	private int len;
	
	public Document(String[] words,Vocabulary voc) {
		this.len = words.length;
		HashMap<Integer,Integer> wordtmp = new HashMap<Integer,Integer>(words.length);
		for(int i=0;i<words.length;i++){
			int index = voc.getIndex(words[i]);
			wordtmp.put(index, wordtmp.containsKey(index)?
					wordtmp.get(index)+1:1);
		}
		wordCount = new int[wordtmp.size()][2];
		int i = 0;
		for(int key : wordtmp.keySet()){
			wordCount[i][0]=key;
			wordCount[i++][1]=wordtmp.get(key);
		}
	}

	public int len() {
		return len;
	}
	
	public int[] getWord(int index) {
		return wordCount[index];
	}
	
	public static void main(String[] args) {
		double[][] a=new double[2][2];
		a[0][0]+=2;
		System.out.println(a[0][0]);
	}

	public int uniwordLen() {
		return wordCount.length;
	}
}
