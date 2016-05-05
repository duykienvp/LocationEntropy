package com.duykien.usc.EBM.calculator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.duykien.usc.EBM.dataIO.EBMDataIO;
import com.duykien.usc.locationentropy.locationdata.IntIntIntIntMap;
import com.duykien.usc.locationentropy.locationdata.IntIntIntMap;

public class DiversityCalculator {
	public static final double q = 0.1;
	private static final Logger LOG = Logger.getLogger(DiversityCalculator.class);
	
	public static void calculateDiversity(String cooccurenceFile, String frequencyFile, String potentialFile, String diversityFile) {
		try {					
			PrintWriter writer = new PrintWriter(diversityFile);
			
			Map<Integer, Set<Integer>> potentials = EBMDataIO.readPotentials(potentialFile);
			Set<Integer> potentialsUsers = new HashSet<>(potentials.keySet());
			potentials.clear();
			
			BufferedReader coocReader = new BufferedReader(new FileReader(cooccurenceFile));
			BufferedReader freqReader = new BufferedReader(new FileReader(frequencyFile));
			String coocLine = null;
			String freqLine = null;
			while (((coocLine = coocReader.readLine()) != null)
					&& ((freqLine = freqReader.readLine()) != null)) {
				IntIntIntIntMap cooc = EBMDataIO.parseCooccurenceLine(coocLine);
				Set<Integer> us = cooc.getKeySet();
				if (us.isEmpty()) {
					LOG.warn("Calculating diversity: empty user set");
					continue;
				}
				Integer u = us.iterator().next();
				if (potentialsUsers.contains(u) == false) 
					continue;
				
				IntIntIntMap freqMap = EBMDataIO.parseIntIntIntLine(freqLine);
				if (freqMap.getKeySet().contains(u) == false) {
//					LOG.error("freq does not contain user " + u);
					//only write u
					writer.println(freqLine.split(EBMDataIO.USER_SEPARATOR)[0]);
					continue;
				}
				
				writer.write(u + EBMDataIO.USER_SEPARATOR);
				ArrayList<Integer> vs = new ArrayList<>(freqMap.getSecondEntries(u));
				Collections.sort(vs);
				
				for (Integer v : vs) {
					double dUV = 0.0;
					double freqUV = freqMap.get(u, v);
					
					Set<Integer> ls = cooc.getThirdEntries(u, v);
					for (Integer l: ls) {
						double cUVL = cooc.get(u, v, l);
						cUVL = cUVL / freqUV;
						dUV += Math.pow(cUVL, q);
					}
					
					dUV = Math.pow(dUV, 1.0/(1-q));
					writer.write(v + EBMDataIO.COUNT_SEPARATOR 
							+ dUV + EBMDataIO.USER_SEPARATOR);
				}
				
				writer.println();
			}
			
			coocReader.close();
			freqReader.close();
			writer.close();
			
		} catch (Exception e) {
			LOG.error("Error calculating frequency", e);
		}
	}
}
