package com.duykien.usc.EBM.calculator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import org.apache.log4j.Logger;

import com.duykien.usc.EBM.dataIO.EBMDataIO;
import com.duykien.usc.locationentropy.locationdata.IntIntIntIntMap;

public class FrequencyCalculator {
	public static final int MIN_FREQUENCY = 2;
	
	private static final Logger LOG = Logger.getLogger(FrequencyCalculator.class);
	
	public static void calculateFrequency(String cooccurenceFile, String freqFile) {
		try {
			PrintWriter writer = new PrintWriter(freqFile);
			
			BufferedReader reader = new BufferedReader(new FileReader(cooccurenceFile));
			String line = null;
			while ((line = reader.readLine()) != null) {
				IntIntIntIntMap cooc = EBMDataIO.parseCooccurenceLine(line);
				Set<Integer> us = cooc.getKeySet();
				if (us.isEmpty()) {
					LOG.error("Error calculating freq");
					continue;
				}
				Integer u = us.iterator().next(); 
				
				writer.write(u + EBMDataIO.USER_SEPARATOR);
				ArrayList<Integer> vs = new ArrayList<>(cooc.getSecondEntries(u));
				Collections.sort(vs);
				for (Integer v: vs) {
					if (u != v) {
						int c = cooc.sumLastEntries(u, v);
						if (MIN_FREQUENCY <= c) {
							writer.write(v + EBMDataIO.COUNT_SEPARATOR 
									+ c + EBMDataIO.USER_SEPARATOR);
						}
					}
				}
				writer.println();
			}
			
			reader.close();
			writer.close();
		} catch (Exception e) {
			LOG.error("Error calculating frequency", e);
		}
	}
}
