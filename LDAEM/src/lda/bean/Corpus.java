package lda.bean;

public class Corpus {
	private Vocabulary voc = new Vocabulary();
	private Document[] documents;
	
	public Corpus() {
	}

	public int getVocLen() {
		return voc.len();
	}

	public int len() {
		return documents.length;
	}

	public Document getDocument(int j) {
		return documents[j];
	}
}
