package com.duykien.usc.GIS;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;

import com.duykien.usc.GIS.DP.DifferentialPrivacyNoisePerturbator;
import com.duykien.usc.GIS.DP.LocationEntropyInfo;
import com.duykien.usc.GIS.DP.DifferentialPrivacyNoisePerturbator.NoisePertubationMethod;
import com.duykien.usc.GIS.entropycalculator.LocationEntropyCalculator;
import com.duykien.usc.GIS.io.VisitingDatasetIO;
import com.duykien.usc.GIS.measure.LEHistogramInfo;
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
			LEHistogramInfo uncutHistogramInfo,
			Map<Integer, ArrayList<Integer>> visitMap) {
		//calculate location entropy for fixed C
		System.out.println("C = " + C + " Started");
		
		String locationEntropyOutputFile = FileNameUtil.getLocationEntropyOutputFile(prefix, L, N, M, maxC, ze, df, dataGenerationOutputDir, C);
		
		ArrayList<LocationEntropyInfo> leInfos = LocationEntropyCalculator.calLocationEntropy(visitMap, C, locationEntropyOutputFile);
		System.out.println("LocationEntropyCalculator Finished");
			
		//Differential privacy 
		
		String sensitivityInputFile = FileNameUtil.getSmoothSensitivityInputFile(dataGenerationOutputDir, C);
		
		String dpOutputFile = FileNameUtil.getDPOutputFile(prefix, L, N, M, maxC, ze, df, dataGenerationOutputDir, C, useMStr, noisePerturbationMethodStr);
		DifferentialPrivacyNoisePerturbator.perturbUnderDP(leInfos, sensitivityInputFile, N, M, C, eps, delta, minSensitivity, useM, noisePertubationMethod, dpOutputFile);
		System.out.println("DifferentialPrivacyNoisePerturbator Finished");
		
		
		String histogramFile = FileNameUtil.getHistogramFileName(prefix, L, N, M, maxC, ze, df, dataGenerationOutputDir, C, useMStr, bucketSize, noisePerturbationMethodStr);
		LEHistogramInfo leHistogramInfo = LocationEntropyDPMeasureHistogramGenerator.generateHistogram(leInfos, N, bucketSize, df, histogramFile);
		
		System.out.println("LocationEntropyDPMeasureHistogramGenerator Finished");
		
		String histogramErrorFile = FileNameUtil.getHistogramErrorFileName(prefix, L, N, M, maxC, ze, df, dataGenerationOutputDir, C, useMStr, bucketSize, noisePerturbationMethodStr);
		
		MeasurementResults results = LocationEntropyDPMeasureHistogramEvaluator.evaluateHistogram(uncutHistogramInfo, leHistogramInfo, histogramErrorFile);

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
		String dataGenerationOutputFile = FileNameUtil.getDataGenerationOutputFile(prefix, L, N, M, maxC, ze, df, dataGenerationOutputDir);
		Map<Integer, ArrayList<Integer>> visitMap = VisitingDatasetIO.readData(dataGenerationOutputFile);
		LEHistogramInfo uncutHistogramInfo = LocationEntropyDPMeasureHistogramEvaluator.readHistogram(uncutHistogramFile);
		try {
			PrintWriter writer = new PrintWriter(testResultFile);
			for (int C = startC; C < endC; C++) {
				MeasurementResults results = runDPLocationEntropy(prefix, L, N, M, maxC, ze, df, dataGenerationOutputDir, eps, delta, minSensitivity, useM, useMStr, bucketSize, C, noisePertubationMethod, noisePerturbationMethodStr, uncutHistogramInfo, visitMap);
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
		String uncutHistogramFile = FileNameUtil.getOriginalHistogramFileName(prefix, L, N, M, maxC, ze, df, dataGenerationOutputDir, bucketSize);
	
		/*
		 * Original, uncut entropy
		 */
		boolean calOriginal = false;
		if (calOriginal) {
			System.out.println("Start calculating original data");
			String dataGenerationOutputFile = FileNameUtil.getDataGenerationOutputFile(prefix, L, N, M, maxC, ze, df, dataGenerationOutputDir);
			Map<Integer, ArrayList<Integer>> visitMap = VisitingDatasetIO.readData(dataGenerationOutputFile);
			String locationEntropyOutputFile = FileNameUtil.getOriginalEntropyFileName(prefix, L, N, M, maxC, ze, df, dataGenerationOutputDir);
			ArrayList<LocationEntropyInfo> leInfos = LocationEntropyCalculator.calLocationEntropy(visitMap, maxC, locationEntropyOutputFile);
			LocationEntropyDPMeasureHistogramGenerator.generateHistogram(leInfos, N, bucketSize, df, uncutHistogramFile);
			System.out.println("Finished calculating original data");
				
		}
		
		int startC = Constants.START_C;
		int endC = Constants.END_C;
		NoisePertubationMethod noisePertubationMethod = Constants.DP_NOISE_PERTURBATION_METHOD;
		String noisePerturbationMethodStr = Constants.DP_NOISE_PERTURBATION_METHOD_STR; 
		
		runTestForAllC(prefix, L, N, M, maxC, ze, df, dataGenerationOutputDir, eps, delta, minSensitivity, useM, useMStr, bucketSize, uncutHistogramFile, startC, endC, noisePertubationMethod, noisePerturbationMethodStr);
	}

}
