package com.duykien.usc.locationentropy.DP;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;

import org.apache.commons.math3.distribution.LaplaceDistribution;

import com.duykien.usc.locationentropy.util.Util;

public class DifferentialPrivacyNoisePerturbator {
	
	private static LaplaceDistribution laplaceDistribution = new LaplaceDistribution(0, 1);
	
	private static DecimalFormat df = new DecimalFormat("0.0"); 
	
	/**
	 * Read calculated sensitivity from a file.
	 * Format: each line: num_users,<sensitivity>
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
	 * Test differential privacy using Smooth Sensitivity, method 2, which creates (epsilon, delta)-DP.
	 * Entropy input file format: locationID,num_users,<entropy>
	 * Sensitivity input file format: num_users,<sensitivity>
	 * @param entropyInputFile
	 * @param sensitivityInputFile
	 * @param N maximum number of users
	 * @param m
	 * @param c
	 * @param eps
	 * @param delta
	 * @param outputFile
	 */
	public static void testDP(String entropyInputFile, 
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
			double averageRelativeError = 0;
			double averageNoise = 0;
			int numLocations = 0;
			while ((line = reader.readLine()) != null) {
				//read entropy of a location
				StringTokenizer tokenizer = new StringTokenizer(line, ",");
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
				
				//add noise
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
				
				//calculate errors
//				averageRelativeError += (Math.abs(entropy - privateEntropy) / entropy);
				averageNoise += noise;
				numLocations ++;
			}
			
			reader.close();
			writer.close();
			
			averageNoise /= numLocations;
			System.out.println(entropyInputFile + "_eps" + eps + "_delta" + delta + "_minSensitivity" + minSensitivity + ": "
					+ "averageRelativeError=" + averageRelativeError + ", averageNoise=" + averageNoise);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static int calBucketIndex(double bucketSize, double value) {
		if (value < 0)
			return 0;
		return (int)Math.floor(value / bucketSize);
	}
	
	/**
	 * Some measurements:
	 * 	- divide output space [0, logN] to buckets of size bucketSize, and check how many times output wrong butkets
	 * @param inputFile
	 */
	public static void testMeasurements(String inputFile, int N, double bucketSize, String histogramOutputFile) {
		
		try {
			int maxBucketIndex = calBucketIndex(bucketSize, Math.log(N));
			int[] histogramOrg = new int[maxBucketIndex + 1];
			for (int i = 0; i < maxBucketIndex + 1; i++)
				histogramOrg[i] = 0;
			
			int[] histogramNoise = new int[maxBucketIndex + 1];
			for (int i = 0; i < maxBucketIndex + 1; i++)
				histogramNoise[i] = 0;
			
			BufferedReader reader = new BufferedReader(new FileReader(inputFile));
			String line = null;
			int numLocations = 0;
			int numIncorrectBucket = 0;
			while ((line = reader.readLine()) != null) {
				StringTokenizer tokenizer = new StringTokenizer(line, ",");
				Integer locationId = Integer.parseInt(tokenizer.nextToken());
				Integer n = Integer.parseInt(tokenizer.nextToken());
				Double entropy = Double.parseDouble(tokenizer.nextToken());
				Double privateEntropy = Double.parseDouble(tokenizer.nextToken());
				Double noise = Double.parseDouble(tokenizer.nextToken());
				
				numLocations++;
				int orgBucket = calBucketIndex(bucketSize, entropy);
				int noiseBuckey = calBucketIndex(bucketSize, privateEntropy); 
				if (orgBucket != noiseBuckey)
					numIncorrectBucket++;
				
				histogramOrg[orgBucket]++;
				histogramNoise[noiseBuckey]++;
			}
			reader.close();
			
			double percentage = (double) numIncorrectBucket / (double) numLocations;
			
			writeHistogram(histogramOutputFile, histogramOrg, histogramNoise);
			
			System.out.println(inputFile + ", bucketSize = " + bucketSize 
					+ ", numIncorrectBucket=" + numIncorrectBucket 
					+ ", percentage= " + percentage 
					+ ", KL-divergence = " + klDivergence(histogramNoise, histogramOrg));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    /**
     * Returns the KL divergence, K(p1 || p2).
     *
     * The log is w.r.t. base e. <p>
     *
     * *Note*: If any value in <tt>p2</tt> is <tt>0.0</tt> then the KL-divergence
     * is <tt>infinite</tt>. Limin changes it to zero instead of infinite. 
     * 
     */
    public static double klDivergence(int[] c1, int[] c2) {      
      double s1 = 0;
      for (int i = 0; i < c1.length; i++)
    	  s1 += c1[i];
      double[] p1 = new double[c1.length];
      for (int i = 0; i < c1.length; i++)
    	  p1[i] = (double) c1[i] / s1;
      
      double s2 = 0;
      for (int i = 0; i < c2.length; i++)
    	  s2 += c2[i];
      double[] p2 = new double[c2.length];
      for (int i = 0; i < c2.length; i++)
    	  p2[i] = (double) c2[i];

      return klDivergence(p1, p2); // moved this division out of the loop -DM
    }
    
    /**
     * Returns the KL divergence, K(p1 || p2).
     *
     * The log is w.r.t. base e. <p>
     *
     * *Note*: If any value in <tt>p2</tt> is <tt>0.0</tt> then the KL-divergence
     * is <tt>infinite</tt>. Limin changes it to zero instead of infinite. 
     * 
     */
    public static double klDivergence(double[] p1, double[] p2) {


      double klDiv = 0.0;

      for (int i = 0; i < p1.length; ++i) {
        if (p1[i] < Util.PRECISION) { continue; }
        if (p2[i] < Util.PRECISION) { continue; } // Limin

      klDiv += p1[i] * Math.log(p1[i] / p2[i] );
      }

      return klDiv; // moved this division out of the loop -DM
    }
	
	/**
	 * Write histogram.
	 * Format: index,#original,#noise 
	 * @param outputFile
	 * @param histogramOrg
	 * @param histogramNoise
	 */
	public static void writeHistogram(String outputFile, int[] histogramOrg, int[] histogramNoise) {
		try {
			PrintWriter writer = new PrintWriter(outputFile);
			
			for (int i = 0; i < histogramNoise.length; i++) {
				writer.println(i + "," + histogramOrg[i] + "," + histogramNoise[i]);
			}
			
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int L = 10000;
		int N = 1000000;
		int M = 10;
		int C = 10;
		int maxC = 100;
		double eps = Math.log(10); 
		double delta = 1e-7;
		double ze = 1;
		double minSensitivity = 1e-3;
		boolean useM = false;
		String useMStr = useM ? "" : "NOT";
		String inputDir = "/Users/kiennd/Downloads/location_entropy_data/";
		String filenamePrefix = inputDir + "synthetic_data_L" + L + "_N" + N + "_M" + M + "_maxC" + maxC + "_C" + C + "_ze" + df.format(ze);
		String entropyInputFile = filenamePrefix + ".csv";
		String sensitivityInputFile = inputDir + "varyNfixedC"+ C + "SmoothSensitivityEpsLn10Delta1e-7.csv";
		
		String dpFilePrefix = filenamePrefix + "_DP_SS_epsLn10_delta1e-7_minSen1e-3_" + useMStr +"useM";
		String outputFile = dpFilePrefix + ".csv";;
//		testDP(entropyInputFile, sensitivityInputFile, N, M, C, eps, delta, minSensitivity, useM, outputFile);
		
		double bucketSize1 = 0.1;
		double bucketSize2 = 0.2;
		String histogramFile1 = dpFilePrefix +"_histogram_bucketSize" + df.format(bucketSize1) +".csv";
		String histogramFile2 = dpFilePrefix +"_histogram_bucketSize" + df.format(bucketSize2) +".csv";
		testMeasurements(outputFile, N, bucketSize1, histogramFile1);
		testMeasurements(outputFile, N, bucketSize2, histogramFile2);
	}

}
