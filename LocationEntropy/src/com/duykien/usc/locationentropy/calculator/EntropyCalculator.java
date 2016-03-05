package com.duykien.usc.locationentropy.calculator;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class EntropyCalculator {
	public static final double PRECISION = 1e-12;
	
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
	 * Calculate Shannon entropy of a list of numbers
	 * @param nums
	 * @return entropy or 0 if error occurred
	 */
	public static double calShannonEntropy(double[] nums) {
		try {
			double entropy = 0;
			double sum = 0;
			for (int i = 0; i < nums.length; i++) {
				sum += nums[i];
			}
			
			double ln2 = Math.log(2);
			for (int i = 0; i < nums.length; i++) {
				double p = nums[i] / sum;
				entropy += -p * Math.log(p) / ln2;
			}
			
			return entropy;
		} catch (Exception e) {
			LOG.error("Error calculating location entropy", e);
			return 0;
		}
	}
}
