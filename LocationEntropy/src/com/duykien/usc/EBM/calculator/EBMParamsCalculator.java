package com.duykien.usc.EBM.calculator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.duykien.usc.EBM.dataIO.EBMDataIO;

public class EBMParamsCalculator {
	private static final Logger LOG = Logger.getLogger(EBMParamsCalculator.class);
	
	public static void calculateEBMModelParams(String potentialsFile, 
			String diversityFile, 
			String weightedFreqFile, 
			String socialStrengthFile) {
		try {
			Map<Integer, Set<Integer>> potentials = EBMDataIO.readPotentials(potentialsFile);
			BufferedReader potentialReader = new BufferedReader(new FileReader(potentialsFile));
			BufferedReader diversityReader = new BufferedReader(new FileReader(diversityFile));
			BufferedReader weightedFreqReader = new BufferedReader(new FileReader(weightedFreqFile));
			BufferedReader socialStrengthReader = new BufferedReader(new FileReader(socialStrengthFile));
			
			String pLine = null;
			String dLine = null;
			String wLine = null;
			String sLine = null;
			ArrayList<Integer> users = new ArrayList<>(potentials.keySet());
			Collections.sort(users);
			int i = 0;
			while ((dLine = diversityReader.readLine()) != null
					&& (wLine = weightedFreqReader.readLine()) != null
					&& (sLine = socialStrengthReader.readLine()) != null) {
				Integer u = users.get(i);
				String prefix = "" + u;
				if (!dLine.startsWith(prefix)
						|| !wLine.startsWith(prefix)
						|| !sLine.startsWith(prefix)) {
					LOG.error("NOT the same");
					LOG.info(u);
					LOG.info(dLine);
					LOG.info(wLine);
					LOG.info(sLine);
					break;
				}
				i++;
			}
			
			potentialReader.close();
			diversityReader.close();
			weightedFreqReader.close();
			socialStrengthReader.close();
			
		} catch (Exception e) {
			LOG.error("Error calculating EBM model params", e);
		}
	}
}
