package com.duykien.usc.EBM.calculator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.duykien.usc.EBM.dataIO.EBMDataIO;
import com.duykien.usc.locationentropy.locationdata.IntIntIntIntMap;

public class WeightedFrequencyCalculator {
	private static final Logger LOG = Logger.getLogger(WeightedFrequencyCalculator.class);
	
	public static void calculateWeightedFrequency(String cooccurenceFile, String locationEntropyFile, String potentialsFile, String outputFile) {
		try {
			PrintWriter writer = new PrintWriter(outputFile);
			Map<Integer, Double> le = EBMDataIO.readLocationEntropy(locationEntropyFile);
			Map<Integer, Set<Integer>> potentials = EBMDataIO.readPotentials(potentialsFile);
			
			BufferedReader reader = new BufferedReader(new FileReader(cooccurenceFile));
			String line = null;
			while ((line = reader.readLine()) != null) {
				IntIntIntIntMap cooc = EBMDataIO.parseCooccurenceLine(line);
				Set<Integer> us = cooc.getKeySet();
				if (us.isEmpty()) {
					LOG.error("Error calculating freq: u = -1");
					continue;
				}
				Integer u = us.iterator().next(); 
				
				writer.write(u + EBMDataIO.USER_SEPARATOR);
				if (potentials.containsKey(u) == false) {
					// there is no potential pair for this user
					continue;
				}
				
				ArrayList<Integer> vs = new ArrayList<>(potentials.get(u));
				Collections.sort(vs);
				
				for (Integer v: vs) {
					ArrayList<Integer> ls = new ArrayList<>(cooc.getThirdEntries(u, v));
					Collections.sort(ls);
					
					double wf = 0;
					
					for (Integer l : ls) {
						wf += cooc.get(u, v, l) * Math.exp(-le.get(l));
					}
					
					writer.write(v + EBMDataIO.COUNT_SEPARATOR 
							+ wf + EBMDataIO.USER_SEPARATOR);
				}
				writer.println();
			}
			
			reader.close();
			writer.close();
		} catch (Exception e) {
			LOG.error("Error calculating potential pairs", e);
		}
	}
}
