import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

public class Cropus implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8875296046073898235L;

	private Cropus() {
	};

	Document[] doc;
	HashMap<String, Integer> voc = new HashMap<String, Integer>();
	double[][] weight;

	public static Cropus getCropus(String[] docs) {
		Cropus cp = new Cropus();
		cp.doc = new Document[docs.length];
		cp.voc = new HashMap<String, Integer>();
		for (int i = 0; i < docs.length; i++) {
			String[] words = segment(docs[i]);
			Document doci = new Document(words, cp.voc,false);
			cp.doc[i] = doci;
		}

		return cp;
	}

	private static String[] segment(String str) {
		return str.split(" ");
	}

	public void result() {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter("./res/document_topic.txt"));
			for (int i = 0; i < doc.length; i++) {
				for (double tpc : doc[i].topic) {
					int tp = (int) (tpc * 100);
					if (tp < 10) {
						bw.write("00" + tp + " ");
					} else if (tp < 100) {
						bw.write("0" + tp + " ");
					} else {
						bw.write(tp + " ");
					}
				}
				bw.newLine();
			}
		} catch (Exception e) {
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (Exception e) {
				} finally {
					bw = null;
				}
			}
		}
	}

	public void save(String filestr){
		File f=new File(filestr);
        if(f.exists()){
        	f.delete();
        }
        FileOutputStream os = null;
        ObjectOutputStream oos = null;
	    try{
	    	os = new FileOutputStream(f);
	    	oos =new ObjectOutputStream(os);
	    	oos.writeObject(this);
	    }catch (Exception e) {}
	    finally{
			if(oos !=null){
				try {
					oos.close();
				} catch (Exception e2) {}
			}
			if(os != null){
				try {
					os.close();
				} catch (Exception e2) {}
			}
		}
	}
	
	public static Cropus restore(String filestr){
		InputStream is = null;
		ObjectInputStream ois = null;
		Cropus cp = null;
		try {
			is = new FileInputStream(filestr);
			ois=new ObjectInputStream(is);
			cp = (Cropus)ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return cp;
	}
}
