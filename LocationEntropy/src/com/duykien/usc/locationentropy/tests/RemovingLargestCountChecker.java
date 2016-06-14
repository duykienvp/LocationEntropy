package com.duykien.usc.locationentropy.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.duykien.usc.locationentropy.calculator.EntropyCalculator;
import com.duykien.usc.locationentropy.experiments.Experiments;
import com.duykien.usc.locationentropy.util.Util;

public class RemovingLargestCountChecker {
	public static final String LOG4J_PROPERTIES_FILE = "log4j.properties";
	private static final Logger LOG = Logger.getLogger(Util.class);

	private static int C = 9;
	private static int N = 9;
	private static int TTT = 1001;
	private static int[] nums = new int[TTT];
	
	private static int countCombination = 0;
	
	private static double maxChange = 0;
	private static int[] maxChangeNums = new int[TTT];
	private static int maxChangePos = -1;
	private static int[] minNums = new int[TTT];
	private static Set<Integer> maxChangeValues = new HashSet<>();
	
	
	private static double minEntropy = Double.MAX_VALUE;
	
	public static double[] toDoubleArray(int[] ns) {
		double[] tmp = new double[ns.length];
		for (int i = 0; i < ns.length; i++) 
			tmp[i] = ns[i];
		return tmp;
	}
	
	public static double calEntropyOfRemoval(int pos) {
		int value = nums[pos];
		nums[pos] = 0;
		double entropy = EntropyCalculator.calShannonEntropy(toDoubleArray(nums));
//		System.out.println("Removing " + pos +", entropy=" + entropy);
		nums[pos] = value;
		return entropy;
	}
	
	/**
	 * Check if removing the max count results in the max change or not. 
	 * Answer: NO
	 */
	public static void checkRemovalMax() {
		//Original entropy
		double entropy = EntropyCalculator.calShannonEntropy(toDoubleArray(nums));
		
		//Remove max value
		int maxPos = N-1;
		double removingMaxDiff = Math.abs(entropy - calEntropyOfRemoval(N-1));
		
		//Remove others
		for (int i = N-2; 0 <= i; i--) {
			if (0.5 < nums[i + 1] - nums[i]) {
				double removingPosDiff = Math.abs(entropy - calEntropyOfRemoval(i));
				if (Util.PRECISION < removingPosDiff - removingMaxDiff) {
					maxPos = i;
					removingMaxDiff = removingPosDiff;
				}
			}
		}
//		LOG.info("List: " + Arrays.toString(nums));
//		LOG.info("Max pos = " + maxPos + ", max value = " + nums[maxPos]);
//		LOG.info("");
		
		countCombination++;
	}
	
