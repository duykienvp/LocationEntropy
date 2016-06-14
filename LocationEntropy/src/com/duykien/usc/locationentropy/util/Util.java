package com.duykien.usc.locationentropy.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;

public class Util {
	public static final double PRECISION = 1e-15;
	
	private static final Logger LOG = Logger.getLogger(Util.class);
	
	public static DescriptiveStatistics calDescriptiveStatistics(Map<Integer, Double> values) {
		DescriptiveStatistics stat = new DescriptiveStatistics();
		if (values != null) {
			for (Map.Entry<Integer, Double> entry : values.entrySet()) {
				stat.addValue(entry.getValue());
			}
		}
		return stat;
	}
	
	public static boolean isDoubleEqual(double a, double b) {
		return Math.abs(a - b) < PRECISION;
	}
	
	public static void printArray(double[] nums) {
		if (nums == null) 
			return;
		LOG.info(Arrays.toString(nums));
	}
	
	public static void printlnList(ArrayList<Double> a) {
		if (a == null) 
			return;
		for (int i = 0; i < a.size(); i++) {
			LOG.info(a.get(i));
		}
	}
	public static void printList(ArrayList<Double> a) {
		if (a == null) 
			return;
		LOG.info(a.toString());
	}
	
	/**
	 * Remove duplicates in a sort list of double values
	 * @param a
	 * @return
	 */
	public static ArrayList<Double> cleanDuplicates(ArrayList<Double> a) {
		ArrayList<Double> cleanedA = new ArrayList<>();
		double prev = -1;
		for (int i = 0; i < a.size(); i++) {
			if (PRECISION < Math.abs(prev - a.get(i))) {
				prev = a.get(i);
				cleanedA.add(prev);
			}
		}
		
		return cleanedA;
	}
}
