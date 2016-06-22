package com.duykien.usc.GIS.measure;

public class MSECalculator {
	/**
	 * Calculate Mean Square Error between 2 same-length arrays
	 * @param v1
	 * @param v2
	 * @return MSE or -1 if error occurred
	 */
	public static double calMSE(double[] v1, double[] v2) {
		if (v1.length != v2.length)
			return -1;
		
		double MSE = 0;
		int n = v1.length;
		for (int i = 0; i < n; i++)
			MSE += (v1[i] - v2[i]) * (v1[i] - v2[i]);
		
		MSE /= (double)n;
		
		return MSE;
	}
}
