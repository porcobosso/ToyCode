package wj;

import java.util.HashMap;

public class Cropus {
	private Cropus(){};
	
	Document[] doc;
	HashMap<String, Integer> voc = new HashMap<String, Integer>();
	
	public static Cropus getCropus(String[] docs){
		Cropus cp = new Cropus();
		cp.doc = new Document[docs.length];
		for(int i=0;i<docs.length;i++){
			String[] words = segment(docs[i]);
			HashMap<Integer, Integer> wordcount = new HashMap<Integer, Integer>();
			for(String word : words){
				int ind = -1;
				if(cp.voc.containsKey(word)){
					ind = cp.voc.get(word);
				}else{
					ind = cp.voc.size();
					cp.voc.put(word, ind);
				}
				
				if(wordcount.containsKey(ind)){
					wordcount.put(ind,wordcount.get(ind)+1);
				}else{
					wordcount.put(ind,1);
				}
			}
			Document doci = new Document();
			doci.words = new int[wordcount.size()];
			doci.count = new int[wordcount.size()];
			doci.totalWords = words.length;
			doci.oriText = docs[i];
			cp.doc[i] = doci;
			
			int c=0;
			for(int w:wordcount.keySet()){
				doci.words[c] = w;
				doci.count[c++] = wordcount.get(w);
			}
		}
		
		return cp;
	}

	private static String[] segment(String str) {
		return str.split(" ");
	}
	
}
