package com.duykien.usc.GIS;

import java.text.DecimalFormat;

public class FileNameUtil {
	public static final String L_PREFIX = "_L";
	public static final String M_PREFIX = "_M";
	public static final String N_PREFIX = "_N";
	public static final String MAX_C_PREFIX = "_maxC";
	public static final String ZE_PREFIX = "_ze";
	public static final String C_PREFIX = "_C";
	public static final String ENTROPY_PREFIX = "_entropy";
	
	public static final String FILE_EXTENSION = ".csv";

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
				+ L_PREFIX + L 
				+ N_PREFIX + N 
				+ M_PREFIX + M 
				+ MAX_C_PREFIX + maxC 
				+ ZE_PREFIX + df.format(ze) 
				+ FILE_EXTENSION;
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
				+ L_PREFIX + L 
				+ N_PREFIX + N 
				+ M_PREFIX + M 
				+ MAX_C_PREFIX + maxC 
				+ ZE_PREFIX + df.format(ze) 
				+ C_PREFIX + C
				+ "_entropy"
				+ FILE_EXTENSION;
		
		return locationEntropyOutputFile;
	}
	
	public static String getSmoothSensitivityInputFile(String dataGenerationOutputDir,
			int C, String epsStr) {
		String sensitivityInputFile = dataGenerationOutputDir 
				+ "varyN_fixedC" + C 
				+ "_SmoothSensitivity" 
				+ "_eps" + epsStr
				+ "_Delta"+ Constants.DP_DELTA_STR 
				+ "_minSen" + Constants.DP_MIN_SENSITIVITY_STR
				+ FILE_EXTENSION;
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
			String epsStr,
			String useMStr,
			String noisePerturbationMethodStr) {
		String dpOutputFile = dataGenerationOutputDir
				+ prefix
				+ L_PREFIX + L 
				+ N_PREFIX + N 
				+ M_PREFIX + M 
				+ MAX_C_PREFIX + maxC 
				+ ZE_PREFIX + df.format(ze) 
				+ C_PREFIX + C
				+ "_entropy"
				+ "_DP_" + noisePerturbationMethodStr 
				+ "_eps" + epsStr
				+ "_Delta"+ Constants.DP_DELTA_STR 
				+ "_minSen" + Constants.DP_MIN_SENSITIVITY_STR
				+ "_" + useMStr +"useM"
				+ FILE_EXTENSION;
		
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
			String epsStr,
			String useMStr,
			double bucketSize,
			String noisePerturbationMethodStr) {
		String histogramFile = dataGenerationOutputDir
				+ prefix
				+ L_PREFIX + L 
				+ N_PREFIX + N 
				+ M_PREFIX + M 
				+ MAX_C_PREFIX + maxC 
				+ ZE_PREFIX + df.format(ze) 
				+ C_PREFIX + C
				+ "_DP_" + noisePerturbationMethodStr 
				+ "_eps" + epsStr
				+ "_Delta"+ Constants.DP_DELTA_STR 
				+ "_minSen" + Constants.DP_MIN_SENSITIVITY_STR
				+ "_" + useMStr +"useM"
				+"_histogram_bucketSize" + df.format(bucketSize) 
				+ FILE_EXTENSION;
		
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
			String epsStr,
			String useMStr,
			double bucketSize,
			String noisePerturbationMethodStr) {
		String histogramFile = dataGenerationOutputDir
				+ prefix
				+ L_PREFIX + L 
				+ N_PREFIX + N 
				+ M_PREFIX + M 
				+ MAX_C_PREFIX + maxC 
				+ ZE_PREFIX + df.format(ze) 
				+ C_PREFIX + C
				+ "_entropy"
				+ "_DP_" + noisePerturbationMethodStr 
				+ "_eps" + epsStr
				+ "_Delta"+ Constants.DP_DELTA_STR 
				+ "_minSen" + Constants.DP_MIN_SENSITIVITY_STR
				+ "_" + useMStr +"useM"
				+"_histogram_bucketSize" + df.format(bucketSize) 
				+"___errors"
				+ FILE_EXTENSION;
		
		return histogramFile;
	}
	
	public static String getTestResultsFileName(String prefix,
			int L,
			int N,
			int M,
			int maxC,
			double ze,
			DecimalFormat df, 
			String epsStr,
			String dataGenerationOutputDir,
			String useMStr,
			double bucketSize,
			String noisePerturbationMethodStr) {
		String histogramFile = dataGenerationOutputDir
				+ prefix
				+ L_PREFIX + L 
				+ N_PREFIX + N 
				+ M_PREFIX + M 
				+ MAX_C_PREFIX + maxC 
				+ ZE_PREFIX + df.format(ze) 
				+ "_DP_" + noisePerturbationMethodStr 
				+ "_eps" + epsStr
				+ "_Delta"+ Constants.DP_DELTA_STR 
				+ "_minSen" + Constants.DP_MIN_SENSITIVITY_STR
				+ "_" + useMStr +"useM"
				+"_histogram_bucketSize" + df.format(bucketSize) 
				+ "_test_results"
				+ FILE_EXTENSION;
		
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
				+ L_PREFIX + L 
				+ N_PREFIX + N 
				+ M_PREFIX + M 
				+ MAX_C_PREFIX + maxC 
				+ ZE_PREFIX + df.format(ze) 
				+ C_PREFIX + maxC
				+ ENTROPY_PREFIX
				+ FILE_EXTENSION;
		
		return histogramFile;
	}
	
	public static String getOriginalHistogramFileName(String prefix,
			int L,
			int N,
			int maxM,
			int maxC,
			double ze,
			DecimalFormat df, 
			String dataGenerationOutputDir,
			double bucketSize) {
		String histogramFile = dataGenerationOutputDir
				+ prefix
				+ L_PREFIX + L 
				+ N_PREFIX + N 
				+ M_PREFIX + maxM 
				+ MAX_C_PREFIX + maxC 
				+ ZE_PREFIX + df.format(ze) 
				+ C_PREFIX + maxC
				+"_ACTUAL_histogram_bucketSize" + df.format(bucketSize) 
				+ FILE_EXTENSION;
		
		return histogramFile;
	}
	
}
