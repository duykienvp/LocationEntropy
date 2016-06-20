package com.duykien.usc.GIS.sensitivitycalculator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

import com.duykien.usc.GIS.Constants;
import com.duykien.usc.GIS.FileNameUtil;

/**
 * Calculate smooth sensitivity of location entropy when a user is added or removed
 * @author kiennd
 *
 */
public class SmoothSensitivityCalculator {

	/**
	 * Calculate smooth sensitivity of location entropy when a user is added or removed 
	 * when we use the 2nd method of calibrating noise using smooth sensitivity
	 * @param c maximum number of visits of a user
	 * @param n number of users 
	 * @param eps epsilon for differential privacy
	 * @param delta delta for differential privacy
	 * @return
	 */
	public static double calSmoothSensitivityUsing2ndMethod(int c, int n, double eps, double delta) {
		double globalSensitivity = SensitivityCalculator.boundByC(c);
		double beta = eps / (2.0 * Math.log(2.0 / delta));
		boolean stopSmall = false;
		boolean stopLarge = false;
		double maxSensitivityN = 0;
		for (int k = 0; k < Integer.MAX_VALUE; k++) {
			// case 1: new database has SMALLER #users
			double sensivitityK = 0;
			if (stopSmall == false) {
				int ny = Math.max(0, n - k);
				double lsy = SensitivityCalculator.boundByNC(ny, c);
				sensivitityK = Math.max(sensivitityK, Math.exp(-k * beta) * lsy);
				stopSmall = (Math.exp(-k * beta) * globalSensitivity < maxSensitivityN);
			}
			if (stopLarge == false) {
				int ny = n + k;
				double lsy = SensitivityCalculator.boundByNC(ny, c);
				sensivitityK = Math.max(sensivitityK, Math.exp(-k * beta) * lsy);
				stopLarge = (ny > (c / (Math.log(c) - 1) + 1));
			}
			maxSensitivityN = Math.max(maxSensitivityN, sensivitityK);
			if (stopSmall && stopLarge) 
				break;
		}
		
		return maxSensitivityN;
	}

	public static void calSmoothSensitivity2ndMethod(double eps,
	double delta,
	int c,
	double minSensitivity,
	double N,
	String dataGenerationOutputDir) {
		try {
			
			String outputFile = FileNameUtil.getSmoothSensitivityInputFile(dataGenerationOutputDir, c);
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(outputFile)));
			for (int n = 1; n < N; n++) {
				double maxSensitivityN = calSmoothSensitivityUsing2ndMethod(c, n, eps, delta);
				if (maxSensitivityN <  minSensitivity)
					break;
				writer.println(n + "," + maxSensitivityN);
//				if (n % 10000 == 0) 
//					System.out.println(n);
			}
			writer.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		System.out.println("start");
		
		double eps = Constants.DP_EPSILON;
		double delta = Constants.DP_DELTA;
		
		double minSensitivity = Constants.MIN_SENSITIVITY;
		double N = Constants.N + 10;
		String dataGenerationOutputDir = Constants.DATA_GENERATOR_OUTPUT_DIR;
		
		for (int c = 1; c <= 50; c++)
			calSmoothSensitivity2ndMethod(eps, delta, c, minSensitivity, N, dataGenerationOutputDir);
		
		System.out.println("finished");
	}
}
