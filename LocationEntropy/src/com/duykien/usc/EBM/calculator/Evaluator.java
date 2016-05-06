package com.duykien.usc.EBM.calculator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.log4j.Logger;

import com.duykien.usc.EBM.dataIO.EBMDataIO;
import com.duykien.usc.locationentropy.locationdata.IntIntDoubleMap;

public class Evaluator {
	private static final Logger LOG = Logger.getLogger(EBMPrediction.class);
	
	public static void evaluate(String observedFile, String predictedFile, String outputFile) {
		try {
			PrintWriter writer = new PrintWriter(outputFile);
			BufferedReader oReader = new BufferedReader(new FileReader(observedFile));
			BufferedReader pReader = new BufferedReader(new FileReader(predictedFile));
			
			String oLine = null;
			String pLine = null;
			
			double sHatBar = calculateSBar(observedFile);
			double SSTol = 0;
			double SSErr = 0;
			while ((oLine = oReader.readLine()) != null
					&& (pLine = pReader.readLine()) != null) {
				IntIntDoubleMap oMap = EBMDataIO.parseIntIntDoubleLine(oLine);
				IntIntDoubleMap pMap = EBMDataIO.parseIntIntDoubleLine(pLine);
				
				Integer i = Integer.parseInt(oLine.split(EBMDataIO.USER_SEPARATOR)[0]);
				Integer iTmp = Integer.parseInt(pLine.split(EBMDataIO.USER_SEPARATOR)[0]);
				
				if (i.equals(iTmp) == false) {
					LOG.error("Not the same");
					break;
				}
				
				ArrayList<Integer> js = new ArrayList<>(oMap.getSecondEntries(i));
				Collections.sort(js);
				for (Integer j : js) {
					double sHatIJ = oMap.get(i, j);
					double sIJ = pMap.get(i, j);
					
					SSTol += (sHatIJ - sHatBar) * (sHatIJ - sHatBar);
					SSErr += (sHatIJ - sIJ) * (sHatIJ - sIJ);
				}
			}
			
			double R2 = 1 - (SSErr / SSTol);
			writer.println("R2 = " + R2);
			
			writer.close();
			oReader.close();
			pReader.close();
		} catch (Exception e) {
			LOG.error(e, e);
		}
	}
	
	private static double calculateSBar(String observedFile) {
		try {
			BufferedReader oReader = new BufferedReader(new FileReader(observedFile));
			
			String oLine = null;
			
			double num = 0;
			double s = 0;
			while ((oLine = oReader.readLine()) != null) {
				IntIntDoubleMap oMap = EBMDataIO.parseIntIntDoubleLine(oLine);
				
				Integer i = Integer.parseInt(oLine.split(EBMDataIO.USER_SEPARATOR)[0]);
				
				ArrayList<Integer> js = new ArrayList<>(oMap.getSecondEntries(i));
				Collections.sort(js);
				for (Integer j : js) {
					s += oMap.get(i, j);
					num += 1;
				}
			}
			
			oReader.close();
			
			double sBar = s / num;
			return sBar;
		} catch (Exception e) {
			LOG.error(e, e);
			return 0;
		}
	}
}
