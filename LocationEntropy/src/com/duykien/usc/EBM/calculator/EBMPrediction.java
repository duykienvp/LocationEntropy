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
import com.duykien.usc.EBM.datatypes.EBMModelParams;
import com.duykien.usc.locationentropy.locationdata.IntIntDoubleMap;

public class EBMPrediction {
	private static final Logger LOG = Logger.getLogger(EBMPrediction.class);
	
	public static void calculateEBMModelParams(String potentialsFile, 
			String diversityFile, 
			String weightedFreqFile, 
			String paramsFile,
			String predictionOutputFile) {
		try {
			EBMModelParams params = EBMDataIO.readEBMModelParams(paramsFile);
			Map<Integer, Set<Integer>> potentials = EBMDataIO.readPotentials(potentialsFile);
			BufferedReader diversityReader = new BufferedReader(new FileReader(diversityFile));
			BufferedReader weightedFreqReader = new BufferedReader(new FileReader(weightedFreqFile));
			
			PrintWriter writer = new PrintWriter(predictionOutputFile);
			
			String dLine = null;
			String wLine = null;
			ArrayList<Integer> users = new ArrayList<>(potentials.keySet());
			Collections.sort(users);
			int userIndex = 0;
			
			while ((dLine = diversityReader.readLine()) != null
					&& (wLine = weightedFreqReader.readLine()) != null) {
				Integer i = users.get(userIndex);
				String prefix = "" + i;
				if (!dLine.startsWith(prefix)
						|| !wLine.startsWith(prefix)) {
					LOG.error("NOT the same");
					LOG.info(i);
					LOG.info(dLine);
					LOG.info(wLine);
					break;
				}
				userIndex++;
				
				
				IntIntDoubleMap dMap = EBMDataIO.parseIntIntDoubleLine(dLine);
				IntIntDoubleMap wMap = EBMDataIO.parseIntIntDoubleLine(wLine);
				
				writer.write(i + EBMDataIO.USER_SEPARATOR);
				ArrayList<Integer> js = new ArrayList<>(potentials.get(i));
				Collections.sort(js);
				for (Integer j: js) {
					//user i and j
					double Dij = dMap.get(i, j);
					double Fij = wMap.get(i, j);
					
					double pred = params.alpha * Dij + params.beta * Fij + params.gamma;
					writer.write(j + EBMDataIO.COUNT_SEPARATOR
							+ pred + EBMDataIO.USER_SEPARATOR);
				}
				
				writer.println();
			}
			
			diversityReader.close();
			weightedFreqReader.close();	
			writer.close();
		} catch (Exception e) {
			LOG.error("Error calculating EBM model params", e);
		}
	}
}
