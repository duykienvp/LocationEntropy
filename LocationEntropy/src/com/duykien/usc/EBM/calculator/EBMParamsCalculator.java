package com.duykien.usc.EBM.calculator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.duykien.usc.EBM.dataIO.EBMDataIO;
import com.duykien.usc.EBM.datatypes.EBMModelParams;
import com.duykien.usc.locationentropy.locationdata.IntIntDoubleMap;

public class EBMParamsCalculator {
	private static final Logger LOG = Logger.getLogger(EBMParamsCalculator.class);
	
	public static void calculateEBMModelParams(String potentialsFile, 
			String diversityFile, 
			String weightedFreqFile, 
			String socialStrengthFile,
			String modelParamsOutputFile) {
		try {
			Map<Integer, Set<Integer>> potentials = EBMDataIO.readPotentials(potentialsFile);
			BufferedReader diversityReader = new BufferedReader(new FileReader(diversityFile));
			BufferedReader weightedFreqReader = new BufferedReader(new FileReader(weightedFreqFile));
			BufferedReader socialStrengthReader = new BufferedReader(new FileReader(socialStrengthFile));
			
			String dLine = null;
			String wLine = null;
			String sLine = null;
			ArrayList<Integer> users = new ArrayList<>(potentials.keySet());
			Collections.sort(users);
			int userIndex = 0;
			
			double num = 0;
			double sumF = 0;
			double sumF2 = 0;
			double sumD = 0;
			double sumD2 = 0;
			double sumS = 0;
			double sumDs = 0;
			double sumDF = 0;
			double sumFs = 0;
			
			while ((dLine = diversityReader.readLine()) != null
					&& (wLine = weightedFreqReader.readLine()) != null
					&& (sLine = socialStrengthReader.readLine()) != null) {
				Integer i = users.get(userIndex);
				String prefix = "" + i;
				if (!dLine.startsWith(prefix)
						|| !wLine.startsWith(prefix)
						|| !sLine.startsWith(prefix)) {
					LOG.error("NOT the same");
					LOG.info(i);
					LOG.info(dLine);
					LOG.info(wLine);
					LOG.info(sLine);
					break;
				}
				userIndex++;
				
				
				IntIntDoubleMap dMap = EBMDataIO.parseIntIntDoubleLine(dLine);
				IntIntDoubleMap wMap = EBMDataIO.parseIntIntDoubleLine(wLine);
				IntIntDoubleMap sMap = EBMDataIO.parseIntIntDoubleLine(sLine);
				
				for (Integer j: potentials.get(i)) {
					//user i and j
					double Dij = dMap.get(i, j);
					double Fij = wMap.get(i, j);
					double sij = sMap.get(i, j);
					
					num++;
					sumF2 += Fij * Fij;
					sumDs += Dij * sij;
					sumDF += Dij * Fij;
					sumFs += Fij * sij;
					sumD2 += Dij * Dij;
					sumS += sij;
					sumD += Dij;
					sumF += Fij;
				}
			}
			
			double sBar = sumS / num;
			double dBar = sumD / num;
			double fBar = sumF / num;
			
			EBMModelParams params = new EBMModelParams();
			params.alpha = ((sumF2 * sumDs) - (sumDF * sumFs)) / ((sumD2 * sumF2) - (sumDF * sumDF));
			params.beta = ((sumD2 * sumFs) - (sumDF * sumDs)) / ((sumD2 * sumF2) - (sumDF * sumDF));
			params.gamma = sBar - params.alpha * dBar - params.beta * fBar;
			
			diversityReader.close();
			weightedFreqReader.close();
			socialStrengthReader.close();
			
			EBMDataIO.writeModelParams(params, modelParamsOutputFile);
			
		} catch (Exception e) {
			LOG.error("Error calculating EBM model params", e);
		}
	}
}
