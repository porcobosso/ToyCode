package cc;
import java.util.ArrayList;
import java.util.HashMap;

public class MRKDTree {
	
	private Node mrkdtree;
	
	private class Node{
		//�ָ��ά��
		int partitionDimention;
		//�ָ��ֵ
		double partitionValue;
		//���Ϊ��Ҷ�ӽڵ㣬������Ϊ��
		//����Ϊ����
		double[] value;
		//�Ƿ�ΪҶ��
		boolean isLeaf=false;
		//����
		Node left;
		//����
		Node right;
		//ÿ��ά�ȵ���Сֵ
		double[] min;
		//ÿ��ά�ȵ����ֵ
		double[] max;
		
		double[] sumOfPoints;
		int n;
	}
	
	private static class UtilZ{
		/**
		 * �������ά�ȵķ���
		 * @param data ����
		 * @param dimention ά��
		 * @return ����
		 */
		static double variance(ArrayList<double[]> data,int dimention){
			double vsum = 0;
			double sum = 0;
			for(double[] d:data){
				sum+=d[dimention];
				vsum+=d[dimention]*d[dimention];
			}
			int n = data.size();
			return vsum/n-Math.pow(sum/n, 2);
		}
		/**
		 * ȡ�������м�λ����ֵ
		 * @param data ����
		 * @param dimention ά��
		 * @return
		 */
		static double median(ArrayList<double[]> data,int dimention){
			double[] d =new double[data.size()];
			int i=0;
			for(double[] k:data){
				d[i++]=k[dimention];
			}
			return median(d);
		}
		
		private static double median(double[] a){
			int n=a.length;
			int L = 0;
			int R = n - 1;
			int k = n / 2;
			int i;
			int j;
			while (L < R) {
				double x = a[k];
				i = L;
				j = R;
				do {
					while (a[i] < x)
						i++;
					while (x < a[j])
						j--;
					if (i <= j) {
						double t = a[i];
						a[i] = a[j];
						a[j] = t;
						i++;
						j--;
					}
				} while (i <= j);
				if (j < k)
					L = i;
				if (k < i)
					R = j;
			}
			return a[k];
		}
		
		static double[][] maxmin(ArrayList<double[]> data,int dimentions){
			double[][] mm = new double[2][dimentions];
			//��ʼ�� ��һ��Ϊmin���ڶ���Ϊmax
			for(int i=0;i<dimentions;i++){
				mm[0][i]=mm[1][i]=data.get(0)[i];
				for(int j=1;j<data.size();j++){
					double[] d = data.get(j);
					if(d[i]<mm[0][i]){
						mm[0][i]=d[i];
					}else if(d[i]>mm[1][i]){
						mm[1][i]=d[i];
					}
				}
			}
			return mm;
		}
		
		static double distance(double[] a,double[] b){
			double sum = 0;
			for(int i=0;i<a.length;i++){
				sum+=Math.pow(a[i]-b[i], 2);
			}
			return sum;
		}
		
		/**
		 * ��max��min��ʾ�ĳ������еĵ�͵�a����С����
		 * @param a ��a
		 * @param max �����θ���ά�ȵ����ֵ
		 * @param min �����θ���ά�ȵ���Сֵ
		 * @return �������еĵ�͵�a����С����
		 */
		static double mindistance(double[] a,double[] max,double[] min){
			double sum = 0;
			for(int i=0;i<a.length;i++){
				if(a[i]>max[i])
					sum += Math.pow(a[i]-max[i], 2);
				else if (a[i]<min[i]) {
					sum += Math.pow(min[i]-a[i], 2);
				}
			}
			
			return sum;
		}
		
		public static double[] sumOfPoints(ArrayList<double[]> data,
				int dimentions) {
			double[] res = new double[dimentions];
			for(double[] d:data){
				for(int i=0;i<dimentions;i++){
					res[i]+=d[i];
				}
			}
			return res;
		}
		/**
		 * �ж�centerd�Ƿ���h������c
		 * @param centerd
		 * @param c
		 * @param max
		 * @param min
		 * @return
		 */
		public static boolean isOver(double[] center, double[] c,
				double[] max, double[] min) {
			double discenter = 0;
			double disc = 0;
			for(int i=0;i<c.length;i++){
				if(c[i]-center[i]>0){
					disc+=Math.pow(max[i]-c[i],2);
					discenter+=Math.pow(max[i]-center[i],2);
				}else if(c[i]-center[i]<0) {
					disc+=Math.pow(min[i]-c[i],2);
					discenter+=Math.pow(min[i]-center[i],2);
				}
				
			}
			return discenter<disc;
		}
	}
	
