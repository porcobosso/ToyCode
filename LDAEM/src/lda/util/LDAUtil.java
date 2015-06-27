package lda.util;

import org.apache.commons.math3.special.Gamma;

/**
 * 1.���ڼ���digamma������digammaһ�ε���
 *   psi:digamma����
 *   
 * @author wangjie
 */
public class LDAUtil {
	/**
	 * �ж��Ƿ�����
	 * @param a ����a
	 * @param b ����b
	 * @param l ��ֵ
	 * @return �Ƿ�����
	 */
	public static boolean converged(double[] a,double[] b,double l){
		double lenA=0;
		double diffAB=0;
		for(int i=0;i<a.length;i++){
			lenA+=Math.pow(a[i],2);
			diffAB+=Math.pow(a[i]-b[i], 2);
		}
		
		return Math.sqrt(diffAB/lenA)<l;
	}
	
	public static void normalize(double[] a){
		double sum = 0;
		for(double i : a)
			sum += i;
		for(int i=0;i<a.length;i++){
			a[i]/=sum;
		}
	}

	public static double psi(double d, int i) {
		if(i==0)
			return Gamma.digamma(d);
		else 
			return Gamma.trigamma(d);
	}
}
