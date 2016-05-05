package com.duykien.usc.EBM.calculator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.log4j.Logger;

import com.duykien.usc.EBM.dataIO.EBMDataIO;
import com.duykien.usc.locationentropy.locationdata.IntIntIntMap;

public class PotentialPairsCalculator {
	private static final Logger LOG = Logger.getLogger(PotentialPairsCalculator.class);
	
	public static void calculatePotentialPairs(String freqFile, String outputFile) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(freqFile));
			PrintWriter writer = new PrintWriter(outputFile);
			String line = null;
			while ((line = reader.readLine()) != null) {
				IntIntIntMap freqMap = EBMDataIO.parseIntIntIntLine(line);
				if (freqMap.getKeySet().isEmpty()) {
					//only write u
					line = line.split(EBMDataIO.USER_SEPARATOR)[0];
					writer.println(line);
					continue;
				}
				
				Integer u = freqMap.getKeySet().iterator().next();
				writer.write(u + EBMDataIO.USER_SEPARATOR);
				
				ArrayList<Integer> potentials = new ArrayList<>(freqMap.getSecondEntries(u));
				Collections.sort(potentials);
				for (Integer v : potentials) {
					writer.write(v + EBMDataIO.USER_SEPARATOR);
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
