package com.duykien.usc.GIS;

import java.io.PrintWriter;
import java.text.DecimalFormat;

import com.duykien.usc.GIS.DP.DifferentialPrivacyNoisePerturbator;
import com.duykien.usc.GIS.DP.DifferentialPrivacyNoisePerturbator.NoisePertubationMethod;
import com.duykien.usc.GIS.entropycalculator.LocationEntropyCalculator;
import com.duykien.usc.GIS.measure.LocationEntropyDPMeasureHistogramEvaluator;
import com.duykien.usc.GIS.measure.LocationEntropyDPMeasureHistogramGenerator;
import com.duykien.usc.GIS.measure.MeasurementResults;

public class DPLocationEntropy {

	public static MeasurementResults runDPLocationEntropy(String prefix,
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
			NoisePertubationMethod noisePertubationMethod,
			String noisePerturbationMethodStr,
			String uncutHistogramFile) {
		//calculate location entropy for fixed C
		System.out.println("C = " + C + " Started");
		String dataGenerationOutputFile = FileNameUtil.getDataGenerationOutputFile(prefix, L, N, M, maxC, ze, df, dataGenerationOutputDir);
		
		String locationEntropyOutputFile = FileNameUtil.getLocationEntropyOutputFile(prefix, L, N, M, maxC, ze, df, dataGenerationOutputDir, C);
		
		LocationEntropyCalculator.calLocationEntropy(dataGenerationOutputFile, C, locationEntropyOutputFile);
		System.out.println("LocationEntropyCalculator Finished");
			
		//Differential privacy 
		
		String sensitivityInputFile = FileNameUtil.getSmoothSensitivityInputFile(dataGenerationOutputDir, C);
		
		String dpOutputFile = FileNameUtil.getDPOutputFile(prefix, L, N, M, maxC, ze, df, dataGenerationOutputDir, C, useMStr, noisePerturbationMethodStr);
		DifferentialPrivacyNoisePerturbator.perturbUnderDP(locationEntropyOutputFile, sensitivityInputFile, N, M, C, eps, delta, minSensitivity, useM, noisePertubationMethod, dpOutputFile);
		System.out.println("DifferentialPrivacyNoisePerturbator Finished");
		
		
		String histogramFile = FileNameUtil.getHistogramFileName(prefix, L, N, M, maxC, ze, df, dataGenerationOutputDir, C, useMStr, bucketSize, noisePerturbationMethodStr);
		LocationEntropyDPMeasureHistogramGenerator.generateHistogram(dpOutputFile, N, bucketSize, df, histogramFile);
		
		System.out.println("LocationEntropyDPMeasureHistogramGenerator Finished");
		
		String histogramErrorFile = FileNameUtil.getHistogramErrorFileName(prefix, L, N, M, maxC, ze, df, dataGenerationOutputDir, C, useMStr, bucketSize, noisePerturbationMethodStr);
		
		MeasurementResults results = LocationEntropyDPMeasureHistogramEvaluator.evaluateHistogram(uncutHistogramFile, histogramFile, histogramErrorFile);

		System.out.println("LocationEntropyDPMeasureHistogramEvaluator Finished");
		System.out.println("C = " + C + " Finished");
		
		return results;
	}
	
	public static void runTestForAllC(String prefix,
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
			String uncutHistogramFile,
			int startC,
			int endC,
			NoisePertubationMethod noisePertubationMethod,
			String noisePerturbationMethodStr) {
		String testResultFile = FileNameUtil.getTestResultsFileName(prefix, L, N, M, maxC, ze, df, dataGenerationOutputDir, useMStr, bucketSize, noisePerturbationMethodStr);
		
		try {
			PrintWriter writer = new PrintWriter(testResultFile);
			for (int C = startC; C < endC; C++) {
				MeasurementResults results = runDPLocationEntropy(prefix, L, N, M, maxC, ze, df, dataGenerationOutputDir, eps, delta, minSensitivity, useM, useMStr, bucketSize, C, noisePertubationMethod, noisePerturbationMethodStr, uncutHistogramFile);
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
		String prefix = Constants.DATASET_PREFIX;
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
	
		/*
		 * Original, uncut entropy
		 */
		/*
		String dataGenerationOutputFile = FileNameUtil.getDataGenerationOutputFile(L, N, M, maxC, ze, df, dataGenerationOutputDir);
		String locationEntropyOutputFile = FileNameUtil.getOriginalEntropyFileName(L, N, M, maxC, ze, df, dataGenerationOutputDir);
		LocationEntropyCalculator.calLocationEntropy(dataGenerationOutputFile, maxC, locationEntropyOutputFile);
		LocationEntropyDPMeasureHistogramGenerator.generateHistogram(locationEntropyOutputFile, N, bucketSize, df, uncutHistogramFile);
		runOriginalEntropy(dataGenerationOutputFile, locationEntropyOutputFile, uncutHistogramFile, N, maxC, df, bucketSize);
		*/
		
		int startC = Constants.START_C;
		int endC = Constants.END_C;
		NoisePertubationMethod noisePertubationMethod = Constants.DP_NOISE_PERTURBATION_METHOD;
		String noisePerturbationMethodStr = Constants.DP_NOISE_PERTURBATION_METHOD_STR; 
		String uncutHistogramFile = FileNameUtil.getOriginalHistogramFileName(prefix, L, N, M, maxC, ze, df, dataGenerationOutputDir, bucketSize);
		runTestForAllC(prefix, L, N, M, maxC, ze, df, dataGenerationOutputDir, eps, delta, minSensitivity, useM, useMStr, bucketSize, uncutHistogramFile, startC, endC, noisePertubationMethod, noisePerturbationMethodStr);
	}

}
