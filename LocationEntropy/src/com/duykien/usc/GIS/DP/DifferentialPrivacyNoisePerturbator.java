package com.duykien.usc.GIS.DP;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.math3.distribution.LaplaceDistribution;

import com.duykien.usc.GIS.Constants;
import com.duykien.usc.GIS.FileNameUtil;
import com.duykien.usc.GIS.DP.PertubationMethodFactory.NoisePertubationMethod;
import com.duykien.usc.GIS.sensitivitycalculator.SensitivityCalculator;

public class DifferentialPrivacyNoisePerturbator {
	
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
	
	public static void prepareLaplaceDistributionForCrowdBlending(int c, double eps, double m, int k) {
		double sensitivity = SensitivityCalculator.boundByNC(k, c);
		double b = (m * sensitivity) / eps;
		globalLaplaceDistribution = new LaplaceDistribution(0, b);
	}
	
	/**
	 * Perturb entropy using differential privacy using Smooth Sensitivity, method 2, which creates (epsilon, delta)-DP.
	 * Entropy input format: locationID,num_users,entropy
	 * Sensitivity input file format: num_users,sensitivity
	 * 
	 * Output file:
	 * Each line: locationID,num_users,entropy,perturbed_entropy,noise
	 * @param leInfos location entropy infos
	 * @param sensitivityInputFile
	 * @param N maximum number of users
	 * @param m
	 * @param c
	 * @param eps
	 * @param delta
	 * @param useM should we use M in adding noise
	 * @param outputFile
	 */
	public static void perturbUnderDP(ArrayList<LocationEntropyInfo> leInfos, 
			String sensitivityInputFile,
			int N,
			int m, 
			int c, 
			double eps, 
			double delta, 
			double minSensitivity,
			int kCrowd,
			boolean useM,
			NoisePertubationMethod noisePertubationMethod,
			String outputFile) {
		try {
			Map<Integer, Double> sensitivityMap = null;
			if (noisePertubationMethod == NoisePertubationMethod.LIMIT_SS) {
				sensitivityMap = readSensitivity(sensitivityInputFile);
			}
			BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
			
			double multipleLocationMagnitude = useM ? m : 1;
			
			if (noisePertubationMethod == NoisePertubationMethod.LIMIT)
				prepareLaplaceDistribution(c, eps, multipleLocationMagnitude);
			if (noisePertubationMethod == NoisePertubationMethod.LIMIT_CROWD) 
				prepareLaplaceDistributionForCrowdBlending(c, eps, multipleLocationMagnitude, kCrowd);
				
			
			for (int i = 0; i < leInfos.size(); i++) {
				LocationEntropyInfo leInfo = leInfos.get(i);
				
				Integer n = leInfo.getNumUser();
				Double entropy = leInfo.getEntropy();
				entropy = Math.max(0, entropy);
				
				//cal noise
				double noise = 0;
				if (noisePertubationMethod == NoisePertubationMethod.LIMIT_SS) {
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
				
				//k-crowd blending
				if ((noisePertubationMethod == NoisePertubationMethod.LIMIT_CROWD)
						&& (n < kCrowd)) {
					privateEntropy = 0;
					noise = entropy;
				}
				
				leInfo.setPrivateEntropy(privateEntropy);
				leInfo.setNoise(noise);
				
				//write output
				String outputLine = leInfo.getLocationId() 
						+ "," + n
						+ "," + entropy
						+ "," + privateEntropy 
						+ "," + noise;
				writer.write(outputLine);
				writer.newLine();
			
			}
			
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Note: not used now
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
	public static void perturbUnderDPDisabled(String entropyInputFile, 
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
			if (noisePertubationMethod == NoisePertubationMethod.LIMIT_SS) {
				sensitivityMap = readSensitivity(sensitivityInputFile);
			}
			BufferedReader reader = new BufferedReader(new FileReader(entropyInputFile));
			BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
			
			double multipleLocationMagnitude = useM ? m : 1;
			
			if (noisePertubationMethod == NoisePertubationMethod.LIMIT)
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
				if (noisePertubationMethod == NoisePertubationMethod.LIMIT_SS) {
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
		String epsStr = DPUtil.toEpsilonString(eps);
		
		String sensitivityInputFile = FileNameUtil.getSmoothSensitivityInputFile(dataGenerationOutputDir, C, epsStr);
		
		NoisePertubationMethod noisePertubationMethod = NoisePertubationMethod.LIMIT_SS;
		String dpOutputFile = FileNameUtil.getDPOutputFile(prefix, L, N, M, maxC, ze, df, dataGenerationOutputDir, C, epsStr, useMStr, noisePertubationMethod.toString());
		perturbUnderDPDisabled(locationEntropyOutputFile, sensitivityInputFile, N, M, C, eps, delta, minSensitivity, useM, noisePertubationMethod,  dpOutputFile);
		System.out.println("Finished");
	}

}
