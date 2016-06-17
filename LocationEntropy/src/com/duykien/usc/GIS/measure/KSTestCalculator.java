package com.duykien.usc.GIS.measure;

import java.util.ArrayList;

/**
 * Calculate the Kolmogorov–Smirnov test (https://en.m.wikipedia.org/wiki/Kolmogorov%E2%80%93Smirnov_test)
 * @author kiennd
 *
 */
public class KSTestCalculator {


	/**
	 * Calculate the Kolmogorov–Smirnov test of 2 EQUAL-SIZE CDFs
	 * @param cdf1
	 * @param cdf2
	 * @return  Kolmogorov–Smirnov test of 2 EQUAL-SIZE CDFs or -1 if errors occurred
	 */
	public static double calKolmogorovSmirnovTest(double[] cdf1, double[] cdf2) {
		if (cdf1.length != cdf2.length)
			return -1;
		
		double ksTest = Double.MIN_VALUE;
		for (int i = 0; i < cdf1.length; i++) {
			ksTest = Math.max(ksTest, Math.abs(cdf1[i] - cdf2[i]));
		}
		
		return ksTest;
	}
	
	/**
	 * Calculate the Kolmogorov–Smirnov test of 2 EQUAL-SIZE CDFs
	 * @param cdf1
	 * @param cdf2
	 * @return  Kolmogorov–Smirnov test of 2 EQUAL-SIZE CDFs or -1 if errors occurred
	 */
	public static double calKolmogorovSmirnovTest(ArrayList<Double> l1, ArrayList<Double> l2) {
		double[] p1 = Util.convertToProbabilityArray(Util.convertDoubleListToDoubleArray(l1));
		double[] p2 = Util.convertToProbabilityArray(Util.convertDoubleListToDoubleArray(l2));
		
		return calKolmogorovSmirnovTest(p1, p2);
	}
}
