package com.duykien.usc.locationentropy.experiments;

public class Experiments {
	public static final String LOG4J_PROPERTIES_FILE = "log4j.properties";
	
	public static final double LN2 = Math.log(2);
	
	public static double maxH(double n, double C) {
		return (C * Math.log(n)) / (n + C);
	}
	
	public static double findN0(int C) {
		double epsilon = 0.01;
		if (C < epsilon) {
			return 0;
		}
		double n0 = 0;
		double maxValue = Double.MIN_VALUE;
		double n = 0;
		while (n <= C) {
			n += epsilon;
			if (maxValue <= maxH(n, C)) {
				n0 = n;
				maxValue = maxH(n, C);
			} 
//			else {
//				break;
//			}
		}
		
		return n0;
	}

	public static void main(String[] args) {
		for (int C = 1; C < 101; C++) {
			double n0 = findN0(C);
//			double n0 = 100;
//			System.out.println(C + "," + n0 + "," + maxH(n0, C));
		}
		
		double step = 0.01;
		for (double n = 1; n <= 50; n += step) {
			System.out.println(n + ", " + maxH(n, 10));
		}
	}

}
