package lda.util;

import org.junit.Test;


public class LDAUtilTest {
	@Test
	public void converged(){
		assert LDAUtil.converged(new double[]{1,1,1}, new double[]{1.0001, 1, 1}, 1.0e-3);
	}
	@Test
	public void psi(){
		assert LDAUtil.psi(11, 0)==2.3518;
		assert LDAUtil.psi(11, 1)==0.0952;
	}
}
