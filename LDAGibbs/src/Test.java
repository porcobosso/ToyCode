import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class Test {
	public static void main(String[] args) throws Exception{
		predict();
  	}
	
	public static void predict(){
		Cropus cp = Cropus.restore("gibbs1000");
		String docstr = "��ͣ �� 4 �� �� �� ���̹� IPO ��� ���� ��բ �й� ���� �ɷ� ��˾ �׷� �Ѿ� �� �� �ƻ� ���� 120��A �� Ԥ�� ���� ��ģ ���� 400�� Ԫ Ϊ ���� ��� ��ģ �� IPO Զ ���� ���� �ڶ� �� ��ú ��Դ �� 257�� Ԫ ���� ���� �׷��� �� ��Ʊ ��ʼ ���� �� 2007�� ���� ���� �� �¹� �Ѿ� �� 12 �� ���� ��˾ �Ʒ� ���� �й� ̫�� �й� ʯ�� ��ͨ ���� �й� ���� ���� �ɷ� ���� ���� ���� �ɷ� ���� �ɷ� ���� ���� ���� ���� �к� ���� ��ú ��Դ ���� �� 9 ֻ �� �� 6�� 10�� �� ���� ��ô Ͷ���� �Ƿ� �� ���� �¹� �깺 �� ���� �� �� �� ע�� ��Щ ���� �� ���� ��� Ӧ ���� �깺 ���� 1 �ɽ� �嵭 ��ʾ ���� �� ���� ���� �г� ���� ���� �µ� ���� �� �ɽ� ��� �� ���� 1000�� Ԫ ���� �� ˮƽ �� �� �� �׵� �� ��� �� �� �� �� ���� ��ʾ �� Ŀǰ 3000 �� ���� �� ��λ �� �� �� ���� ���� �ʽ� ���� ���� �г� �� ���� �� �� ���� ��Ȼ �� �� �г� �� ���� �� �� Ͷ���� ���� �� ���� �ʽ� ���� �¹� �깺 �� ��� �� ���� ���� ���� ���� �� �� ��Ʊ �� ���� ���� �� ��λ ֮�� ���� �� ���� �»� �� �� A ���� ���� ���� ���� ���� ���м� �� ���� ��С �� һ �� ���� ���� ���� ���� ���м� �� �� 2006�� 8�·� �� �� ���� �� �й� ���� ���� ˵ �깺 �¹� ���� �� ���� �� ���� ���� 2 ���̹� ���� �� �ܼ� ���� �� ��� ��ǩ�� ��ν ��ǩ�� ���� ���� ���� ���� ��Ч �깺 ���� �� ��ǩ �� ���� ���� ���� ��ģ ͨ�� �� ��ǩ�� �� ���� ��ϵ ���� ��ģ Խ �� ��ǩ�� Խ�� �� ���� ���� �׷� ���� ļ�� �ʽ� �� �� �� �� �� �� ��˾ ��ú ��Դ �й� ���� �Ͻ� ��ҵ ���� �ɷ� ļ�� �ʽ� ��ģ �ֱ� Ϊ 256.71�� Ԫ 222.46�� Ԫ 99.82�� Ԫ 89.15�� Ԫ ���� �깺 ��ǩ�� �� �ֱ� Ϊ 0.73% 0.64% 0.37% 0.32% �� ���� 44 �� �� С �� ���� ��˾ ���ʶ� �� �� 16�� Ԫ ���� ��ǩ�� �� �� �� 0.25% ���� 0.20% ���� �� Ϊ 40 �� ���� �� ֻ �� С �� ���� ��˾ ͬ�� ���� Ҳ �� ��� ��ǩ�� ��ǩ�� �� ���� �� ���� Ͷ���� ���� �� �깺 ���� 3 ���̹� ���� �� ���� ���� �ʽ� ���� ���̹� ���� �� ��ǩ�� �� ���� ���� �ʽ� ӻԾ �깺 �� �� �� �г� �ʽ� ���� �� �� �� ���� ���� �Ӷ� �� �� �� �г� ���� ���� һ�� �� ���� Ӱ�� �� �� ��� �� �� �г� Ͷ�� ���� �� ȱʧ ���� ���� �깺 �ʽ� �� ���� �� �� ������ ���� �깺 �ʽ� һ�� �� ���� �� �� �� ���� �ɷ� �� �� ���� ���� �� ���� �ʽ� 2.2���� Ԫ �й� ���� Ϊ 3.1���� Ԫ ��ú ��Դ Ϊ 3.1���� Ԫ �н� �ɷ� ���� ʱ Ҳ �� ���� ���� �ʽ� ��Ȼ �� �� ��ʧ �� �� �г� �� Ͷ�� ���� Ͷ���� ��Ӧ ���� ���� ���̹� �� �׷� �깺 ��ǩ �¹� Ӧ ע�� �� �� ���� Ͷ���� ��ǩ ֮�� �� ��� ���� �¹� �� �е� �� ���� ���� �Ҵ� ���� �е� ����Ϊ �ɱ� �� �� ���� �г��� ���� ��ô ���� ��ǩ ֮�� �� �¹� Ͷ���� �� ���� ���� �� ���� ���� �� 1 ���� �¹� �׷� ��ӯ�� �ӽ� �� �� �г� ƽ�� ˮƽ ���� ���� �׷� ���� �� �¹� ���� ���� �� �� ���� �� �� �г� ���� ̬�� ƫ �� �� Ӱ�� Ҳ �е� �׷� ��ӯ�� ���� �� ���� �� �� δ ���� �ϴ� �� ��� �ռ� �� ���� ���� �� �� �� ��λ ���� ��� ���� �� ��ֵ �ع� ���� ���� �׷� ���� �� 48 ֻ ��Ʊ �� �� �� ��ӯ�� �� 20 �� ���� �� ���� 4 ֻ 30 �� ���� �� ���� 4 ֻ ����� �¹� �׷� ��ӯ�� ���� 20 �� �� 30 �� ֮�� �� Ŀǰ �޳� ���� ��˾ �� A�� ��ӯ�� �� Ϊ 28 �� ���� ���� �¹� Ҫ ���� ļ�� �ʽ� �� �� ��̬ ��ӯ�� �� ���� �¹� ���� ���� ���� �� �� Ӧ �� ���� �� ��� ���� 2 ���� �깺 ���� �� ���� ��Ϊ �� ���� Ŀǰ �¹� ���� �� ��Ҫ ���� �� �� �� �� �� �ⶳ �� �� ���� ��Ϊ һ �� �� �״� ���� ���� ǰ �� ���� �� �ɷ� ���� ���� ��˾ ��� �� �� ���� �ɷ� ת�� ָ�� ��� �� ���� �� ���� �ɷ� �� �ֹ� �ɶ� Ԥ�� δ�� һ �� �� �� ���� ���� ��� �� �� ���� �ɷ� �� ���� ���� �� ��˾ �ɷ� ���� 1% �� Ӧ�� ͨ�� ֤ȯ ������ ���� ���� ϵͳ ת�� ���� �ɷ� �� һ �� �� ���� ���� ���� ���� һ�� ���� �� �� �� �� һ�� Ϊ �� �� �� ս�� Ͷ���� �� �� �� �� һ�� Ϊ ʮ�� �� �� �� ������ ���� �ɷ� �� ���� ���� ���� ���� �� ���� �� ռ ��˾ �� �ɱ� ���� 1% ���� ���� Ͷ���� ���� �¹� �� �� �� �� �ⶳ Ӧ ���� ���� ���� �Ƿ� ���� �ɷ� �� ���� 3 ���� ���� ���� ���� ���� ���� ���� �׷� ���� �� 48 ֻ ��Ʊ �� ���� �� �� �� ������ �� �ۼ� �ǵ��� С�� ���� ���� �ǵ��� �� �� 39 ֻ Ҳ ����˵ �˳ɶ� �� �¹� �� ���� �� һ �� �Ƿ� ���� �� խ ״�� ���� ���� ���� ��� �ϴ� �� �Ƿ� ���� 35 ֻ �¹� �� ���� ���� ��� �� �� �� ���� �� �� �� ������ �� ��� �� 25 ֻ �¹� �� ���� ���� ��� �� �� �� ���� ���� ��� �� ��ô Ͷ���� ��� �� ��ǩ �� �� �¹� �� ���� ���� �� �ߵ� ���� �� �� �� �� ���� �� ���� �� �� �� ʱ�� �� ��� �� �� ���� ���� ָ�� ���� �ɿ� ���� ���� �� ���� �� �� �� �¹� �� 80% ���� ���� ���� ���� �� �� �¸� �� �� �ƴ� Ѷ�� ��� �ɷ� �� Ҳ �� �ϲ� �� ��� һ ζ ���� ¡ �� ���� ���� �� ���� ���� �� �¹� �� 60% ���� Ҳ ���� ���컯 ���� �� �� �¸� �� �к�� �²� ���� �� �� �� �� ��ú ��Դ ���� ���� ���� �Ƿ� �� �� �� �Ƿ� ���� �� ��Ʊ 100% ���� ��� ���� ���� �� �� �� �Ƿ� ��С �� ��Ʊ 50% ���� �� �� �� ���� ���컯 ��չ �ɴ� ���� �ó� ���� Ͷ���� �� Ŀǰ �г� ��� �� Ӧ ���� ���� �¹� �깺 ���� �� ���� ���� ���� �� ��� û�� ̫ �� �� ���� �� ���� �� ��ǩ �� �� �¹� ���� ���� �� ���� ���� �� �� ���� ��Ϊ ���� �� ѡ�� �� ���� ���� �� �ǵ��� �� ������ �� �ж� ���� ���� �� ���� ����  �Ѻ� ֤ȯ ���� �� Ƶ�� ��Ѷ ���� ϵ ת ���� ���� ý�� �� ���� ���� �� ���� �Ѻ� ֤ȯ ���� �۵� �� ���� ���� Ͷ���� �Դ� ��Ѷ ���� �ж� �ݴ� ���� ���� �Ե� ���� �༭ ����Ȼ ";
		Document dc = new Document(docstr.split(" "), cp.voc,true);
		GibbsLDA.predict(dc, cp, 10, 3, 1, 10);
		for(double d : dc.topic){
			int tp = (int) (d * 100);
			if (tp < 10) {
				System.out.print("00" + tp + " ");
			} else if (tp < 100) {
				System.out.print("0" + tp + " ");
			} else {
				System.out.print(tp + " ");
			}
		}
	}
	
	public static void train() throws Exception{
		BufferedReader br = new BufferedReader(new FileReader("news_seg.txt"));
		ArrayList<String> docslist = new ArrayList<String>();
		String tmp = null;
		int count = 0;
		while((tmp=br.readLine())!=null){
			if(tmp.length()<500) continue;
			docslist.add(tmp);
			count++;
			if(count>=10000) break;
		}
		String[] docs = docslist.toArray(new String[docslist.size()]);
		Cropus cp = Cropus.getCropus(docs);
		GibbsLDA.train(cp, 10, 3, 1, 10,false);
		cp.save("gibbs1000");
		cp.result();
	}
}