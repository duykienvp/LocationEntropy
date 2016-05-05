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
	
	private static final Logger LOG = Logger.getLogger(FrequencyCalculator.class);
	
	public static void calculateFrequency(String cooccurenceFile, String freqFile) {
		try {
			PrintWriter writer = new PrintWriter(freqFile);
			
			BufferedReader reader = new BufferedReader(new FileReader(cooccurenceFile));
			String line = null;
			while ((line = reader.readLine()) != null) {
				IntIntIntIntMap cooc = EBMDataIO.parseCooccurenceLine(line);
				Set<Integer> us = cooc.getKeySet();
				for (Integer u : us) {
					ArrayList<Integer> vs = new ArrayList<>(cooc.getSecondEntries(u));
					Collections.sort(vs);
					for (Integer v: vs) {
						if (u != v) {
							writer.println(u + EBMDataIO.USER_SEPARATOR
									+ v + EBMDataIO.USER_SEPARATOR
									+ cooc.sumLastEntries(u, v));
						}
					}
				}
			}
			
			reader.close();
			writer.close();
		} catch (Exception e) {
			LOG.error("Error calculating frequency", e);
		}
	}
}
