package com.duykien.usc.GIS.DP;

public class DPUtil {
	public static final double PRECISION = 1e-15;
	
	public static boolean isDoubleEqual(double a, double b) {
		return Math.abs(a - b) < PRECISION;
	}
	
	/**
	 * Get a string representation of a double epsilon value
	 * @param eps
	 * @return the string representation of a double epsilon value or "ERROR" if error occurred
	 */
	public static String toEpsilonString(double eps) {
		if (isDoubleEqual(eps, 0.01)) 
			return "0.01";
		if (isDoubleEqual(eps, 0.02)) 
			return "0.02";
		if (isDoubleEqual(eps, 0.05)) 
			return "0.05";
		if (isDoubleEqual(eps, 0.1)) 
			return "0.1";
		if (isDoubleEqual(eps, 0.2)) 
			return "0.2";
		if (isDoubleEqual(eps, 0.25)) 
			return "0.25";
		if (isDoubleEqual(eps, 0.5)) 
			return "0.5";
		if (isDoubleEqual(eps, 1)) 
			return "1";
		if (isDoubleEqual(eps, 2)) 
			return "2";
		if (isDoubleEqual(eps, 2.5)) 
			return "2.5";
		if (isDoubleEqual(eps, 5)) 
			return "5";
		if (isDoubleEqual(eps, 10)) 
			return "10";
		
		return "ERROR";
	}
}
