package lda.bean;

import java.util.HashMap;

public class Vocabulary {
	private HashMap<String, Integer> voc = new HashMap<String, Integer>(100000);
	private int count=0;
	
	protected int getIndex(String word){
		if(voc.containsKey(word))
			return voc.get(word);
		
		voc.put(word, ++count);
		return count;
	}

	public int len() {
		return count;
	}
}
