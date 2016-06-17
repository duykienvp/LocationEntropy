package com.duykien.usc.locationentropy.DP;

/**
 * Calculate bound of the change of location entropy when a user is added or removed
 * @author kiennd
 *
 */
public class SensitivityCalculator {
	
	/**
	 * Calculate entropy of a set [1, 1, ..., 1, C] where 1 appears n times
	 * @param n
	 * @param c
	 * @return
	 */
	public static double entropyNtimes1AndC(double n, double c) {
		if (n < 0.1 || c < 1.1)
			return 0;
		return Math.log(n + c) - ((double)c / (double)(n + c)) * Math.log(c);
	}
	
	/**
	 * Calculate the bound of local sensitivity of a location entropy when a user is removed in case 1
	 * @param n number of users 
	 * @param c maximum number of visits of a user
	 * @return
	 */
	public static double case1WithN(double n, double c) {
		if (n < 0.1 || c < 1.1)
			return 0;
		return Math.log(n - 1) - entropyNtimes1AndC(n - 1, c);
	}
	
	/**
	 * Calculate the bound of global sensitivity of a location entropy in case 1
	 * @param c maximum number of visits of a user
	 * @return
	 */
	public static double case1WithoutN(double C) {
		if (C < 1.1)
			return 0;
		return Math.log(C) - Math.log(Math.log(C)) - 1;
	}
	
	/**
	 * Calculate the bound of local sensitivity of a location entropy when a user is removed in case 2
	 * @param n number of users 
	 * @param c maximum number of visits of a user
	 * @return
	 */
	public static double case2UsingK(double n, double c) {
		if (n <= 0.1 || c < 0.1) // 0 if n == 0 or c == 0
			return 0;
		if (n < 1.1 || c < 1.1) { //  if c == 1
			return Math.log(((n+1)/n));
		}
		// test 2 cases: floor(k) and floor(k) + 1
		n = n - 1;
		double k = Math.floor(getKByNC(n, c));
		// let 1 <= k <= n;
		k = Math.min(k, n);
		k = Math.max(1, k);
		double Hk1 = Math.log(n - k + k * c) - ((k * c) / (n - k + k * c)) * Math.log(c);
		double deltaH1 = Math.log(1 + (1.0 / Math.exp(Hk1)));

		k++;
		// let 1 <= k <= n;
		k = Math.min(k, n);
		k = Math.max(1, k);
		double Hk2 = Math.log(n - k + k * c) - ((k * c) / (n - k + k * c)) * Math.log(c);
		double deltaH2 = Math.log(1 + (1.0 / Math.exp(Hk2)));
		return Math.max(deltaH1, deltaH2);
	}
	
	/**
	 * Get the value of {@code k}  when a user is removed in case 2.
	 * @param n number of users 
	 * @param c maximum number of visits of a user
	 * @return
	 */
	public static double getKByNC(double n, double c) {
		if (n < 0.1 || c < 1.1)
			return 0;
		return (n * (c * Math.log(c) - c + 1)) / ((c - 1) * (c - 1));
	}
	
	/**
	 * Calculate the bound of local sensitivity of a location entropy when a user is added or removed
	 * @param n number of users 
	 * @param c maximum number of visits of a user
	 * @return
	 */
	public static double boundByNC(double n, double c) {
		double maxCase1 = Math.max(case1WithN(n, c), case1WithN(n + 1, c));
		double maxCase2 = Math.max(case2UsingK(n, c), case2UsingK(n+1, c));
		return Math.max(maxCase1, maxCase2);
	}
	
	/**
	 * Calculate the bound of global sensitivity of a location entropy  when a user is added or removed
	 * @param c maximum number of visits of a user
	 * @return
	 */
	public static double boundByC(double c) {
		int minNCase2 = 1;
		return Math.max(case1WithoutN(c), case2UsingK(minNCase2, c));
	}
	
	
	/**
	 * Test global sensitivity
	 */
	private static void testMaxByAllN() {
		for (int c = 1; c < 50; c++) {
			System.out.println(c + "," + boundByC(c));
		}
	}
	
	public static void main(String[] args) {
		testMaxByAllN();
	}
}
