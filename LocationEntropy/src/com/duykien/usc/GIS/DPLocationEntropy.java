package com.duykien.usc.GIS;

import java.text.DecimalFormat;

import com.duykien.usc.GIS.DP.DifferentialPrivacyNoisePerturbator;
import com.duykien.usc.GIS.measure.LocationEntropyDPMeasureHistogramEvaluator;
import com.duykien.usc.GIS.measure.LocationEntropyDPMeasureHistogramGenerator;

public class DPLocationEntropy {

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
		DifferentialPrivacyNoisePerturbator.perturbUnderDP(locationEntropyOutputFile, sensitivityInputFile, N, M, C, eps, delta, minSensitivity, useM, dpOutputFile);
		System.out.println("DifferentialPrivacyNoisePerturbator Finished");
		
		double bucketSize = Constants.BUCKET_SIZE;
		String histogramFile = FileNameUtil.getHistogramFileName(L, N, M, maxC, ze, df, dataGenerationOutputDir, C, useMStr, bucketSize);
		LocationEntropyDPMeasureHistogramGenerator.generateHistogram(dpOutputFile, N, bucketSize, df, histogramFile);
		
		System.out.println("LocationEntropyDPMeasureHistogramGenerator Finished");
		
		String histogramErrorFile = FileNameUtil.getHistogramErrorFileName(L, N, M, maxC, ze, df, dataGenerationOutputDir, C, useMStr, bucketSize);
		
		LocationEntropyDPMeasureHistogramEvaluator.evaluateHistogram(histogramFile, histogramErrorFile);

		System.out.println("LocationEntropyDPMeasureHistogramEvaluator Finished");
	}

}
