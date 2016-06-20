package com.duykien.usc.GIS.entropycalculator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.StringTokenizer;

import com.duykien.usc.GIS.Constants;
import com.duykien.usc.GIS.FileNameUtil;

public class LocationEntropyCalculator {
	
	/**
	 * Calculate location entropy when limiting maximum number of visits of a user to a location by C
	 * 
	 * Input file format:
	 * for each line: locationId,userid1,visits1,userid3,visits2,.....
	 * 
	 * Output file format:
	 * for each line: locationId,num_users,entropy
	 * 
	 * @param inputFile 
	 * @param C maximum number of visits of a user to a location
	 * @param outputFile
	 */
	public static void calLocationEntropy(String inputFile, int C, String outputFile) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(inputFile));
			PrintWriter writer = new PrintWriter(outputFile);
			
			String line = null;
			while ((line = reader.readLine()) != null) {
				//get data of a location: locationId,userid1,visits1,userid3,visits2,.....
				StringTokenizer tokenizer = new StringTokenizer(line, ",");
				int locationId = Integer.parseInt(tokenizer.nextToken());
				
				ArrayList<Double> counts = new ArrayList<>();
				while (tokenizer.hasMoreTokens()) {
					@SuppressWarnings("unused")
					int userid = Integer.parseInt(tokenizer.nextToken());
					
					Double count = (double) Math.min(Integer.parseInt(tokenizer.nextToken()), C);
					
					counts.add(count);
				}
				
				double entropy = calEntropy(counts);
				
				//write data: locationId,num_users,entropy
				writer.println(locationId + "," + counts.size() + "," + entropy); 
			}
			
			writer.close();
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Calculate entropy of numbers 
	 * @param counts
	 * @return
	 */
	public static double calEntropy(ArrayList<Double> counts) {
		if (counts.size() <= 1) { 
			// entropy = 0 if we have only 1 users
			return 0;
		}
		
		//calculate sum
		double sum = 0;
		for (int i = 0; i < counts.size(); i++) {
			sum += counts.get(i);
		}
		
		if (sum < 0.5) { 
			// entropy = 0 if sum = 0 ( < 1). 
			//I donot want to compare == for double numbers
			return 0;
		}
		
		//calculate entropy
		double entropy = 0;
		for (int i = 0; i < counts.size(); i++) {
			double p = counts.get(i) / sum;
			
			entropy += p * Math.log(p);
		}
		
		entropy = -entropy;
		
		return entropy;
	}

	public static void main(String[] args) {
		String prefix = Constants.DATASET_PREFIX;
		int L = Constants.L;
		int N = Constants.N;
		int M = Constants.M;
		int maxC = Constants.MAX_C;
		double ze = Constants.ZIPF_EXPONENT;
		DecimalFormat df = Constants.DOUBLE_FORMAT;
		
		String dataGenerationOutputDir = Constants.DATA_GENERATOR_OUTPUT_DIR;
		String dataGenerationOutputFile = FileNameUtil.getDataGenerationOutputFile(prefix, L, N, M, maxC, ze, df, dataGenerationOutputDir);
		
		int C = Constants.C;
		String locationEntropyOutputFile = FileNameUtil.getLocationEntropyOutputFile(prefix, L, N, M, maxC, ze, df, dataGenerationOutputDir, C);
		
		calLocationEntropy(dataGenerationOutputFile, C, locationEntropyOutputFile);
		System.out.println("Finished");
	}
}
