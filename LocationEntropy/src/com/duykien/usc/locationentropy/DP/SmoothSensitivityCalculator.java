package com.duykien.usc.locationentropy.DP;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

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

	public static void calSmoothSensitivity2ndMethod() {
		try {
			double eps = Math.log(10);
			double delta = 1e-7;
			int c = 50;
			double tolerate = 1e-3;
			double N = 1000010;
			String outputDir = "/Users/kiennd/Downloads/location_entropy_data/";
			String outputFile = outputDir + "varyN_fixedC" + c + "_SmoothSensitivity_EpsLn10_Delta1e-7_Tolerance1e-3"
					+ ".csv";
			
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(outputFile)));
			for (int n = 1; n < N; n++) {
				double maxSensitivityN = calSmoothSensitivityUsing2ndMethod(c, n, eps, delta);
				if (maxSensitivityN <  tolerate)
					break;
				writer.println(n + "," + maxSensitivityN);
				if (n % 10000 == 0) 
					System.out.println(n);
			}
			writer.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		System.out.println("start");
		
		calSmoothSensitivity2ndMethod();
		
		System.out.println("finished");
	}
}
