package com.duykien.usc.locationentropy.util;

import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;

public class Util {
	public static final double PRECISION = 1e-12;
	
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
	
	public static void printlnList(ArrayList<Double> a) {
		for (int i = 0; i < a.size(); i++) {
			System.out.println(a.get(i));
		}
	}
	public static void printList(ArrayList<Double> a) {
		for (int i = 0; i < a.size(); i++) {
			System.out.print(a.get(i));
		}
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
