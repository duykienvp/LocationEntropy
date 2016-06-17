package com.duykien.usc.GIS;

import java.text.DecimalFormat;

public class FileNameUtil {

	public static String getDataGenerationOutputFile(int L,
			int N,
			int M,
			int maxC,
			double ze,
			DecimalFormat df, 
			String dataGenerationOutputDir) {
		String dataGenerationOutputFile = dataGenerationOutputDir 
				+ "synthetic_data_L" + L 
				+ "_N" + N 
				+ "_M" + M 
				+ "_maxC" + maxC 
				+ "_ze" + df.format(ze) 
				+ ".csv";
		return dataGenerationOutputFile;
	}
	
	public static String getLocationEntropyOutputFile(int L,
			int N,
			int M,
			int maxC,
			double ze,
			DecimalFormat df, 
			String dataGenerationOutputDir,
			int C) {
		String locationEntropyOutputFile = dataGenerationOutputDir 
				+ "synthetic_data_L" + L 
				+ "_N" + N 
				+ "_M" + M 
				+ "_maxC" + maxC 
				+ "_ze" + df.format(ze) 
				+ "_C" + C
				+ "_entropy"
				+ ".csv";
		
		return locationEntropyOutputFile;
	}
	
	public static String getSensitivityInputFile(String dataGenerationOutputDir,
			int C) {
		String sensitivityInputFile = dataGenerationOutputDir 
				+ "varyN_fixedC" + C 
				+ "_SmoothSensitivity_EpsLn10_Delta1e-7_Tolerance1e-3"
				+ ".csv";
		return sensitivityInputFile;
	}
	
	public static String getDPOutputFile(int L,
			int N,
			int M,
			int maxC,
			double ze,
			DecimalFormat df, 
			String dataGenerationOutputDir,
			int C,
			String useMStr) {
		String dpOutputFile = dataGenerationOutputDir 
				+ "synthetic_data_L" + L 
				+ "_N" + N 
				+ "_M" + M 
				+ "_maxC" + maxC 
				+ "_ze" + df.format(ze) 
				+ "_C" + C
				+ "_entropy"
				+ "_DP_SS_epsLn10_delta1e-7_minSen1e-3_" 
				+ useMStr +"useM"
				+ ".csv";
		
		return dpOutputFile;
	}
	
	public static String getHistogramFileName(int L,
			int N,
			int M,
			int maxC,
			double ze,
			DecimalFormat df, 
			String dataGenerationOutputDir,
			int C,
			String useMStr,
			double bucketSize) {
		String histogramFile = dataGenerationOutputDir 
				+ "synthetic_data_L" + L 
				+ "_N" + N 
				+ "_M" + M 
				+ "_maxC" + maxC 
				+ "_ze" + df.format(ze) 
				+ "_C" + C
				+ "_entropy"
				+ "_DP_SS_epsLn10_delta1e-7_minSen1e-3_" 
				+ useMStr +"useM" 
				+"_histogram_bucketSize" + df.format(bucketSize) +".csv";
		
		return histogramFile;
	}
	
	public static String getHistogramErrorFileName(int L,
			int N,
			int M,
			int maxC,
			double ze,
			DecimalFormat df, 
			String dataGenerationOutputDir,
			int C,
			String useMStr,
			double bucketSize) {
		String histogramFile = dataGenerationOutputDir 
				+ "synthetic_data_L" + L 
				+ "_N" + N 
				+ "_M" + M 
				+ "_maxC" + maxC 
				+ "_ze" + df.format(ze) 
				+ "_C" + C
				+ "_entropy"
				+ "_DP_SS_epsLn10_delta1e-7_minSen1e-3_" 
				+ useMStr +"useM" 
				+"_histogram_bucketSize" + df.format(bucketSize) 
				+"___errors.csv";
		
		return histogramFile;
	}
}
