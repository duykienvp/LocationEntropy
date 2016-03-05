package com.duykien.usc.locationentropy.util;

import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;

public class Util {
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
}
