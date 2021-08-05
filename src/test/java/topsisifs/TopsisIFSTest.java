package topsisifs;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;

public class TopsisIFSTest {

	@Test public void test() {

		Criteria c1 = new Criteria("C1", 0.2);
		Criteria c2 = new Criteria("C2", 0.3);
		Criteria c3 = new Criteria("C3", 0.2);
		Criteria c4 = new Criteria("C4", 0.3);

		Alternative a1 = new Alternative("A1");
		a1.addInitialValue(new IFSInitialValue(c1, 0.8, 0.1));
		a1.addInitialValue(new IFSInitialValue(c2, 0.7, 0.1));
		a1.addInitialValue(new IFSInitialValue(c3, 0.7, 0));
		a1.addInitialValue(new IFSInitialValue(c4, 0.4, 0.3));

		Alternative a2 = new Alternative("A2");
		a2.addInitialValue(new IFSInitialValue(c1, 0.6, 0.3));
		a2.addInitialValue(new IFSInitialValue(c2, 0.6, 0.1));
		a2.addInitialValue(new IFSInitialValue(c3, 0.8, 0.1));
		a2.addInitialValue(new IFSInitialValue(c4, 0.7, 0.2));

		Alternative a3 = new Alternative("A3");
		a3.addInitialValue(new IFSInitialValue(c1, 0.3, 0.5));
		a3.addInitialValue(new IFSInitialValue(c2, 0.8, 0.1));
		a3.addInitialValue(new IFSInitialValue(c3, 0.5, 0.3));
		a3.addInitialValue(new IFSInitialValue(c4, 0.6, 0.3));

		Alternative a4 = new Alternative("A4");
		a4.addInitialValue(new IFSInitialValue(c1, 0.7, 0.1));
		a4.addInitialValue(new IFSInitialValue(c2, 0.2, 0.5));
		a4.addInitialValue(new IFSInitialValue(c3, 0.5, 0.3));
		a4.addInitialValue(new IFSInitialValue(c4, 0.8, 0.1));

		Alternative a5 = new Alternative("A5");
		a5.addInitialValue(new IFSInitialValue(c1, 0.5, 0.2));
		a5.addInitialValue(new IFSInitialValue(c2, 0.3, 0.6));
		a5.addInitialValue(new IFSInitialValue(c3, 0.4, 0.2));
		a5.addInitialValue(new IFSInitialValue(c4, 0.9, 0));

		Alternative[] alternatives = new Alternative[] {
				a1,a2,a3,a4,a5
		};

		var expectedResult = new double[] {
				0.6544,
				0.596,
				0.5258,
				0.4985,
				0.4524
		};
		Assert.assertArrayEquals(Arrays.stream(TopsisIFS.getInstance().computeResult(alternatives))
				.map(TopsisIFS.ComputedAlternative::getRelativeCloseness)
				.mapToDouble(BigDecimal::doubleValue)
				.toArray(), expectedResult, 0.1);
	}
}
