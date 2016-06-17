package com.duykien.usc.GIS;

import java.io.PrintWriter;
import java.text.DecimalFormat;

import com.duykien.usc.GIS.DP.DifferentialPrivacyNoisePerturbator;
import com.duykien.usc.GIS.entropycalculator.LocationEntropyCalculator;
import com.duykien.usc.GIS.measure.LocationEntropyDPMeasureHistogramEvaluator;
import com.duykien.usc.GIS.measure.LocationEntropyDPMeasureHistogramGenerator;
import com.duykien.usc.GIS.measure.MeasurementResults;

public class DPLocationEntropy {
	/**
	 * Original entropy is the entropy when C = maxC
	 * @param L
	 * @param N
	 * @param M
	 * @param maxC
	 * @param ze
	 * @param df
	 * @param dataGenerationOutputDir
	 */
	public static void runOriginalEntropy(String dataGenerationOutputFile,
			String locationEntropyOutputFile,
			String histogramFile,
			int N,
			int maxC,
			DecimalFormat df,
			double bucketSize) {
		//calculate location entropy for fixed C
				
		LocationEntropyCalculator.calLocationEntropy(dataGenerationOutputFile, maxC, locationEntropyOutputFile);
		LocationEntropyDPMeasureHistogramGenerator.generateHistogram(locationEntropyOutputFile, N, bucketSize, df, histogramFile);
	}
	
	
	public static MeasurementResults runDPLocationEntropy(
			int L,
			int N,
			int M,
			int maxC,
			double ze,
			DecimalFormat df,
			String dataGenerationOutputDir,
			double eps,
			double delta,
			double minSensitivity,
			boolean useM,
			String useMStr,
			double bucketSize,
			int C,
			String uncutHistogramFile) {
		//calculate location entropy for fixed C
		System.out.println("C = " + C + " Started");
		String dataGenerationOutputFile = FileNameUtil.getDataGenerationOutputFile(L, N, M, maxC, ze, df, dataGenerationOutputDir);
		
		String locationEntropyOutputFile = FileNameUtil.getLocationEntropyOutputFile(L, N, M, maxC, ze, df, dataGenerationOutputDir, C);
		
		LocationEntropyCalculator.calLocationEntropy(dataGenerationOutputFile, C, locationEntropyOutputFile);
		System.out.println("LocationEntropyCalculator Finished");
			
		//Differential privacy 
		
		String sensitivityInputFile = FileNameUtil.getSensitivityInputFile(dataGenerationOutputDir, C);
		
		String dpOutputFile = FileNameUtil.getDPOutputFile(L, N, M, maxC, ze, df, dataGenerationOutputDir, C, useMStr);
		DifferentialPrivacyNoisePerturbator.perturbUnderDP(locationEntropyOutputFile, sensitivityInputFile, N, M, C, eps, delta, minSensitivity, useM, dpOutputFile);
		System.out.println("DifferentialPrivacyNoisePerturbator Finished");
		
		
		String histogramFile = FileNameUtil.getHistogramFileName(L, N, M, maxC, ze, df, dataGenerationOutputDir, C, useMStr, bucketSize);
		LocationEntropyDPMeasureHistogramGenerator.generateHistogram(dpOutputFile, N, bucketSize, df, histogramFile);
		
		System.out.println("LocationEntropyDPMeasureHistogramGenerator Finished");
		
		String histogramErrorFile = FileNameUtil.getHistogramErrorFileName(L, N, M, maxC, ze, df, dataGenerationOutputDir, C, useMStr, bucketSize);
		
		MeasurementResults results = LocationEntropyDPMeasureHistogramEvaluator.evaluateHistogram(uncutHistogramFile, histogramFile, histogramErrorFile);

		System.out.println("LocationEntropyDPMeasureHistogramEvaluator Finished");
		System.out.println("C = " + C + " Finished");
		
		return results;
	}
	
	public static void runTestForAllC(int L,
			int N,
			int M,
			int maxC,
			double ze,
			DecimalFormat df,
			String dataGenerationOutputDir,
			double eps,
			double delta,
			double minSensitivity,
			boolean useM,
			String useMStr,
			double bucketSize,
			String uncutHistogramFile) {
		String testResultFile = FileNameUtil.getTestResultsFileName(L, N, M, maxC, ze, df, dataGenerationOutputDir, useMStr, bucketSize);
		
		try {
			PrintWriter writer = new PrintWriter(testResultFile);
			for (int C = 1; C <= 50; C++) {
				MeasurementResults results = runDPLocationEntropy(L, N, M, maxC, ze, df, dataGenerationOutputDir, eps, delta, minSensitivity, useM, useMStr, bucketSize, C, uncutHistogramFile);
				writer.println(C 
						+ "," + results.klDivergenceNoisyVsCut + "," + results.ksTestValueNoisyVsCut
						+ "," + results.klDivergenceNoisyVsUncut + "," + results.ksTestValueNoisyVsUncut
						+ "," + results.klDivergenceCutVsUncut + "," + results.ksTestValueCutVsUncut); 
			}
			
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
		
		double eps = Constants.DP_EPSILON; 
		double delta = Constants.DP_DELTA;
		double minSensitivity = Constants.MIN_SENSITIVITY;
		
		boolean useM = Constants.USE_M;
		String useMStr = useM ? "" : "NOT";
		
		double bucketSize = Constants.BUCKET_SIZE;
		
		String dataGenerationOutputDir = Constants.DATA_GENERATOR_OUTPUT_DIR;
	
		String dataGenerationOutputFile = FileNameUtil.getDataGenerationOutputFile(L, N, M, maxC, ze, df, dataGenerationOutputDir);
		
		String locationEntropyOutputFile = FileNameUtil.getOriginalEntropyFileName(L, N, M, maxC, ze, df, dataGenerationOutputDir);
				
		LocationEntropyCalculator.calLocationEntropy(dataGenerationOutputFile, maxC, locationEntropyOutputFile);
		
		String uncutHistogramFile = FileNameUtil.getOriginalHistogramFileName(L, N, M, maxC, ze, df, dataGenerationOutputDir, bucketSize);
		LocationEntropyDPMeasureHistogramGenerator.generateHistogram(locationEntropyOutputFile, N, bucketSize, df, uncutHistogramFile);

//		runOriginalEntropy(dataGenerationOutputFile, locationEntropyOutputFile, uncutHistogramFile, N, maxC, df, bucketSize);
		runTestForAllC(L, N, M, maxC, ze, df, dataGenerationOutputDir, eps, delta, minSensitivity, useM, useMStr, bucketSize, uncutHistogramFile);
	}

}
