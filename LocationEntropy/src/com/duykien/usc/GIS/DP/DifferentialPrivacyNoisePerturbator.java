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
import com.duykien.usc.GIS.sensitivitycalculator.SensitivityCalculator;

public class DifferentialPrivacyNoisePerturbator {
	public enum NoisePertubationMethod {
		GLOBAL_LAPLACE, SMOOTH_SENSITIVITY_2ND_METHOD
	}
	
	private static LaplaceDistribution laplaceDistribution = new LaplaceDistribution(0, 1);
	private static LaplaceDistribution globalLaplaceDistribution = new LaplaceDistribution(0, 1);
		
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
	 * Sample a noise 
	 * @param c
	 * @param eps
	 * @param multipleLocationMagnitude
	 * @return
	 */
	public static double calGlobalLaplaceNoise(int c, double eps, double multipleLocationMagnitude) {
		return globalLaplaceDistribution.sample();
	}
	
	public static void prepareLaplaceDistribution(int c, double eps, double m) {
		double b = (m * SensitivityCalculator.boundByC(c)) / eps;
		globalLaplaceDistribution = new LaplaceDistribution(0, b);
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
	 * @param useM should we use M in adding noise
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
			NoisePertubationMethod noisePertubationMethod,
			String outputFile) {
		try {
			Map<Integer, Double> sensitivityMap = null;
			if (noisePertubationMethod == NoisePertubationMethod.SMOOTH_SENSITIVITY_2ND_METHOD) {
				sensitivityMap = readSensitivity(sensitivityInputFile);
			}
			BufferedReader reader = new BufferedReader(new FileReader(entropyInputFile));
			BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
			
			double multipleLocationMagnitude = useM ? m : 1;
			
			if (noisePertubationMethod == NoisePertubationMethod.GLOBAL_LAPLACE)
				prepareLaplaceDistribution(c, eps, multipleLocationMagnitude);
				
			
			String line = null;
			while ((line = reader.readLine()) != null) {
				//read entropy of a location
				StringTokenizer tokenizer = new StringTokenizer(line, ",");
				@SuppressWarnings("unused")
				Integer locationId = Integer.parseInt(tokenizer.nextToken());
				
				Integer n = Integer.parseInt(tokenizer.nextToken());
				Double entropy = Double.parseDouble(tokenizer.nextToken());
				entropy = Math.max(0, entropy);
				
				//cal noise
				double noise = 0;
				if (noisePertubationMethod == NoisePertubationMethod.SMOOTH_SENSITIVITY_2ND_METHOD) {
					//smooth sensitivity
					//get sensitivity; if unable, use the min sensitivity 
					Double sensitivity = sensitivityMap.get(n);
					if (sensitivity == null) {
						sensitivity = minSensitivity;
					}
					noise = calSmoothSensitivityNoise2ndMethod(sensitivity, eps, multipleLocationMagnitude);
				} else {
					//default: GLOBAL_LAPLACE
					noise = calGlobalLaplaceNoise(c, eps, multipleLocationMagnitude);
				}
				
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
		String prefix = Constants.DATASET_PREFIX;
		int L = Constants.L;
		int N = Constants.N;
		int M = Constants.M;
		int maxC = Constants.MAX_C;
		double ze = Constants.ZIPF_EXPONENT;
		DecimalFormat df = Constants.DOUBLE_FORMAT;
		
		String dataGenerationOutputDir = Constants.DATA_GENERATOR_OUTPUT_DIR;
		
		int C = Constants.C;
		String locationEntropyOutputFile = FileNameUtil.getLocationEntropyOutputFile(prefix, L, N, M, maxC, ze, df, dataGenerationOutputDir, C);
		
		//Differential privacy 
		double eps = Constants.DP_EPSILON; 
		double delta = Constants.DP_DELTA;
		double minSensitivity = Constants.MIN_SENSITIVITY;
		boolean useM = Constants.USE_M;
		String useMStr = useM ? "" : "NOT";
		
		String sensitivityInputFile = FileNameUtil.getSmoothSensitivityInputFile(dataGenerationOutputDir, C);
		
		NoisePertubationMethod noisePertubationMethod = NoisePertubationMethod.SMOOTH_SENSITIVITY_2ND_METHOD;
		String noisePerturbationMethodStr = Constants.DP_NOISE_PERTURBATION_METHOD_STR; 
		String dpOutputFile = FileNameUtil.getDPOutputFile(prefix, L, N, M, maxC, ze, df, dataGenerationOutputDir, C, useMStr, noisePerturbationMethodStr);
		perturbUnderDP(locationEntropyOutputFile, sensitivityInputFile, N, M, C, eps, delta, minSensitivity, useM, noisePertubationMethod,  dpOutputFile);
		System.out.println("Finished");
	}

}
