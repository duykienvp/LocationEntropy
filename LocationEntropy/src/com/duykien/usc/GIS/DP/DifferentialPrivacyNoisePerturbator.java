package com.duykien.usc.GIS.DP;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.math3.distribution.LaplaceDistribution;

import com.duykien.usc.GIS.Constants;
import com.duykien.usc.GIS.FileNameUtil;

public class DifferentialPrivacyNoisePerturbator {
	
	private static LaplaceDistribution laplaceDistribution = new LaplaceDistribution(0, 1);
		
	/**
	 * Read calculated sensitivity from a file.
	 * Format: each line: num_users,sensitivity
	 * @param inputFile
	 * @return
	 */
	public static Map<Integer, Double> readSensitivity(String inputFile) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(inputFile));
			Map<Integer, Double> sens = new HashMap<>();
			
			String line;
			while ((line = reader.readLine()) != null) {
				StringTokenizer tokenizer = new StringTokenizer(line, ",");
				Integer n = Integer.parseInt(tokenizer.nextToken());
				Double s = Double.parseDouble(tokenizer.nextToken());
				
				sens.put(n, s);
			}
			
			reader.close();
			
			return sens;
		} catch (Exception e) {
			System.out.println("Error reading sensitivity");
			e.printStackTrace();
			return null;
		}
	}
	
	public static double calSmoothSensitivityNoise2ndMethod(double smoothSensitivity, double eps, double m) {
		double eta = laplaceDistribution.sample();
		double noise = (m * 2 * smoothSensitivity * eta) / eps;
		return noise;
	}
	
	/**
	 * Perturb entropy using differential privacy using Smooth Sensitivity, method 2, which creates (epsilon, delta)-DP.
	 * Entropy input file format: locationID,num_users,entropy
	 * Sensitivity input file format: num_users,sensitivity
	 * 
	 * Output file:
	 * Each line: locationID,num_users,entropy,perturbed_entropy,noise
	 * @param entropyInputFile
	 * @param sensitivityInputFile
	 * @param N maximum number of users
	 * @param m
	 * @param c
	 * @param eps
	 * @param delta
	 * @param outputFile
	 */
	public static void perturbUnderDP(String entropyInputFile, 
			String sensitivityInputFile,
			int N,
			int m, 
			int c, 
			double eps, 
			double delta, 
			double minSensitivity,
			boolean useM,
			String outputFile) {
		try {
			Map<Integer, Double> sensitivityMap = readSensitivity(sensitivityInputFile);
			if (sensitivityMap == null) 
				return;
			BufferedReader reader = new BufferedReader(new FileReader(entropyInputFile));
			BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
			
			String line = null;
			while ((line = reader.readLine()) != null) {
				//read entropy of a location
				StringTokenizer tokenizer = new StringTokenizer(line, ",");
				@SuppressWarnings("unused")
				Integer locationId = Integer.parseInt(tokenizer.nextToken());
				
				Integer n = Integer.parseInt(tokenizer.nextToken());
				Double entropy = Double.parseDouble(tokenizer.nextToken());
				entropy = Math.max(0, entropy);
				
				//get sensitivity; if unable, use the min sensitivity 
				Double sensitivity = sensitivityMap.get(n);
				if (sensitivity == null) {
					sensitivity = minSensitivity;
				}
				
				//cal noise
				double multipleLocationMagnitude = useM ? m : 1;
				double noise = calSmoothSensitivityNoise2ndMethod(sensitivity, eps, multipleLocationMagnitude);
				
				//add noise and make sure 0 <= private entropy <= log N
				double privateEntropy = entropy + noise;
				if (Math.log(N) < privateEntropy) {
					privateEntropy = Math.log(N);
					noise = privateEntropy - entropy;
				}
				if (privateEntropy < 0) {
					privateEntropy = 0;
					noise = privateEntropy - entropy;
				}
				
				//write output
				String outputLine = line + "," + privateEntropy + "," + noise;
				writer.write(outputLine);
				writer.newLine();
			
			}
			
			reader.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void main(String[] args) {
		int L = Constants.L;
		int N = Constants.N;
		int M = Constants.M;
		int maxC = Constants.MAX_C;
		double ze = Constants.ZIPF_EXPONENT;
		DecimalFormat df = Constants.DOUBLE_FORMAT;
		
		String dataGenerationOutputDir = Constants.DATA_GENERATOR_OUTPUT_DIR;
		
		int C = Constants.C;
		String locationEntropyOutputFile = FileNameUtil.getLocationEntropyOutputFile(L, N, M, maxC, ze, df, dataGenerationOutputDir, C);
		
		//Differential privacy 
		double eps = Constants.DP_EPSILON; 
		double delta = Constants.DP_DELTA;
		double minSensitivity = Constants.MIN_SENSITIVITY;
		boolean useM = Constants.USE_M;
		String useMStr = useM ? "" : "NOT";
		
		String sensitivityInputFile = FileNameUtil.getSensitivityInputFile(dataGenerationOutputDir, C);
		
		String dpOutputFile = FileNameUtil.getDPOutputFile(L, N, M, maxC, ze, df, dataGenerationOutputDir, C, useMStr);
		perturbUnderDP(locationEntropyOutputFile, sensitivityInputFile, N, M, C, eps, delta, minSensitivity, useM, dpOutputFile);
		System.out.println("Finished");
	}

}
