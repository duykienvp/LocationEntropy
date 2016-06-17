package com.duykien.usc.GIS.measure;

import java.util.ArrayList;


/**
 * Calculate KL_divergence {@link https://en.wikipedia.org/wiki/Kullback%E2%80%93Leibler_divergence}: 
 * 
 * it is the amount of information lost when Q is used to approximate P
 * @author kiennd
 *
 */
public class KLDivergenceCalculator {
	
	/**
	 * Returns the KL divergence, K(p1 || p2).
	 *
	 * The log is w.r.t. base e.
	 * <p>
	 *
	 * *Note*: If any value in <tt>p2</tt> is <tt>0.0</tt> then the
	 * KL-divergence is <tt>infinite</tt>. Limin changes it to zero instead of
	 * infinite.
	 * 
	 */
	public static double klDivergence(ArrayList<Integer> l1, ArrayList<Integer> l2) {
		double[] p1 = Util.convertToProbabilityArray(Util.convertIntListToDoubleArray(l1));
		double[] p2 = Util.convertToProbabilityArray(Util.convertIntListToDoubleArray(l2));
		
		return klDivergence(p1, p2);
	}
	

	/**
	 * Returns the KL divergence, K(p1 || p2).
	 *
	 * The log is w.r.t. base e.
	 * <p>
	 *
	 * *Note*: If any value in <tt>p2</tt> is <tt>0.0</tt> then the
	 * KL-divergence is <tt>infinite</tt>. Limin changes it to zero instead of
	 * infinite.
	 * 
	 */
	public static double klDivergence(int[] c1, int[] c2) {
		double[] p1 = Util.convertToProbabilityArray(Util.convertIntArrayToDoubleArray(c1));
		double[] p2 = Util.convertToProbabilityArray(Util.convertIntArrayToDoubleArray(c2));

		return klDivergence(p1, p2); // moved this division out of the loop -DM
	}

	/**
	 * Returns the KL divergence, K(p1 || p2).
	 *
	 * The log is w.r.t. base e.
	 * <p>
	 *
	 * *Note*: If any value in <tt>p2</tt> is <tt>0.0</tt> then the
	 * KL-divergence is <tt>infinite</tt>. Limin changes it to zero instead of
	 * infinite.
	 * 
	 */
	public static double klDivergence(double[] p1, double[] p2) {

		double klDiv = 0.0;

		for (int i = 0; i < p1.length; ++i) {
			if (p1[i] < Util.PRECISION) {
				continue;
			}
			if (p2[i] < Util.PRECISION) {
				continue;
			} // Limin

			klDiv += p1[i] * Math.log(p1[i] / p2[i]);
		}

		return klDiv; // moved this division out of the loop -DM
	}
}
