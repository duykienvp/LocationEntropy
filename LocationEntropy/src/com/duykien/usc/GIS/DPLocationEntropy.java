package com.duykien.usc.GIS;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;

import com.duykien.usc.GIS.DP.DPUtil;
import com.duykien.usc.GIS.DP.DifferentialPrivacyNoisePerturbator;
import com.duykien.usc.GIS.DP.LocationEntropyInfo;
import com.duykien.usc.GIS.DP.PertubationMethodFactory.NoisePertubationMethod;
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
			int kCrowd,
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
		
		ArrayList<LocationEntropyInfo> leInfos = LocationEntropyCalculator.calLocationEntropy(visitMap, C, locationEntropyOutputFile, true);
		System.out.println("LocationEntropyCalculator Finished");
			
		//Differential privacy 
		String epsStr = DPUtil.toEpsilonString(eps);
		String sensitivityInputFile = FileNameUtil.getSmoothSensitivityInputFile(dataGenerationOutputDir, C, epsStr);
		
		String dpOutputFile = FileNameUtil.getDPOutputFile(prefix, L, N, M, maxC, ze, df, dataGenerationOutputDir, C, epsStr, useMStr, noisePerturbationMethodStr);
		DifferentialPrivacyNoisePerturbator.perturbUnderDP(leInfos, sensitivityInputFile, N, M, C, eps, delta, minSensitivity, kCrowd, useM, noisePertubationMethod, dpOutputFile);
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
	
	public static void runTestFixMAndEpsilonVaryC(String prefix,
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
			int kCrowd,
			boolean useM,
			String useMStr,
			double bucketSize,
			String uncutHistogramFile,
			int startC,
			int endC,
			NoisePertubationMethod noisePertubationMethod,
			String rawLocationEntropyFile,
			boolean measureOnly) {
		String noisePerturbationMethodStr = noisePertubationMethod.toString();
		ArrayList<LocationEntropyInfo> rawLocationEntropyList = LocationEntropyIO.readLocationEntropy(rawLocationEntropyFile);
		String dataGenerationOutputFile = FileNameUtil.getDataGenerationOutputFile(prefix, L, N, M, maxC, ze, df, dataGenerationOutputDir);
		Map<Integer, ArrayList<Integer>> visitMap = VisitingDatasetIO.readData(dataGenerationOutputFile);
		LEHistogramInfo uncutHistogramInfo = LocationEntropyDPMeasureHistogramEvaluator.readHistogram(uncutHistogramFile);
		String epsStr = DPUtil.toEpsilonString(eps);
		String testResultFile = FileNameUtil.getTestResultsFileName(prefix, L, N, M, maxC, ze, df, epsStr, dataGenerationOutputDir, useMStr, bucketSize, noisePerturbationMethodStr);
		try {
			PrintWriter writer = new PrintWriter(testResultFile);
			for (int C = startC; C < endC; C++) {
				
				MeasurementResults results = new MeasurementResults();
				if (measureOnly) {
					String dpOutputFile = FileNameUtil.getDPOutputFile(prefix, L, N, M, maxC, ze, df, dataGenerationOutputDir, C, epsStr, useMStr, noisePerturbationMethodStr);
					ArrayList<LocationEntropyInfo> dpLocationEntropyList = LocationEntropyIO.readLocationEntropy(dpOutputFile);
					results = LocationEntropyDPMeasureEntropyEvaluator.evaluateEntropy(rawLocationEntropyList, dpLocationEntropyList, bucketSize);
				} else {
					results = runDPLocationEntropy(prefix, L, N, M, maxC, ze, df, dataGenerationOutputDir, eps, delta, minSensitivity, kCrowd, useM, useMStr, bucketSize, C, noisePertubationMethod, noisePerturbationMethodStr, uncutHistogramInfo, rawLocationEntropyList, visitMap);
				}
				writer.println(epsStr
						+ "," + C 
						+ "," + results.klDivergencePrivateVsLimited + "," + results.ksTestValuePrivateVsLimited
						+ "," + results.klDivergencePrivateVsActual + "," + results.ksTestValuePrivateVsActual
						+ "," + results.klDivergenceLimitedVsActual + "," + results.ksTestValueLimitedVsActual
						+ "," + results.MSEPrivateVsActual + "," + results.MSELimitedVsActual
						+ "," + results.MSEPrivateVsLimited); 
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
			int kCrowd,
			boolean useM,
			String useMStr,
			double bucketSize,
			String uncutHistogramFile,
			int C,
			NoisePertubationMethod noisePertubationMethod,
			String rawLocationEntropyFile,
			double[] epsilons,
			boolean measureOnly) {
		String noisePerturbationMethodStr = noisePertubationMethod.toString();
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
				
				MeasurementResults results = new MeasurementResults();
				if (measureOnly) {
					String dpOutputFile = FileNameUtil.getDPOutputFile(prefix, L, N, M, maxC, ze, df, dataGenerationOutputDir, C, epsStr, useMStr, noisePerturbationMethodStr);
					ArrayList<LocationEntropyInfo> dpLocationEntropyList = LocationEntropyIO.readLocationEntropy(dpOutputFile);
					results = LocationEntropyDPMeasureEntropyEvaluator.evaluateEntropy(rawLocationEntropyList, dpLocationEntropyList, bucketSize);
				} else {
					results = runDPLocationEntropy(prefix, L, N, M, maxC, ze, df, dataGenerationOutputDir, eps, delta, minSensitivity, kCrowd, useM, useMStr, bucketSize, C, noisePertubationMethod, noisePerturbationMethodStr, uncutHistogramInfo, rawLocationEntropyList, visitMap);
				}
				
				writer.println(C 
						+ "," + results.klDivergencePrivateVsLimited + "," + results.ksTestValuePrivateVsLimited
						+ "," + results.klDivergencePrivateVsActual + "," + results.ksTestValuePrivateVsActual
						+ "," + results.klDivergenceLimitedVsActual + "," + results.ksTestValueLimitedVsActual
						+ "," + results.MSEPrivateVsActual + "," + results.MSELimitedVsActual
						+ "," + results.MSEPrivateVsLimited);
				writer.close();
				
				allEpsilonWriter.println(epsStr
						+ "," + C 
						+ "," + results.klDivergencePrivateVsLimited + "," + results.ksTestValuePrivateVsLimited
						+ "," + results.klDivergencePrivateVsActual + "," + results.ksTestValuePrivateVsActual
						+ "," + results.klDivergenceLimitedVsActual + "," + results.ksTestValueLimitedVsActual
						+ "," + results.MSEPrivateVsActual + "," + results.MSELimitedVsActual
						+ "," + results.MSEPrivateVsLimited);
			}
			
			allEpsilonWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void runTestFixCAndEpsilonVaryM(String prefix,
			int L,
			int N,
			int maxM,
			int maxC,
			double ze,
			DecimalFormat df,
			String dataGenerationOutputDir,
			double delta,
			double minSensitivity,
			int kCrowd,
			boolean useM,
			String useMStr,
			double bucketSize,
			String uncutHistogramFile,
			int C,
			NoisePertubationMethod noisePertubationMethod,
			String rawLocationEntropyFile,
			double eps,
			int[] Ms,
			boolean measureOnly) {
		String noisePerturbationMethodStr = noisePertubationMethod.toString();
		ArrayList<LocationEntropyInfo> rawLocationEntropyList = LocationEntropyIO.readLocationEntropy(rawLocationEntropyFile);
		
		try {
			
			String allMResultFile = FileNameUtil.getTestResultsFileName(prefix, L, N, 0, maxC, ze, df, DPUtil.toEpsilonString(eps), dataGenerationOutputDir, useMStr, bucketSize, noisePerturbationMethodStr);
			PrintWriter allWriter = new PrintWriter(allMResultFile); 
			
			for (int i = 0; i < Ms.length; i++) {
				int M = Ms[i];
				double epsM = eps/M;
				String dataGenerationOutputFile = FileNameUtil.getDataGenerationOutputFile(prefix, L, N, M, maxC, ze, df, dataGenerationOutputDir);
				Map<Integer, ArrayList<Integer>> visitMap = VisitingDatasetIO.readData(dataGenerationOutputFile);
				LEHistogramInfo uncutHistogramInfo = LocationEntropyDPMeasureHistogramEvaluator.readHistogram(uncutHistogramFile);
				
				String epsStr = DPUtil.toEpsilonString(epsM);
				String testResultFile = FileNameUtil.getTestResultsFileName(prefix, L, N, M, maxC, ze, df, epsStr, dataGenerationOutputDir, useMStr, bucketSize, noisePerturbationMethodStr);
				PrintWriter writer = new PrintWriter(testResultFile);
				
				MeasurementResults results = new MeasurementResults();
				if (measureOnly) {
					String dpOutputFile = FileNameUtil.getDPOutputFile(prefix, L, N, M, maxC, ze, df, dataGenerationOutputDir, C, epsStr, useMStr, noisePerturbationMethodStr);
					ArrayList<LocationEntropyInfo> dpLocationEntropyList = LocationEntropyIO.readLocationEntropy(dpOutputFile);
					results = LocationEntropyDPMeasureEntropyEvaluator.evaluateEntropy(rawLocationEntropyList, dpLocationEntropyList, bucketSize);
				} else {
					results = runDPLocationEntropy(prefix, L, N, M, maxC, ze, df, dataGenerationOutputDir, epsM, delta, minSensitivity, kCrowd, useM, useMStr, bucketSize, C, noisePertubationMethod, noisePerturbationMethodStr, uncutHistogramInfo, rawLocationEntropyList, visitMap);
				}
				
				writer.println(C 
						+ "," + results.klDivergencePrivateVsLimited + "," + results.ksTestValuePrivateVsLimited
						+ "," + results.klDivergencePrivateVsActual + "," + results.ksTestValuePrivateVsActual
						+ "," + results.klDivergenceLimitedVsActual + "," + results.ksTestValueLimitedVsActual
						+ "," + results.MSEPrivateVsActual + "," + results.MSELimitedVsActual
						+ "," + results.MSEPrivateVsLimited);
				writer.close();
				
				allWriter.println(M
						+ "," + C 
						+ "," + results.klDivergencePrivateVsLimited + "," + results.ksTestValuePrivateVsLimited
						+ "," + results.klDivergencePrivateVsActual + "," + results.ksTestValuePrivateVsActual
						+ "," + results.klDivergenceLimitedVsActual + "," + results.ksTestValueLimitedVsActual
						+ "," + results.MSEPrivateVsActual + "," + results.MSELimitedVsActual
						+ "," + results.MSEPrivateVsLimited);
			}
			
			allWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void runBaseline(String prefix,
			int L,
			int N,
			int maxM,
			int maxC,
			double ze,
			DecimalFormat df,
			String dataGenerationOutputDir,
			boolean useM,
			String useMStr,
			double bucketSize,
			String uncutHistogramFile,
			double eps) {
		NoisePertubationMethod noisePertubationMethod = NoisePertubationMethod.LIMIT;
		String noisePerturbationMethodStr = NoisePertubationMethod.BASELINE.toString();
		String rawLocationEntropyFile = Constants.DATA_GENERATOR_OUTPUT_DIR + "synthetic_data_L10000_N100000_M100_maxC1000_ze1.0_C1000_entropy_actual.csv";
		ArrayList<LocationEntropyInfo> rawLocationEntropyList = LocationEntropyIO.readLocationEntropy(rawLocationEntropyFile);
		String dataGenerationOutputFile = FileNameUtil.getDataGenerationOutputFile(prefix, L, N, maxM, maxC, ze, df, dataGenerationOutputDir);
		Map<Integer, ArrayList<Integer>> visitMap = VisitingDatasetIO.readData(dataGenerationOutputFile);
		LEHistogramInfo uncutHistogramInfo = LocationEntropyDPMeasureHistogramEvaluator.readHistogram(uncutHistogramFile);
		try {
			String epsStr = DPUtil.toEpsilonString(eps);
			String testResultFile = FileNameUtil.getTestResultsFileName(prefix, L, N, maxM, maxC, ze, df, epsStr, dataGenerationOutputDir, useMStr, bucketSize, noisePerturbationMethodStr);
			PrintWriter writer = new PrintWriter(testResultFile);
			MeasurementResults results = runDPLocationEntropy(prefix, L, N, maxM, maxC, ze, df, dataGenerationOutputDir, eps, 0, 0, 1, useM, useMStr, bucketSize, maxC, noisePertubationMethod, noisePerturbationMethodStr, uncutHistogramInfo, rawLocationEntropyList, visitMap);
			writer.println(maxC 
					+ "," + results.klDivergencePrivateVsLimited + "," + results.ksTestValuePrivateVsLimited
					+ "," + results.klDivergencePrivateVsActual + "," + results.ksTestValuePrivateVsActual
					+ "," + results.klDivergenceLimitedVsActual + "," + results.ksTestValueLimitedVsActual
					+ "," + results.MSEPrivateVsActual + "," + results.MSELimitedVsActual
					+ "," + results.MSEPrivateVsLimited);
			writer.close();
			
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
//		boolean useM = true;
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
			ArrayList<LocationEntropyInfo> leInfos = LocationEntropyCalculator.calLocationEntropy(visitMap, maxC, rawLocationEntropyFile, true);
//			ArrayList<LocationEntropyInfo> leInfos = LocationEntropyIO.readLocationEntropy(rawLocationEntropyFile);
			LocationEntropyDPMeasureHistogramGenerator.generateHistogram(leInfos, N, bucketSize, df, uncutHistogramFile);
			System.out.println("Finished calculating original data");
				
		}
		
		boolean stop = false;
		if (stop)
			return;
		
		int startC = Constants.START_C;
		int endC = Constants.END_C;
		NoisePertubationMethod noisePertubationMethod = NoisePertubationMethod.LIMIT_SS;
		int kCrowd = Constants.K_CROWD;
		
		int C = Constants.C;
		double[] epsilons = new double[] {0.02, 0.1, 0.2, 1, 2};
		
		boolean measureOnly = false; //only measure or run the full experiments
		
		int[] Ms = new int[] {1, 2, 5, 10, 20, 30};
		
//		runBaseline(prefix, L, N, maxM, maxC, ze, df, dataGenerationOutputDir, useM, useMStr, bucketSize, uncutHistogramFile, 1);
//		runTestFixMAndCVaryEpsilon(prefix, L, N, M, maxM, maxC, ze, df, dataGenerationOutputDir, delta, minSensitivity, kCrowd, useM, useMStr, bucketSize, uncutHistogramFile, C, noisePertubationMethod, rawLocationEntropyFile, epsilons, measureOnly);
		runTestFixMAndEpsilonVaryC(prefix, L, N, M, maxC, ze, df, dataGenerationOutputDir, eps, delta, minSensitivity, kCrowd, useM, useMStr, bucketSize, uncutHistogramFile, startC, endC, noisePertubationMethod, rawLocationEntropyFile, measureOnly);
		
		//for runTestFixCAndEpsilonVaryM
//		eps = 5.0;
//		runTestFixCAndEpsilonVaryM(prefix, L, N, maxM, maxC, ze, df, dataGenerationOutputDir, delta, minSensitivity, kCrowd, useM, useMStr, bucketSize, uncutHistogramFile, C, noisePertubationMethod, rawLocationEntropyFile, eps, Ms, measureOnly);
	}

}