	/**
	 * Check if removing a user can increase the entropy or not.
	 * Answer: YES
	 */
	public static boolean checkRemovalIncrease() {
		countCombination++;
		//Original entropy
		double entropy = EntropyCalculator.calShannonEntropy(toDoubleArray(nums));
		
		//Remove max value
		double newE = calEntropyOfRemoval(N-1);
		if (Util.PRECISION < newE - entropy) {
			
			LOG.info("INCREASED");
			LOG.info("List: " + Arrays.toString(nums));
			return true;
		}
		
		//Remove others
		for (int i = N-2; 0 <= i; i--) {
			if (0.5 < nums[i + 1] - nums[i]) {
				newE = calEntropyOfRemoval(i);
				if (Util.PRECISION < newE - entropy) {
					LOG.info("INCREASED");
					LOG.info("List: " + Arrays.toString(nums));
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Calculate the max change when we remove 1 user. 
	 */
	public static double calRemovalMax() {
		countCombination++;
		//Original entropy
		double entropy = EntropyCalculator.calShannonEntropy(toDoubleArray(nums));
		
		//Remove max value
		int maxPos = N-1;
		double removingMaxDiff = entropy - calEntropyOfRemoval(N-1);
		
		//Remove others
		for (int i = N-2; 0 <= i; i--) {
			if (nums[i] < nums[i + 1]) {
				double removingPosDiff = entropy - calEntropyOfRemoval(i);
				if (Util.PRECISION < Math.abs(removingPosDiff) - Math.abs(removingMaxDiff)) {
					maxPos = i;
					removingMaxDiff = removingPosDiff;
				}
			}
		}
		/*
		 * If current maxChange is less than new change, OR
		 * if current maxChange is equal to the new change but the new change is caused by removing the max count,
		 * SAVE it
		 */
		if (Util.PRECISION < Math.abs(removingMaxDiff) - Math.abs(maxChange)) {
			maxChange = removingMaxDiff;
			for (int i = 0; i < nums.length; i++) {
				maxChangeNums[i] = nums[i];
			}
			maxChangePos = maxPos;
			maxChangeValues = new HashSet<>();
			maxChangeValues.add(nums[maxChangePos]);
		} 
//		else if (Util.isDoubleEqual(Math.abs(maxChange), Math.abs(removingMaxDiff))) {
//			maxChangeValues.add(nums[maxChangePos]);
//		}
		
		return removingMaxDiff;
	}
	
	public static void checkSmallestEntropy() {
		double entropy = EntropyCalculator.calShannonEntropy(toDoubleArray(nums));
		if (Util.PRECISION < minEntropy - entropy) {
			minEntropy = entropy;
			for (int i = 0; i < nums.length; i++) {
				minNums[i] = nums[i];
			}
			
//			LOG.info("UPDATE MIN: " + entropy + "," + Arrays.toString(nums));
		}
	}
	
	public static void removeOne() {
		double entropy = EntropyCalculator.calShannonEntropy(toDoubleArray(nums));
		for (int i = 0; i < N; i++) {
			if (nums[i] == C) {
				double removingPosDiff = entropy - calEntropyOfRemoval(i);
				if (Util.PRECISION < Math.abs(removingPosDiff)) {
					if (removingPosDiff < 0)
						LOG.info("Remove " + i + " - " + nums[i] + " INcreases " + Math.abs(removingPosDiff) + ": " + Arrays.toString(nums));
				}
				break;
			}
		}
	}
	
	public static void removeAll() {
		double entropy = EntropyCalculator.calShannonEntropy(toDoubleArray(nums));
		System.out.println(nums[N-1] + "," + (calEntropyOfRemoval(N-1) - entropy)); 
		
		for (int i = N-2; 0 <= i; i--) {
			if (nums[i] < nums[i + 1]) {
				double diff = calEntropyOfRemoval(i) - entropy;
//				System.out.println(nums[i] + "," + diff); 
			}
		}
	}
	
	public static void tryPos(int pos, int minValue) {
		for (int i = minValue; i <= C; i++) {
			nums[pos] = i;
			if (pos == N-1) 
//				checkRemovalMax();
//				checkRemovalIncrease();
				calRemovalMax();
//				checkSmallestEntropy();
//				removeOne();
//				removeAll();
			else
				tryPos(pos+1, i);
			nums[pos] = 0;
		}
	}
	
	public static void testPositiveNegative() {
		LOG.info("START test positive, negative of the difference between entropies before and after the removal");
		
		for (int tC = 2; tC < 5; tC++) {
			C = tC;
			double pivot = C / (Math.log(C) - 1);
			LOG.info("C = " + C + ", pivot = " + pivot);
			for (int tN = 1; tN < 100; tN++) {
				N = tN;
				LOG.info("N = " + N);
				for (int i = 0; i < nums.length; i++) 
					nums[i] = 0;
				for (int i = 0; i < N; i++) 
					nums[i] = 1;
				double lnN = EntropyCalculator.calShannonEntropy(toDoubleArray(nums));
				nums[N] = C;
				double diff = lnN - EntropyCalculator.calShannonEntropy(toDoubleArray(nums));
				LOG.info("diff = " + diff);
			}
		}
		
		LOG.info("FINISH test positive, negative of the difference between entropies before and after the removal");
	}
	
	public static void testP() {
		double step = 0.01;
		int n = 15;
		double[] tmp = new double[n];
		for (double p = step; p <= 1; p += step) {
			for (int i = 0; i < n; i++)
				tmp[i] = (1 - p) / (n-1);
			tmp[n-1] = p;
			System.out.println(EntropyCalculator.calShannonEntropy(tmp));
		}
	}
	
	public static boolean checkNByC(double N, double C) {
		return (Math.log(N) - Math.log(N + C) + (C / (N + C)) * Math.log(C)) > 0;
	}
	
	public static void testNByC() {
		for (int c = 20; 1 < c; c--) {
			for (int n = 1; n < 1000000000; n++) {
				if (checkNByC(n, c)) {
					System.out.println(c + " " + n);
					break;
				}
			}
		}
	}
	
	public static boolean checkMaxChangeNums() {
		if (maxChangeNums[N-1] != C)
			return false;
		for (int i = 0; i < N - 1; i++) {
			if (maxChangeNums[i] != 1)
				return false;
		}
		return true;
	}
	
	public static void testChangeAnotherValue() {
		int c = 5;
		for (int i = 1; i <= c; i++) {
			int[] ts = new int[] {1, 1, 1, 1, 1, i, c};
			double entropy1 = EntropyCalculator.calShannonEntropy(toDoubleArray(ts));
			ts = new int[] {1, 1, 1, 1, 1, i};
			double entropy2 = EntropyCalculator.calShannonEntropy(toDoubleArray(ts));
			System.out.println(i + "\t" + (entropy2 - entropy1));
		}
		
//		int[] ts = new int[] {1, 1, 1, 1, 1, 1, c};
//		double entropy1 = EntropyCalculator.calShannonEntropy(toDoubleArray(ts));
//		ts = new int[] {1, 1, 1, 1, 1, 1};
//		double entropy2 = EntropyCalculator.calShannonEntropy(toDoubleArray(ts));
//		System.out.println(0 + "\t" + (entropy2 - entropy1));
	}
	
	public static double entropyNtimes1AndC(double n, double c) {
		return Math.log(n+c) - (c / (n+c))*Math.log(c);
	}
	
	public static void testNByC2() {
		double minC = 3;
		double maxC = 4;
		for (double c = minC; c < maxC + 1; c++) {
			double res = -1;
			for (double n = 2; n < 1000000; n++) {
				double enc = entropyNtimes1AndC(n, c);
				double en1c = entropyNtimes1AndC(n-1, c);
				double sk = n - 1 + c;
				double ck = sk / Math.exp(en1c);
				if (Math.log(n) - enc > Math.log(1.0 + ck / sk)) { 
					res = n;
					System.out.println(c + " " + n + " " + ck + " " + sk + " " + enc + " " + en1c + " " + Math.exp(en1c) + " " + (sk/ck));
					System.out.println((Math.log(n) - enc) + " " + Math.log(1.0 + ck / sk));
					break;
				}
			}
			
			if (res > -1) {
				System.out.println(c + " -- " + (res + 1));
			} else {
				System.out.println(c + " -- " + "unknown");
			}
		}
	}
	
	public static void testNByC3() {
		for (double c = 3; c < 20; c++) {
			double res = -1;
			for (double n = 2; n < 1000000; n++) {
				double enc = entropyNtimes1AndC(n, c);
				if (Math.log(n) - enc > Math.log((n+1) / n)) { 
					res = n;
					break;
				}
			}
			
			if (res > -1) {
				System.out.println(c + " -- " + (res + 1));
			} else {
				System.out.println(c + " -- " + "unknown");
			}
		}
	}
	
	public static void testNByC4() {
		for (double c = 4; c < 8; c++) {
			for (double n = 3; n < 50; n++) {
				double k = (n * c * (c - 1 - Math.log(c))) / ((c-1) * (c-1));
				System.out.println(c + " " + n + " " +k);
			}
		}
		
		for (double c = 4; c < 20; c++) {
			double res = -1;
			double pivot = (c * (c - 1 - Math.log(c)) ) / ((c - 1) * (c - 1));
			for (double n = 2; n < 1000000; n++) {
				if (pivot - ((n-1) / n) >= Util.PRECISION) { 
					res = n;
					break;
				}
			}
			
			if (res > -1) {
				System.out.println(c + " -- " + (res + 1));
			} else {
				System.out.println(c + " -- " + "unknown");
			}
		}
	}
	
	public static double case1(double n, double c) {
		return Math.log(n-1) - entropyNtimes1AndC(n-1, c);
	}
	
	public static double case2(double n, double c) {
		double Hk = getHkByNC(n, c);
		return Math.log(1 + (1.0 / Math.exp(Hk)));
	}
	
	public static double getHkByNC(double n, double c) {
		double k = getKByNC(n, c);
		return Math.log(k + (n-k) * c) - ((n - k) * c) / (k + (n-k) * c) * Math.log(c);
	}
	
	public static double getKByNC(double n, double c) {
		return (n * c * (c - 1 - Math.log(c))) / ((c-1) * (c-1));
	}

	public static double getCkByNC(double n, double c) {
		long k = Math.round(getKByNC(n-1, c));
		double Sk = k + (n - 1 - k) * c;
		return Sk / Math.exp(getHkByNC(n, c));
	}
	
	public static double case2NoK(double n, double c) {
		double Hk = Math.log(n-1) - (Math.log(c)) / (c-1) + Math.log((Math.log(c)) / (c-1)) + 1;
		return Math.log(1 + (1.0 / Math.exp(Hk))); 
	}
	
	public static void testNByC5() {
//		for (double cd = 2; cd < 50; cd++) {
//			for (double nd = 2; nd < 50; nd++) {
//				long c = Math.round(cd);
//				long n = Math.round(nd);
//
//				double value = Math.max(case1(n, c), case2(n-1, c));
//				LOG.info("C=\t" + c + ", N=\t" + n + ", value =\t" + value); 
//			}
//		}
//		
//		boolean stop  = true;
//		if (stop)
//			return;
		
		ArrayList<Double> res = new ArrayList<>();
		for (double cd = 4; cd < 20; cd++) {
			for (double nd = 2; nd < 1000000000; nd++) {
				long c = Math.round(cd);
				long n = Math.round(nd);

				res.clear();
				if (case1(n, c) > case2NoK(n, c)) {
					for (int i = 0; i < n-1; i++) 
						res.add(1.0);
					res.add((double)c);
					String infoStr = "------C = " + c + ", N = " + n;
					System.out.println(infoStr);
					System.out.println(case1(n, c));
					System.out.println(Arrays.toString(res.toArray()));
					
					break;
				} else {
					long k = Math.round(getKByNC(n-1, c));
					for (int i = 0; i < k; i++) 
						res.add(1.0);
					res.add((double)Math.round(getCkByNC(n, c)));
					for (int i = 0; i < n - k - 1; i++) {
						res.add((double)c);
					}
					String infoStr = "------C = " + c + ", N = " + n;
					System.out.println(infoStr);
					System.out.println(k + " " + (double)Math.round(getCkByNC(n, c)));
					System.out.println(case2(n, c));
					System.out.println(Arrays.toString(res.toArray()));
				}
			}
		}
	}
	
	public static double deltaH1(double C) {
		return Math.log(C) - Math.log(Math.log(C)) - 1;
	}
	
	public static void testDeltaH1WithoutN() {
		for (int C = 2; C < 50; C++) {
			System.out.println(C + "," + deltaH1(C));
		}
	}
	
	public static void testCase2() {
		for (int c = 2; c < 20; c++) {
			double v1 = Double.MAX_VALUE;
			for (int n = 2; n < 10; n++) {
				System.out.println(c + "\t-- " + n + "\t-- " + case2NoK(n, c));
			}
			System.out.println("+++++++");
		}
//		System.out.println("+++++++");
	}
	
	public static void testMaxByAllN() {
		System.out.println(1 + "," + Math.log(2));
		for (int c = 2; c < 50; c++) {
			double m = Math.max(deltaH1(c), case2NoK(2, c));
			System.out.println(c + "," + m);
		}
	}
	public static void testNEquals2() {
		for (int c = 2; c < 50; c++) {
			double m = Math.max(deltaH1(c), case2(2, c));
			System.out.println(c + "\t" + deltaH1(c) + "\t" + case2(2, c));
		}
	}

	public static void main(String[] args) {
		PropertyConfigurator.configure(LOG4J_PROPERTIES_FILE);
//		testNEquals2();
		testMaxByAllN();
//		testCase2();
//		testWithoutN1();
//		testNByC5();
//		testChangeAnotherValue();
//		int[] ts = new int[] {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 4, 4, 4, 4, 4, 4};
//		double en = EntropyCalculator.calShannonEntropy(toDoubleArray(ts));
//		System.out.println(en);
//		ts = new int[] {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 4, 4, 4, 4, 4, 4};
//		double en1 = EntropyCalculator.calShannonEntropy(toDoubleArray(ts));
//		System.out.println(en1);
//		System.out.println(en1 - en);
////		System.out.println(Math.log(3));
//		double sk = 12;
//		double ck = sk / Math.pow(Math.E, en);
//		System.out.println(ck);
//		ts = new int[] {1, 1, 1, 1, 1, 1, 7};
//		System.out.println(Math.log((sk + ck)/sk));
//		int n = 10;
//		for (double t = 0.01; t < 0.5; t += 0.01) {
//			System.out.println(t * Math.log(n / t));
//		}
//		testNByC3();
//		testNByC2();
//		testNByC4();
//		testP();
//		testNByC();
//		int[] ts = new int[] {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 8};
//		double h2 = EntropyCalculator.calShannonEntropy(toDoubleArray(ts));
//		for (int i = 1; i < 9; i++) {
//			ts[12] = i;
//			double h1 = EntropyCalculator.calShannonEntropy(toDoubleArray(ts));
//			System.out.println(h2 - h1);
//		}
		
//		testPositiveNegative();
		
		
//		for (int i = 0; i < nums.length; i++) 
//			nums[i] = 0.0;
//		N = 10;
//		C = 5;
//		nums[N] = C;
//		tryPos(0, 1);
//		
//		System.out.println(Math.log(6.0/13.0) + (7.0 / 13.0) * Math.log(7) - Math.log((12 + ck)/12.0));
		boolean stop = true;
		if (stop)
			return;
//		for (int i = 0; i < N; i++) 
//			nums[i] = 1;
//		LOG.info("entropy N = " + EntropyCalculator.calShannonEntropy(nums));
//		nums[N] = 5;
//		LOG.info("entropy N +C = " + EntropyCalculator.calShannonEntropy(nums));
		
//		LOG.info("From 30 ---->>>>> 50");
		for (int tC = 5; tC < 8; tC++) {
			for (int tN = 2; tN < 13; tN++) {
				//1131
				N = tN;
				C = tC;
//				String infoStr = "------N = " + N + ", C = " + C;
				String infoStr = "------C = " + C + ", N = " + N;
//				if (C == 5)
//					infoStr += " <<<<<<<<";
				LOG.info(infoStr);
				TTT = N;
				nums = new int[TTT];
				
				countCombination = 0;
				
				maxChange = 0;
				maxChangeNums = new int[TTT];
				maxChangePos = -1;
				maxChangeValues = new HashSet<>();
				minEntropy = Double.MAX_VALUE;
				minNums = new int[TTT];
				for (int i = 0; i < nums.length; i++) 
					nums[i] = 0;
//				nums[N] = C;
				tryPos(0, 1);
//				LOG.info("minNums=" + Arrays.toString(minNums));
				
//				if (checkMaxChangeNums() == false) 
//					continue;
				
				LOG.info("maxChangeValues: " + maxChangeValues.toString());
				LOG.info("maxChange("+ C + ")=" + maxChange);
				LOG.info("maxChangeNums=" + Arrays.toString(maxChangeNums));
				LOG.info("maxChangePos =" + maxChangePos + ", value =" + maxChangeNums[maxChangePos]);
//				if (checkMaxChangeNums())
//					break;
//				break;
//				if (C == 5) {
//					if (!Util.isDoubleEqual(maxChangeNums[N-2], 1)) {
//						LOG.error("WRONGGGGGGGGGGGGGGGGGGGGGGGG");
//					}
//				}
//				LOG.info("ln2 = " + Math.log(2));
//				double n0 = Experiments.findN0(C);
//				LOG.info("maxH(" + C + ")=" + Experiments.maxH(n0, C));
			}
		}
		
		/*
		- Observation 1: The biggest change may not from the biggest count (Go to Observation 3).
		- Observation 2: the changes in SPECIFIC cases are often very SMALL
		- Observation 3: When C is big enough compared to n, removing C from [1, 1, ..., 1, C] will cause the biggest change. 
		Question: HOW MUCH is enough?
		*/
	}

}