	private MRKDTree() {}
	/**
	 * ������
	 * @param input ����
	 * @return KDTree��
	 */
	public static MRKDTree build(double[][] input){
		int n = input.length;
		int m = input[0].length;
		
		ArrayList<double[]> data =new ArrayList<double[]>(n);
		for(int i=0;i<n;i++){
			double[] d = new double[m];
			for(int j=0;j<m;j++)
				d[j]=input[i][j];
			data.add(d);
		}
		
		MRKDTree tree = new MRKDTree();
		tree.mrkdtree = tree.new Node();
		tree.buildDetail(tree.mrkdtree, data, m,0);
		
		return tree;
	}
	/**
	 * ѭ��������
	 * @param node �ڵ�
	 * @param data ����
	 * @param dimentions ���ݵ�ά��
	 */
	private void buildDetail(Node node,ArrayList<double[]> data,int dimentions,int lv){
		if(data.size()==1){
			node.isLeaf=true;
			node.value=data.get(0);
			return;
		}
		
		//ѡ�񷽲�����ά��
		/*
		node.partitionDimention=-1;
		double var = -1;
		double tmpvar;
		for(int i=0;i<dimentions;i++){
			tmpvar=UtilZ.variance(data, i);
			if (tmpvar>var){
				var = tmpvar;
				node.partitionDimention = i;
			}
		}
		//�������=0����ʾ�������ݶ���ͬ���ж�ΪҶ�ӽڵ�
		if(var<1e-10){
			node.isLeaf=true;
			node.value=data.get(0);
			return;
		}
		*/
		double[][] maxmin=UtilZ.maxmin(data, dimentions);
		
		node.min = maxmin[0];
		node.max = maxmin[1];
		
		//ѡȡ������ά�ȣ�����Ҫ�ܳ�ʱ��
		//�ĳ�ʹ��ѡȡ���ݷ�Χ����ά��
		//��������kdtree���ٶȻ��죬������kmean�������ĵ�����
		boolean isleaf = true;
		for(int i=0;i<node.min.length;i++)
			if(node.min[i]!=node.max[i]){
				isleaf=false;
				break;
			}
		
		if(isleaf){
			node.isLeaf=true;
			node.value=data.get(0);
			return;
		}
		
		node.partitionDimention=-1;
		double diff = -1;
		double tmpdiff;
		for(int i=0;i<dimentions;i++){
			tmpdiff=node.max[i]-node.min[i];
			if (tmpdiff>diff){
				diff = tmpdiff;
				node.partitionDimention = i;
			}
		}
		
		node.sumOfPoints = UtilZ.sumOfPoints(data,dimentions);
		node.n = data.size();
		
		//ѡ��ָ��ֵ
		node.partitionValue=UtilZ.median(data, node.partitionDimention);
		if(node.partitionValue==node.min[node.partitionDimention]){
			node.partitionValue+=1e-5;
		}
		
		int size = (int)(data.size()*0.55);
		ArrayList<double[]> left = new ArrayList<double[]>(size);
		ArrayList<double[]> right = new ArrayList<double[]>(size);
		
		for(double[] d:data){
			if (d[node.partitionDimention]<node.partitionValue) {
				left.add(d);
			}else {
				right.add(d);
			}
		}
		
		Node leftnode = new Node();
		Node rightnode = new Node();
		node.left=leftnode;
		node.right=rightnode;
		buildDetail(leftnode, left, dimentions,lv+1);
		buildDetail(rightnode, right, dimentions,lv+1);
	}
	
	public double[][] updateCentroids(double[][] cs){
		int k = cs.length;
		int m = cs[0].length;
		double[][] entroids = new double[k][m];
		int[] datacount = new int[k];
		HashMap<Integer, double[]> cscopy = new HashMap<Integer, double[]>();
		for(int i=0;i<k;i++)
			cscopy.put(i, cs[i]);
		
		updateCentroidsDetail(mrkdtree,cscopy,entroids,datacount,k,m);
		double[][] csnew = new double[k][m];
		for(int i=0;i<k;i++){
			for(int j=0;j<m;j++){
				csnew[i][j]=entroids[i][j]/datacount[i];
			}
		}
		
		return csnew;
	}
	
	private void updateCentroidsDetail(Node node,
			HashMap<Integer, double[]> cs, double[][] entroids,
			int[] datacount,int k,int m) {
		//�����Ҷ�ӽڵ�
		if(node.isLeaf){
			double[] v=node.value;
			double dis=Double.MAX_VALUE;
			double tdis;
			int index = -1;
			//�ҵ����������ĵ�
			for(Integer i: cs.keySet()){
				double[] c = cs.get(i);
				tdis = UtilZ.distance(c, v);
				if(tdis<dis){
					dis=tdis;
					index=i;
				}
			}
			
			//����ͳ����Ϣ
			datacount[index]++;
			for(int i=0;i<m;i++){
				entroids[index][i]+=v[i];
			}
			return;
		}
		
		double[] stack = new double[k];
		int stackpoint = 0;
		int center=0;
		double tdis;
		for(Integer i: cs.keySet()){
			double[] c = cs.get(i);
			tdis = UtilZ.mindistance(c, node.max, node.min);
			if(stackpoint==0){
				stack[stackpoint++]=tdis;
				center=i;
			}else if (tdis<stack[stackpoint-1]) {
				stackpoint=1;
				stack[0]=tdis;
				center=i;
			}else if (tdis==stack[stackpoint-1]) {
				stack[stackpoint++]=tdis;
			}
			
		}
		//stackpoint>1��˵���ж����Сֵ�����������ĵ�
		if(stackpoint!=1){
			updateCentroidsDetail(node.left, cs, entroids, datacount, k, m);
			updateCentroidsDetail(node.right, cs, entroids, datacount, k, m);
			return;
		}
		
		HashMap<Integer, Boolean> ctover = new HashMap<Integer, Boolean>();
		double[] centerd = cs.get(center);
		for(Integer i: cs.keySet()){
			if(i==center) continue;
			double[] c = cs.get(i);
			if(UtilZ.isOver(centerd,c,node.max,node.min)){
				ctover.put(i, true);
			}
		}
		
		if(ctover.size()==cs.size()-1){
			//��ʱ���ĵ㼴Ϊcenter��������Ϣ
			datacount[center]+=node.n;
			for(int i=0;i<m;i++){
				entroids[center][i]+=node.sumOfPoints[i];
			}
			return;
		}
		
		//�����center������ĵ��ų�
		HashMap<Integer, double[]> csnew = new HashMap<Integer, double[]>();
		for(Integer i:cs.keySet()){
			if(!ctover.containsKey(i))
				csnew.put(i, cs.get(i));
		}
		
		updateCentroidsDetail(node.left, csnew, entroids, datacount, k, m);
		updateCentroidsDetail(node.right, csnew, entroids, datacount, k, m);
	}
}