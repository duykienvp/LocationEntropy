package com.duykien.usc.GIS;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;

import com.duykien.usc.GIS.DP.DPUtil;
import com.duykien.usc.GIS.DP.DifferentialPrivacyNoisePerturbator;
import com.duykien.usc.GIS.DP.LocationEntropyInfo;
import com.duykien.usc.GIS.DP.DifferentialPrivacyNoisePerturbator.NoisePertubationMethod;
import com.duykien.usc.GIS.entropycalculator.LocationEntropyCalculator;
import com.duykien.usc.GIS.io.LocationEntropyIO;
import com.duykien.usc.GIS.io.VisitingDatasetIO;
import com.duykien.usc.GIS.measure.LEHistogramInfo;
import com.duykien.usc.GIS.measure.LocationEntropyDPMeasureEntropyEvaluator;
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
			ArrayList<LocationEntropyInfo> rawLocationEntropyList,
			Map<Integer, ArrayList<Integer>> visitMap) {
		//calculate location entropy for fixed C		
		String locationEntropyOutputFile = FileNameUtil.getLocationEntropyOutputFile(prefix, L, N, M, maxC, ze, df, dataGenerationOutputDir, C);
		
		ArrayList<LocationEntropyInfo> leInfos = LocationEntropyCalculator.calLocationEntropy(visitMap, C, locationEntropyOutputFile);
		System.out.println("LocationEntropyCalculator Finished");
			
		//Differential privacy 
		String epsStr = DPUtil.toEpsilonString(eps);
		String sensitivityInputFile = FileNameUtil.getSmoothSensitivityInputFile(dataGenerationOutputDir, C, epsStr);
		
		String dpOutputFile = FileNameUtil.getDPOutputFile(prefix, L, N, M, maxC, ze, df, dataGenerationOutputDir, C, epsStr, useMStr, noisePerturbationMethodStr);
		DifferentialPrivacyNoisePerturbator.perturbUnderDP(leInfos, sensitivityInputFile, N, M, C, eps, delta, minSensitivity, useM, noisePertubationMethod, dpOutputFile);
		System.out.println("DifferentialPrivacyNoisePerturbator Finished");
		
		//evaluate histogram
		String histogramFile = FileNameUtil.getHistogramFileName(prefix, L, N, M, maxC, ze, df, dataGenerationOutputDir, C, epsStr, useMStr, bucketSize, noisePerturbationMethodStr);
		LEHistogramInfo leHistogramInfo = LocationEntropyDPMeasureHistogramGenerator.generateHistogram(leInfos, N, bucketSize, df, histogramFile);
		
		System.out.println("LocationEntropyDPMeasureHistogramGenerator Finished");
		
		String histogramErrorFile = FileNameUtil.getHistogramErrorFileName(prefix, L, N, M, maxC, ze, df, dataGenerationOutputDir, C, epsStr, useMStr, bucketSize, noisePerturbationMethodStr);
		
		LocationEntropyDPMeasureHistogramEvaluator.evaluateHistogram(uncutHistogramInfo, leHistogramInfo, histogramErrorFile);
		System.out.println("LocationEntropyDPMeasureHistogramEvaluator Finished");
		
		//evaluate raw location entropy
		MeasurementResults results = LocationEntropyDPMeasureEntropyEvaluator.evaluateEntropy(rawLocationEntropyList, leInfos, bucketSize);

		System.out.println("LocationEntropyDPMeasureEntropyEvaluator Finished");
		
		System.out.println("One test Finished");
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
			String noisePerturbationMethodStr,
			String rawLocationEntropyFile) {
		ArrayList<LocationEntropyInfo> rawLocationEntropyList = LocationEntropyIO.readLocationEntropy(rawLocationEntropyFile);
		String epsStr = DPUtil.toEpsilonString(eps);
		String testResultFile = FileNameUtil.getTestResultsFileName(prefix, L, N, M, maxC, ze, df, epsStr, dataGenerationOutputDir, useMStr, bucketSize, noisePerturbationMethodStr);
		String dataGenerationOutputFile = FileNameUtil.getDataGenerationOutputFile(prefix, L, N, M, maxC, ze, df, dataGenerationOutputDir);
		Map<Integer, ArrayList<Integer>> visitMap = VisitingDatasetIO.readData(dataGenerationOutputFile);
		LEHistogramInfo uncutHistogramInfo = LocationEntropyDPMeasureHistogramEvaluator.readHistogram(uncutHistogramFile);
		try {
			PrintWriter writer = new PrintWriter(testResultFile);
			for (int C = startC; C < endC; C++) {
				MeasurementResults results = runDPLocationEntropy(prefix, L, N, M, maxC, ze, df, dataGenerationOutputDir, eps, delta, minSensitivity, useM, useMStr, bucketSize, C, noisePertubationMethod, noisePerturbationMethodStr, uncutHistogramInfo, rawLocationEntropyList, visitMap);
				writer.println(C 
						+ "," + results.klDivergencePrivateVsLimited + "," + results.ksTestValuePrivateVsLimited
						+ "," + results.klDivergencePrivateVsActual + "," + results.ksTestValuePrivateVsActual
						+ "," + results.klDivergenceLimitedVsActual + "," + results.ksTestValueLimitedVsActual); 
			}
			
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void runTestFixMAndCVaryEpsilon(String prefix,
			int L,
			int N,
			int M,
			int maxM,
			int maxC,
			double ze,
			DecimalFormat df,
			String dataGenerationOutputDir,
			double delta,
			double minSensitivity,
			boolean useM,
			String useMStr,
			double bucketSize,
			String uncutHistogramFile,
			int C,
			NoisePertubationMethod noisePertubationMethod,
			String noisePerturbationMethodStr,
			String rawLocationEntropyFile,
			double[] epsilons) {
		ArrayList<LocationEntropyInfo> rawLocationEntropyList = LocationEntropyIO.readLocationEntropy(rawLocationEntropyFile);
		String dataGenerationOutputFile = FileNameUtil.getDataGenerationOutputFile(prefix, L, N, M, maxC, ze, df, dataGenerationOutputDir);
		Map<Integer, ArrayList<Integer>> visitMap = VisitingDatasetIO.readData(dataGenerationOutputFile);
		LEHistogramInfo uncutHistogramInfo = LocationEntropyDPMeasureHistogramEvaluator.readHistogram(uncutHistogramFile);
		try {
			String allEpsilonResultFile = FileNameUtil.getTestResultsFileName(prefix, L, N, M, maxC, ze, df, Constants.DP_ALL_EPSILON_STR, dataGenerationOutputDir, useMStr, bucketSize, noisePerturbationMethodStr);
			PrintWriter allEpsilonWriter = new PrintWriter(allEpsilonResultFile); 
			
			for (int i = 0; i < epsilons.length; i++) {
				double eps = epsilons[i];
				String epsStr = DPUtil.toEpsilonString(eps);
				String testResultFile = FileNameUtil.getTestResultsFileName(prefix, L, N, M, maxC, ze, df, epsStr, dataGenerationOutputDir, useMStr, bucketSize, noisePerturbationMethodStr);
				PrintWriter writer = new PrintWriter(testResultFile);
				MeasurementResults results = runDPLocationEntropy(prefix, L, N, M, maxC, ze, df, dataGenerationOutputDir, eps, delta, minSensitivity, useM, useMStr, bucketSize, C, noisePertubationMethod, noisePerturbationMethodStr, uncutHistogramInfo, rawLocationEntropyList, visitMap);
				writer.println(C 
						+ "," + results.klDivergencePrivateVsLimited + "," + results.ksTestValuePrivateVsLimited
						+ "," + results.klDivergencePrivateVsActual + "," + results.ksTestValuePrivateVsActual
						+ "," + results.klDivergenceLimitedVsActual + "," + results.ksTestValueLimitedVsActual);
				writer.close();
				
				allEpsilonWriter.println(epsStr
						+ "," + C 
						+ "," + results.klDivergencePrivateVsLimited + "," + results.ksTestValuePrivateVsLimited
						+ "," + results.klDivergencePrivateVsActual + "," + results.ksTestValuePrivateVsActual
						+ "," + results.klDivergenceLimitedVsActual + "," + results.ksTestValueLimitedVsActual);
			}
			
			allEpsilonWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		String prefix = Constants.DATASET_PREFIX;
		int L = Constants.L;
		int N = Constants.N;
		int maxM = Constants.MAX_M;
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
		String uncutHistogramFile = FileNameUtil.getOriginalHistogramFileName(prefix, L, N, maxM, maxC, ze, df, dataGenerationOutputDir, bucketSize);
	
		/*
		 * Original, uncut entropy
		 */
		String rawLocationEntropyFile = FileNameUtil.getOriginalEntropyFileName(prefix, L, N, maxM, maxC, ze, df, dataGenerationOutputDir);
		
		boolean calOriginal = false;
		if (calOriginal) {
			System.out.println("Start calculating original data");
			String dataGenerationOutputFile = FileNameUtil.getDataGenerationOutputFile(prefix, L, N, maxM, maxC, ze, df, dataGenerationOutputDir);
			Map<Integer, ArrayList<Integer>> visitMap = VisitingDatasetIO.readData(dataGenerationOutputFile);
			ArrayList<LocationEntropyInfo> leInfos = LocationEntropyCalculator.calLocationEntropy(visitMap, maxC, rawLocationEntropyFile);
//			ArrayList<LocationEntropyInfo> leInfos = LocationEntropyIO.readLocationEntropy(rawLocationEntropyFile);
			LocationEntropyDPMeasureHistogramGenerator.generateHistogram(leInfos, N, bucketSize, df, uncutHistogramFile);
			System.out.println("Finished calculating original data");
				
		}
		
		boolean stop = false;
		if (stop)
			return;
		
		int startC = Constants.C;
		int endC = Constants.C + 1;
		NoisePertubationMethod noisePertubationMethod = Constants.DP_NOISE_PERTURBATION_METHOD;
		String noisePerturbationMethodStr = Constants.DP_NOISE_PERTURBATION_METHOD_STR; 
		
		int C = Constants.C;
		double[] epsilons = new double[] {0.05, 0.25, 0.5, 2.5, 5};
		runTestFixMAndCVaryEpsilon(prefix, L, N, M, maxM, maxC, ze, df, dataGenerationOutputDir, delta, minSensitivity, useM, useMStr, bucketSize, uncutHistogramFile, C, noisePertubationMethod, noisePerturbationMethodStr, rawLocationEntropyFile, epsilons);
//		runTestForAllC(prefix, L, N, M, maxC, ze, df, dataGenerationOutputDir, eps, delta, minSensitivity, useM, useMStr, bucketSize, uncutHistogramFile, startC, endC, noisePertubationMethod, noisePerturbationMethodStr);
		
	}

}
