package com.duykien.usc.locationentropy.experiments;

import org.apache.log4j.PropertyConfigurator;

import com.duykien.usc.EBM.EBM;
import com.duykien.usc.EBM.calculator.LocationEntropyCalculator;

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
		PropertyConfigurator.configure(LOG4J_PROPERTIES_FILE);
		String reindexedCheckinsFile = EBM.GOWALLA_DATA_DIR + "loc-gowalla_EBM_totalCheckins_converted_to_index.txt";
		String locationEntropyFilePrefix = EBM.GOWALLA_DATA_DIR + "gowalla_location_entropy";
		int m = Integer.MAX_VALUE;
		int c = Integer.MAX_VALUE;
		String locationEntropyFile = locationEntropyFilePrefix + "_" + m + "_" + c + ".txt";
//		LocationEntropyCalculator.calculateLocationEntropy(reindexedCheckinsFile, locationEntropyFile, m, c);
		
		for (m = 30; m < 51; m++) {
			for (c = 50; c < 51; c++) {
				System.out.println("m = " + m + ",\t" + c); 
				locationEntropyFile = locationEntropyFilePrefix + "_" + m + "_" + c + ".txt";
				LocationEntropyCalculator.calculateLocationEntropy(reindexedCheckinsFile, locationEntropyFile, m, c);
			}
		}
		
//		for (int C = 1; C < 101; C++) {
//			double n0 = findN0(C);
//			double n0 = 100;
//			System.out.println(C + "," + n0 + "," + maxH(n0, C));
//		}
		
//		double step = 0.01;
//		for (double n = 1; n <= 50; n += step) {
//			System.out.println(n + ", " + maxH(n, 10));
//		}
	}

}
