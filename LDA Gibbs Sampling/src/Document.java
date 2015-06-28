import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;


public class Document implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6328825167841447993L;
	/**
	 * each component stores word index in vocabulary
	 */
	int[] word;
	/**
	 * each word's topic.It will change in every iteration of gibbs sampling
	 */
	int[] wordTopic;
	/**
	 * Topic of this document.It will be computed in the end of gibbs sampling
	 */
	double[] topic;
	
	/**
	 * init document
	 * @param wordstr words
	 * @param voc vocabulary
	 */
	public Document(String[] wordstr,HashMap<String, Integer> voc,boolean predict) {
		word = new int[wordstr.length];
		int escape = 0;
		for(int i=0;i<word.length;i++){
			if(voc.containsKey(wordstr[i])){
				word[i-escape] = voc.get(wordstr[i]);
			}else if(!predict){
				word[i-escape] = voc.size();
				voc.put(wordstr[i], voc.size());
			}else{
				escape++;
			}
		}
		if(escape>0)
			word = Arrays.copyOfRange(word, 0, wordstr.length-escape);
		wordTopic = new int[word.length];
	}
}
