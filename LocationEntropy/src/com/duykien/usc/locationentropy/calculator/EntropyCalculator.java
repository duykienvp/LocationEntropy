package com.duykien.usc.locationentropy.calculator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.duykien.usc.locationentropy.util.Util;

public class EntropyCalculator {
	private static int C = 3;
	private static int N = 9;
	private static double currentMinEntropy = Double.MAX_VALUE;
	private static double[] answer = new double[N];
	private static double[] nums = new double[N];
	private static ArrayList<Double> entropies = new ArrayList<>();
	private static ArrayList<Double> deltas = new ArrayList<>();
	
	public static final double PRECISION = Util.PRECISION;
	
	private static final Logger LOG = Logger.getLogger(EntropyCalculator.class);	
	
	/**
	 * Calculate Shannon entropy of checkins in each location Map: locationId -> (Map: userId -> numCheckins)
	 * @param checkins
	 * @return Map: locationId -> entropy
	 */
	public static Map<Integer, Double> calShannonEntropyMultiple(Map<Integer, Map<Integer, Integer>> checkins) {
		Map<Integer, Double> result = new HashMap<>();
		if (checkins != null) {
			for (Map.Entry<Integer, Map<Integer, Integer>> entry : checkins.entrySet()) {
				Double entropy = calShannonEntropy(entry.getValue());
				if (PRECISION < entropy) {
					result.put(entry.getKey(), entropy);
				}
			}
		}
		return result;
	}
	
	/**
	 * Calculate Shannon entropy of values of a map
	 * @param checkins
	 * @return entropy or 0 if error occurred
	 */
	public static double calShannonEntropy(Map<Integer, Integer> checkins) {
		try {
			double[] nums = new double[checkins.size()];
			int i = 0;
			for (Map.Entry<Integer, Integer> entry : checkins.entrySet()) {
				nums[i++] = entry.getValue();
			}
			
			return calShannonEntropy(nums);
		} catch (Exception e) {
			LOG.error("Error calculating location entropy", e);
			return 0;
		}
	}
	
	/**
	 * Calculate Shannon entropy of a list of numbers with base E for logarithm
	 * @param nums
	 * @return entropy or 0 if error occurred
	 */
	public static double calShannonEntropy(double[] nums) {
		return calShannonEntropy(nums, Math.E);
	}
	
	/**
	 * Calculate Shannon entropy of a list of numbers with a given base for logarithm
	 * @param nums
	 * @return entropy or 0 if error occurred
	 */
	public static double calShannonEntropy(double[] nums, double base) {
		try {
			double entropy = 0;
			double sum = 0;
			for (int i = 0; i < nums.length; i++) {
				sum += nums[i];
			}
			
			double lnBase = Math.log(base);
			for (int i = 0; i < nums.length; i++) {
				if (0 < nums[i]) {
					double p = nums[i] / sum;
					entropy += -p * Math.log(p) / lnBase;
				}
			}
			
			return entropy;
		} catch (Exception e) {
			LOG.error("Error calculating location entropy", e);
			return 0;
		}
	}
	
	public static void tryRemove(double[] nums) {
		double[] tmp = new double[nums.length - 1];
		double entropy = calShannonEntropy(nums);
		for (int i = 0; i < nums.length; i++) {
			int pos = 0;
			for (int j = 0; j < nums.length; j++) {
				if (i != j) {
					tmp[pos++] = nums[j];
				}
			}
			
			deltas.add(Math.abs(calShannonEntropy(tmp) - entropy));
		}
	}
	
	public static void tryPos(int pos, int maxPos, int maxC) {
		if (pos == maxPos) {
			double entropy = calShannonEntropy(nums, 2);
//			tryRemove(nums);
//			entropies.add(entropy);
			if (entropy < currentMinEntropy) {
				currentMinEntropy = entropy;
				for (int i = 0; i < nums.length; i++) {
					answer[i] = nums[i];
				}
			}
			
			return;
		}
		for (int i = 1; i < maxC + 1; i++) {
			nums[pos] = i;
			tryPos(pos + 1, maxPos, maxC);
			nums[pos] = 0;
		}
	}
	

	
	public static void test() {
		for (int n = 2; n < N; n++) {
			//reset
			for (int j = 0; j < nums.length; j++)
				nums[j] = 0;
			currentMinEntropy = Double.MAX_VALUE;
			
			tryPos(0, n, C);
//			System.out.print("answer=");
//			for (int i = 0; i < C; i++) {
//				System.out.print(answer[i] + " "); 
//			}
			System.out.println("c = " + n);
			System.out.println("min Entropy=" + currentMinEntropy);
			System.out.println("max Entropy=" + (Math.log(n) / Math.log(2)));
		}
		
//		Collections.sort(deltas);
//		ArrayList<Double> cleanedDeltas = cleanDuplicates(deltas);
//		System.out.println("Cleaned Deltas------------------");
//		printlnList(cleanedDeltas);
//
//		Collections.sort(entropies);
//		System.out.println("Entropies before------------------");
////		printlnList(entropies);
//
//		System.out.println("Entropies after------------------");
//		ArrayList<Double> cleanedEntropies = cleanDuplicates(entropies);
//		printlnList(cleanedEntropies);
		
//		System.out.println(entropies.get(0));
	}
	
	public static void main(String[] args) {
		
		test();
		/*
		double c = 300;
		int n = 8;
		double[] nums = new double[n];
		nums[0] = 1;
		nums[1] = 1;
		nums[2] = 1;
		nums[3] = 1;
		nums[4] = 1;
		nums[5] = 1;
		nums[6] = 1; 
		nums[7] = 8;
		System.out.println("calShannonEntropy(nums) = " + calShannonEntropy(nums));
		
		double h1c = calShannonEntropy(nums);
		nums[n] = 0; // c 1 ... 0
		double h10 = calShannonEntropy(nums);
		nums[1] = c; //c c 0
		double h11c = calShannonEntropy(nums);
		
		nums[0] = 2; 	// 2 1 1 1 1 0
		nums[n] = c; // 2 1 1 1 1 2
		double h2c = calShannonEntropy(nums);
		nums[0] = 0; 	// 0 1 1 1 1 2
		double h20 = calShannonEntropy(nums);
		
//		System.out.println(Math.abs(h10 - h1c));
//		System.out.println(Math.abs(h20 - h2c));
//		System.out.println(Math.abs(h11c - h1c));
//		for (int i = 0; i < c; i++) {
//			nums[0] = i+1;
//			System.out.println("nums[0] = " + nums[0] + ": " + calShannonEntropy(nums));
//		}
 * */
	}
}
