package com.duykien.usc.GIS.measure;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.StringTokenizer;

import com.duykien.usc.GIS.Constants;
import com.duykien.usc.GIS.FileNameUtil;
import com.duykien.usc.GIS.DP.DPUtil;
import com.duykien.usc.GIS.DP.LocationEntropyInfo;

public class LocationEntropyDPMeasureHistogramGenerator {
	
	/**
	 * Calculate bucket index when using buckets
	 * @param bucketSize
	 * @param value
	 * @return
	 */
	public static int calBucketIndex(double bucketSize, double value) {
		if (value < 0)
			return 0;
		return (int)Math.floor(value / bucketSize);
	}
	
	/**
	 * Histogram generator:
	 * 	- divide output space [0, logN] to buckets of size bucketSize
	 *  - output number of times entropy values jumped to a particular bucket
	 * Output format:
	 *  - Each line: bucket_value,orginal_count,noisy_count,cumulative_original_count,cumulative_noisy_count
	 * @param inputFile
	 */
	public static LEHistogramInfo generateHistogram(ArrayList<LocationEntropyInfo> leInfos, int N, double bucketSize, DecimalFormat df, String histogramOutputFile) {
		
		try {
			int maxBucketIndex = calBucketIndex(bucketSize, Math.log(N));
			int[] histogramOrg = new int[maxBucketIndex + 1];
			for (int i = 0; i < maxBucketIndex + 1; i++)
				histogramOrg[i] = 0;
			
			int[] histogramNoise = new int[maxBucketIndex + 1];
			for (int i = 0; i < maxBucketIndex + 1; i++)
				histogramNoise[i] = 0;
			
			for (int i = 0; i < leInfos.size(); i++) {
				LocationEntropyInfo leInfo = leInfos.get(i);
				
				int orgBucket = calBucketIndex(bucketSize, leInfo.getEntropy());
				int noiseBuckey = calBucketIndex(bucketSize, leInfo.getPrivateEntropy()); 
				
				histogramOrg[orgBucket]++;
				histogramNoise[noiseBuckey]++;
			}		
			
			writeHistogram(bucketSize, df, histogramOutputFile, histogramOrg, histogramNoise);
			
			//Calculate CDF
			return calHistogramInfo(bucketSize, histogramOrg, histogramNoise);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Note: not used now
	 * Histogram generator:
	 * 	- divide output space [0, logN] to buckets of size bucketSize
	 *  - output number of times entropy values jumped to a particular bucket
	 * Output format:
	 *  - Each line: bucket_value,orginal_count,noisy_count,cumulative_original_count,cumulative_noisy_count
	 * @param inputFile
	 */
	public static void generateHistogramDisabled(String inputFile, int N, double bucketSize, DecimalFormat df, String histogramOutputFile) {
		
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
			while ((line = reader.readLine()) != null) {
				StringTokenizer tokenizer = new StringTokenizer(line, ",");
				@SuppressWarnings("unused")
				Integer locationId = Integer.parseInt(tokenizer.nextToken());
				@SuppressWarnings("unused")
				Integer n = Integer.parseInt(tokenizer.nextToken());
				Double entropy = Double.parseDouble(tokenizer.nextToken());
				Double privateEntropy = entropy;
				if (tokenizer.hasMoreTokens())
					privateEntropy = Double.parseDouble(tokenizer.nextToken());
				
				int orgBucket = calBucketIndex(bucketSize, entropy);
				int noiseBuckey = calBucketIndex(bucketSize, privateEntropy); 
				
				histogramOrg[orgBucket]++;
				histogramNoise[noiseBuckey]++;
			}
			reader.close();
			
			
			writeHistogram(bucketSize, df, histogramOutputFile, histogramOrg, histogramNoise);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public static LEHistogramInfo calHistogramInfo(double bucketSize, int[] histogramOrg, int[] histogramNoise) {
		LEHistogramInfo info = new LEHistogramInfo();
		double sumCount = 0;
		for (int i = 0; i < histogramNoise.length; i++) 
			sumCount += histogramNoise[i];
		
		double orgCDF = 0;
		double noisyCDF = 0;
		for (int i = 0; i < histogramNoise.length; i++) {
			double bucketValue = i * bucketSize;
			
			orgCDF += (double)histogramOrg[i] / sumCount;
			noisyCDF += (double)histogramNoise[i] / sumCount;
			
			info.getBucketIndices().add(bucketValue);
			info.getOrgCount().add(histogramOrg[i]);
			info.getNoisyCount().add(histogramNoise[i]);
			info.getOrigCDF().add(new Double(orgCDF));
			info.getNoisyCDF().add(new Double(noisyCDF));
		}
		
		return info;
	}

	/**
	 * Write histogram.
	 * Format: bucket_value,orginal_count,noisy_count,original_CDF,noisy_CDF
	 * @param outputFile
	 * @param histogramOrg
	 * @param histogramNoise
	 */
	public static void writeHistogram(double bucketSize,
			DecimalFormat df,
			String outputFile, int[] histogramOrg, int[] histogramNoise) {
		try {
			PrintWriter writer = new PrintWriter(outputFile);
			double sumCount = 0;
			for (int i = 0; i < histogramNoise.length; i++) 
				sumCount += histogramNoise[i];
			
			double orgCDF = 0;
			double noisyCDF = 0;
			for (int i = 0; i < histogramNoise.length; i++) {
				double bucketValue = i * bucketSize;
				
				orgCDF += (double)histogramOrg[i] / sumCount;
				noisyCDF += (double)histogramNoise[i] / sumCount;
				
				writer.println(df.format(bucketValue) + 
						"," + histogramOrg[i] + 
						"," + histogramNoise[i] + 
						"," + orgCDF + 
						"," + noisyCDF);
			}
			
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
		
		//Differential privacy 
		boolean useM = false;
		String useMStr = useM ? "" : "NOT";
		String epsStr = DPUtil.toEpsilonString(Constants.DP_EPSILON);
		
		String noisePerturbationMethodStr = Constants.DP_NOISE_PERTURBATION_METHOD.toString(); 
		
		String dpOutputFile = FileNameUtil.getDPOutputFile(prefix, L, N, M, maxC, ze, df, dataGenerationOutputDir, C, epsStr, useMStr, noisePerturbationMethodStr);
		
		double bucketSize = Constants.BUCKET_SIZE;
		String histogramFile = FileNameUtil.getHistogramFileName(prefix, L, N, M, maxC, ze, df, dataGenerationOutputDir, C, epsStr, useMStr, bucketSize, noisePerturbationMethodStr);
		generateHistogramDisabled(dpOutputFile, N, bucketSize, df, histogramFile);
		
		System.out.println("Finished");
	}
}
