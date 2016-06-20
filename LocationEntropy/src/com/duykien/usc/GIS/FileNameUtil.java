package com.duykien.usc.GIS;

import java.text.DecimalFormat;

public class FileNameUtil {

	public static String getDataGenerationOutputFile(String prefix,
			int L,
			int N,
			int M,
			int maxC,
			double ze,
			DecimalFormat df, 
			String dataGenerationOutputDir) {
		String dataGenerationOutputFile = dataGenerationOutputDir
				+ prefix
				+ "_L" + L 
				+ "_N" + N 
				+ "_M" + M 
				+ "_maxC" + maxC 
				+ "_ze" + df.format(ze) 
				+ ".csv";
		return dataGenerationOutputFile;
	}
	
	public static String getLocationEntropyOutputFile(String prefix,
			int L,
			int N,
			int M,
			int maxC,
			double ze,
			DecimalFormat df, 
			String dataGenerationOutputDir,
			int C) {
		String locationEntropyOutputFile = dataGenerationOutputDir
				+ prefix
				+ "_L" + L 
				+ "_N" + N 
				+ "_M" + M 
				+ "_maxC" + maxC 
				+ "_ze" + df.format(ze) 
				+ "_C" + C
				+ "_entropy"
				+ ".csv";
		
		return locationEntropyOutputFile;
	}
	
	public static String getSmoothSensitivityInputFile(String dataGenerationOutputDir,
			int C) {
		String sensitivityInputFile = dataGenerationOutputDir 
				+ "varyN_fixedC" + C 
				+ "_SmoothSensitivity" 
				+ "_eps" + Constants.DP_EPSILON_STR 
				+ "_Delta"+ Constants.DP_DELTA_STR 
				+ "_minSen" + Constants.DP_MIN_SENSITIVITY_STR
				+ ".csv";
		return sensitivityInputFile;
	}
	
	public static String getDPOutputFile(String prefix,
			int L,
			int N,
			int M,
			int maxC,
			double ze,
			DecimalFormat df, 
			String dataGenerationOutputDir,
			int C,
			String useMStr,
			String noisePerturbationMethodStr) {
		String dpOutputFile = dataGenerationOutputDir
				+ prefix
				+ "_L" + L 
				+ "_N" + N 
				+ "_M" + M 
				+ "_maxC" + maxC 
				+ "_ze" + df.format(ze) 
				+ "_C" + C
				+ "_entropy"
				+ "_DP_" + noisePerturbationMethodStr 
				+ "_eps" + Constants.DP_EPSILON_STR 
				+ "_Delta"+ Constants.DP_DELTA_STR 
				+ "_minSen" + Constants.DP_MIN_SENSITIVITY_STR
				+ "_" + useMStr +"useM"
				+ ".csv";
		
		return dpOutputFile;
	}
	
	public static String getHistogramFileName(String prefix,
			int L,
			int N,
			int M,
			int maxC,
			double ze,
			DecimalFormat df, 
			String dataGenerationOutputDir,
			int C,
			String useMStr,
			double bucketSize,
			String noisePerturbationMethodStr) {
		String histogramFile = dataGenerationOutputDir
				+ prefix
				+ "_L" + L 
				+ "_N" + N 
				+ "_M" + M 
				+ "_maxC" + maxC 
				+ "_ze" + df.format(ze) 
				+ "_C" + C
				+ "_DP_" + noisePerturbationMethodStr 
				+ "_eps" + Constants.DP_EPSILON_STR 
				+ "_Delta"+ Constants.DP_DELTA_STR 
				+ "_minSen" + Constants.DP_MIN_SENSITIVITY_STR
				+ "_" + useMStr +"useM"
				+"_histogram_bucketSize" + df.format(bucketSize) +".csv";
		
		return histogramFile;
	}
	
	public static String getHistogramErrorFileName(String prefix,
			int L,
			int N,
			int M,
			int maxC,
			double ze,
			DecimalFormat df, 
			String dataGenerationOutputDir,
			int C,
			String useMStr,
			double bucketSize,
			String noisePerturbationMethodStr) {
		String histogramFile = dataGenerationOutputDir
				+ prefix
				+ "_L" + L 
				+ "_N" + N 
				+ "_M" + M 
				+ "_maxC" + maxC 
				+ "_ze" + df.format(ze) 
				+ "_C" + C
				+ "_entropy"
				+ "_DP_" + noisePerturbationMethodStr 
				+ "_eps" + Constants.DP_EPSILON_STR 
				+ "_Delta"+ Constants.DP_DELTA_STR 
				+ "_minSen" + Constants.DP_MIN_SENSITIVITY_STR
				+ "_" + useMStr +"useM"
				+"_histogram_bucketSize" + df.format(bucketSize) 
				+"___errors.csv";
		
		return histogramFile;
	}
	
	public static String getTestResultsFileName(String prefix,
			int L,
			int N,
			int M,
			int maxC,
			double ze,
			DecimalFormat df, 
			String dataGenerationOutputDir,
			String useMStr,
			double bucketSize,
			String noisePerturbationMethodStr) {
		String histogramFile = dataGenerationOutputDir
				+ prefix
				+ "_L" + L 
				+ "_N" + N 
				+ "_M" + M 
				+ "_maxC" + maxC 
				+ "_ze" + df.format(ze) 
				+ "_DP_" + noisePerturbationMethodStr 
				+ "_eps" + Constants.DP_EPSILON_STR 
				+ "_Delta"+ Constants.DP_DELTA_STR 
				+ "_minSen" + Constants.DP_MIN_SENSITIVITY_STR
				+ "_" + useMStr +"useM"
				+"_histogram_bucketSize" + df.format(bucketSize) 
				+ "_test_results"
				+".csv";
		
		return histogramFile;
	}
	
	/**
	 * Original entropy file is the entropy when C = maxC
	 * @param L
	 * @param N
	 * @param M
	 * @param maxC
	 * @param ze
	 * @param df
	 * @param dataGenerationOutputDir
	 * @return
	 */
	public static String getOriginalEntropyFileName(String prefix,
			int L,
			int N,
			int M,
			int maxC,
			double ze,
			DecimalFormat df, 
			String dataGenerationOutputDir) {
		String histogramFile = dataGenerationOutputDir
				+ prefix
				+ "_L" + L 
				+ "_N" + N 
				+ "_M" + M 
				+ "_maxC" + maxC 
				+ "_ze" + df.format(ze) 
				+ "_C" + maxC
				+"_entropy.csv";
		
		return histogramFile;
	}
	
	public static String getOriginalHistogramFileName(String prefix,
			int L,
			int N,
			int M,
			int maxC,
			double ze,
			DecimalFormat df, 
			String dataGenerationOutputDir,
			double bucketSize) {
		String histogramFile = dataGenerationOutputDir
				+ prefix
				+ "_L" + L 
				+ "_N" + N 
				+ "_M" + M 
				+ "_maxC" + maxC 
				+ "_ze" + df.format(ze) 
				+ "_C" + maxC
				+"_UNCUT_histogram_bucketSize" 
				+ df.format(bucketSize) +".csv";
		
		return histogramFile;
	}
	
}
