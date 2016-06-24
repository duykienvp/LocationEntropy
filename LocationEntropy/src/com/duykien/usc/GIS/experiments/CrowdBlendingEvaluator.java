package com.duykien.usc.GIS.experiments;

import java.io.PrintWriter;
import java.util.ArrayList;

import com.duykien.usc.GIS.DP.DPUtil;
import com.duykien.usc.GIS.DP.LocationEntropyInfo;
import com.duykien.usc.GIS.io.LocationEntropyIO;
import com.duykien.usc.GIS.measure.LocationEntropyDPMeasureEntropyEvaluator;
import com.duykien.usc.GIS.measure.MeasurementResults;

public class CrowdBlendingEvaluator {
	public static void removeUnpublishedLocation(int k, 
			String limitFile, String limitSSFile, String limitCBFile, String rawInputFile,
			String limitOutFile, String limitSSOutFile, String limitCBOutFile, String rawOutputFile) {
		ArrayList<LocationEntropyInfo> limitInfos = LocationEntropyIO.readLocationEntropy(limitFile);
		ArrayList<LocationEntropyInfo> limitSSInfos = LocationEntropyIO.readLocationEntropy(limitSSFile);
		ArrayList<LocationEntropyInfo> limitCBInfos = LocationEntropyIO.readLocationEntropy(limitCBFile);
		ArrayList<LocationEntropyInfo> rawInfos = LocationEntropyIO.readLocationEntropy(rawInputFile);
		
		ArrayList<LocationEntropyInfo> limitRemovedInfos = new ArrayList<>();
		ArrayList<LocationEntropyInfo> limitSSRemovedInfos = new ArrayList<>();
		ArrayList<LocationEntropyInfo> limitCBRemovedInfos = new ArrayList<>();
		ArrayList<LocationEntropyInfo> rawOutputInfos = new ArrayList<>();
		
		for (int i = 0; i < limitCBInfos.size(); i++) {
			if (k <= limitCBInfos.get(i).getNumUser()) {
				limitRemovedInfos.add(limitInfos.get(i));
				limitSSRemovedInfos.add(limitSSInfos.get(i));
				limitCBRemovedInfos.add(limitCBInfos.get(i));
				rawOutputInfos.add(rawInfos.get(i));
			}
		}
		
		LocationEntropyIO.writeLocationEntropy(limitOutFile, limitRemovedInfos);
		LocationEntropyIO.writeLocationEntropy(limitSSOutFile, limitSSRemovedInfos);
		LocationEntropyIO.writeLocationEntropy(limitCBOutFile, limitCBRemovedInfos);
		LocationEntropyIO.writeLocationEntropy(rawOutputFile, rawOutputInfos);
	}
	
	public static void testVaryM() {
		try {
			int k = 50;
			int N = 10000000;
			String inputDir = "/Users/kiennd/Downloads/location_entropy_data/synthetic_data/"
					+ "synthetic_data_L10000_N"
					+ N
					+ "_M100_maxC1000_ze1.0/FixC5Epsilon5VaryM/only_consider_published_locations/";
			String outputDir = inputDir + "only_published_" + k + "/";
			String rawEntropyInputFile = inputDir + "synthetic_data_L10000_N"
					+ N
					+ "_M100_maxC1000_ze1.0_C1000_entropy.csv";
			String rawEntropyOutputFile = outputDir + "synthetic_data_L10000_N"
					+ N
					+ "_M100_maxC1000_ze1.0_C1000_entropy.csv";
			int[] Ms = new int[] {1, 2, 5, 10, 20, 30};
			double eps = 5;
			String testResultLimitFile = outputDir + "synthetic_data_L10000_N"
					+ N
					+ "_M0_maxC1000_ze1.0_DP_LIMIT_eps5_Delta1e-8_minSen1e-3_NOTuseM_histogram_bucketSize0.1_test_results.csv";
			String testResultLimitSSFile = outputDir + "synthetic_data_L10000_N"
					+ N
					+ "_M0_maxC1000_ze1.0_DP_LIMIT_SS_eps5_Delta1e-8_minSen1e-3_NOTuseM_histogram_bucketSize0.1_test_results.csv";
			String testResultLimitCBFile = outputDir + "synthetic_data_L10000_N"
					+ N
					+ "_M0_maxC1000_ze1.0_DP_LIMIT_CROWD_eps5_Delta1e-8_minSen1e-3_NOTuseM_histogram_bucketSize0.1_test_results.csv";
			
			PrintWriter limitResultWriter = new PrintWriter(testResultLimitFile);
			PrintWriter limitSSResultWriter = new PrintWriter(testResultLimitSSFile);
			PrintWriter limitCBResultWriter = new PrintWriter(testResultLimitCBFile);
			for (int i = 0; i < Ms.length; i++) {
				int M = Ms[i];
				String epsStr = DPUtil.toEpsilonString(eps / M);
				String limitFile = "synthetic_data_L10000_N"
						+ N
						+ "_M"
						+ M
						+ "_maxC1000_ze1.0_C5_entropy_DP_"
						+ "LIMIT"
						+ "_eps"
						+ epsStr
						+ "_Delta1e-8_minSen1e-3_NOTuseM.csv";
				String limitSSFile = "synthetic_data_L10000_N"
						+ N
						+ "_M"
						+ M
						+ "_maxC1000_ze1.0_C5_entropy_DP_"
						+ "LIMIT_SS"
						+ "_eps"
						+ epsStr
						+ "_Delta1e-8_minSen1e-3_NOTuseM.csv";
				String limitCBFile = "synthetic_data_L10000_N"
						+ N
						+ "_M"
						+ M
						+ "_maxC1000_ze1.0_C5_entropy_DP_"
						+ "LIMIT_CROWD"
						+ "_eps"
						+ epsStr
						+ "_Delta1e-8_minSen1e-3_NOTuseM.csv";
				String inputLimitFile = inputDir + limitFile;
				String inputLimitSSFile = inputDir + limitSSFile;
				String inputLimitCBFile = inputDir + limitCBFile;
				
				String outputLimitFile = outputDir + limitFile;
				String outputLimitSSFile = outputDir + limitSSFile;
				String outputLimitCBFile = outputDir + limitCBFile;
				
				removeUnpublishedLocation(k, 
						inputLimitFile, inputLimitSSFile, inputLimitCBFile, rawEntropyInputFile,
						outputLimitFile, outputLimitSSFile, outputLimitCBFile, rawEntropyOutputFile);
				
				double bucketSize = 0.1;
				int C = 5;
				
				ArrayList<LocationEntropyInfo> rawLocationEntropyList = LocationEntropyIO.readLocationEntropy(rawEntropyOutputFile);
				ArrayList<LocationEntropyInfo> limitLocationEntropyList = LocationEntropyIO.readLocationEntropy(outputLimitFile);
				ArrayList<LocationEntropyInfo> limitSSLocationEntropyList = LocationEntropyIO.readLocationEntropy(outputLimitSSFile);
				ArrayList<LocationEntropyInfo> limitCBLocationEntropyList = LocationEntropyIO.readLocationEntropy(outputLimitCBFile);
				
				MeasurementResults results;
				results = LocationEntropyDPMeasureEntropyEvaluator.evaluateEntropy(rawLocationEntropyList, limitLocationEntropyList, bucketSize);
				limitResultWriter.println(M
						+ "," + C 
						+ "," + results.klDivergencePrivateVsLimited + "," + results.ksTestValuePrivateVsLimited
						+ "," + results.klDivergencePrivateVsActual + "," + results.ksTestValuePrivateVsActual
						+ "," + results.klDivergenceLimitedVsActual + "," + results.ksTestValueLimitedVsActual
						+ "," + results.MSEPrivateVsActual + "," + results.MSELimitedVsActual
						+ "," + results.MSEPrivateVsLimited);
				
				results = LocationEntropyDPMeasureEntropyEvaluator.evaluateEntropy(rawLocationEntropyList, limitSSLocationEntropyList, bucketSize);
				limitSSResultWriter.println(M
						+ "," + C 
						+ "," + results.klDivergencePrivateVsLimited + "," + results.ksTestValuePrivateVsLimited
						+ "," + results.klDivergencePrivateVsActual + "," + results.ksTestValuePrivateVsActual
						+ "," + results.klDivergenceLimitedVsActual + "," + results.ksTestValueLimitedVsActual
						+ "," + results.MSEPrivateVsActual + "," + results.MSELimitedVsActual
						+ "," + results.MSEPrivateVsLimited);
				
				results = LocationEntropyDPMeasureEntropyEvaluator.evaluateEntropy(rawLocationEntropyList, limitCBLocationEntropyList, bucketSize);
				limitCBResultWriter.println(M
						+ "," + C 
						+ "," + results.klDivergencePrivateVsLimited + "," + results.ksTestValuePrivateVsLimited
						+ "," + results.klDivergencePrivateVsActual + "," + results.ksTestValuePrivateVsActual
						+ "," + results.klDivergenceLimitedVsActual + "," + results.ksTestValueLimitedVsActual
						+ "," + results.MSEPrivateVsActual + "," + results.MSELimitedVsActual
						+ "," + results.MSEPrivateVsLimited);
			}
			
			limitResultWriter.close();
			limitSSResultWriter.close();
			limitCBResultWriter.close();
			System.out.println("DONE");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void testVaryEpsilon() {
		try {
			int k = 50;
			int N = 100000;
			String inputDir = "/Users/kiennd/Downloads/location_entropy_data/synthetic_data/"
					+ "synthetic_data_L10000_N"
					+ N
					+ "_M100_maxC1000_ze1.0/"
					+ "FixM5C5VaryEpsilon/"
					+ "only_consider_published_locations/";
			String outputDir = inputDir + "only_published_" + k + "/";
			String rawEntropyInputFile = inputDir + "synthetic_data_L10000_N"
					+ N
					+ "_M100_maxC1000_ze1.0_C1000_entropy.csv";
			String rawEntropyOutputFile = outputDir + "synthetic_data_L10000_N"
					+ N
					+ "_M100_maxC1000_ze1.0_C1000_entropy.csv";
			double[] epsilons = new double[] {0.02, 0.1, 0.2, 1, 2};
			int M = 5;
			String testResultLimitFile = outputDir + "synthetic_data_L10000_N"
					+ N
					+ "_M5_maxC1000_ze1.0_DP_LIMIT_epsALL_Delta1e-8_minSen1e-3_NOTuseM_histogram_bucketSize0.1_test_results.csv";
			String testResultLimitSSFile = outputDir + "synthetic_data_L10000_N"
					+ N
					+ "_M5_maxC1000_ze1.0_DP_LIMIT_SS_epsALL_Delta1e-8_minSen1e-3_NOTuseM_histogram_bucketSize0.1_test_results.csv";
			String testResultLimitCBFile = outputDir + "synthetic_data_L10000_N"
					+ N
					+ "_M5_maxC1000_ze1.0_DP_LIMIT_CROWD_epsALL_Delta1e-8_minSen1e-3_NOTuseM_histogram_bucketSize0.1_test_results.csv";
			
			PrintWriter limitResultWriter = new PrintWriter(testResultLimitFile);
			PrintWriter limitSSResultWriter = new PrintWriter(testResultLimitSSFile);
			PrintWriter limitCBResultWriter = new PrintWriter(testResultLimitCBFile);
			for (int i = 0; i < epsilons.length; i++) {
				double eps = epsilons[i];
				String epsStr = DPUtil.toEpsilonString(eps);
				String limitFile = "synthetic_data_L10000_N"
						+ N
						+ "_M"
						+ M
						+ "_maxC1000_ze1.0_C5_entropy_DP_"
						+ "LIMIT"
						+ "_eps"
						+ epsStr
						+ "_Delta1e-8_minSen1e-3_NOTuseM.csv";
				String limitSSFile = "synthetic_data_L10000_N"
						+ N
						+ "_M"
						+ M
						+ "_maxC1000_ze1.0_C5_entropy_DP_"
						+ "LIMIT_SS"
						+ "_eps"
						+ epsStr
						+ "_Delta1e-8_minSen1e-3_NOTuseM.csv";
				String limitCBFile = "synthetic_data_L10000_N"
						+ N
						+ "_M"
						+ M
						+ "_maxC1000_ze1.0_C5_entropy_DP_"
						+ "LIMIT_CROWD"
						+ "_eps"
						+ epsStr
						+ "_Delta1e-8_minSen1e-3_NOTuseM.csv";
				String inputLimitFile = inputDir + limitFile;
				String inputLimitSSFile = inputDir + limitSSFile;
				String inputLimitCBFile = inputDir + limitCBFile;
				
				String outputLimitFile = outputDir + limitFile;
				String outputLimitSSFile = outputDir + limitSSFile;
				String outputLimitCBFile = outputDir + limitCBFile;
				
				removeUnpublishedLocation(k, 
						inputLimitFile, inputLimitSSFile, inputLimitCBFile, rawEntropyInputFile,
						outputLimitFile, outputLimitSSFile, outputLimitCBFile, rawEntropyOutputFile);
				
				double bucketSize = 0.1;
				int C = 5;
				
				ArrayList<LocationEntropyInfo> rawLocationEntropyList = LocationEntropyIO.readLocationEntropy(rawEntropyOutputFile);
				ArrayList<LocationEntropyInfo> limitLocationEntropyList = LocationEntropyIO.readLocationEntropy(outputLimitFile);
				ArrayList<LocationEntropyInfo> limitSSLocationEntropyList = LocationEntropyIO.readLocationEntropy(outputLimitSSFile);
				ArrayList<LocationEntropyInfo> limitCBLocationEntropyList = LocationEntropyIO.readLocationEntropy(outputLimitCBFile);
				
				MeasurementResults results;
				results = LocationEntropyDPMeasureEntropyEvaluator.evaluateEntropy(rawLocationEntropyList, limitLocationEntropyList, bucketSize);
				limitResultWriter.println(epsStr
						+ "," + C 
						+ "," + results.klDivergencePrivateVsLimited + "," + results.ksTestValuePrivateVsLimited
						+ "," + results.klDivergencePrivateVsActual + "," + results.ksTestValuePrivateVsActual
						+ "," + results.klDivergenceLimitedVsActual + "," + results.ksTestValueLimitedVsActual
						+ "," + results.MSEPrivateVsActual + "," + results.MSELimitedVsActual
						+ "," + results.MSEPrivateVsLimited);
				
				results = LocationEntropyDPMeasureEntropyEvaluator.evaluateEntropy(rawLocationEntropyList, limitSSLocationEntropyList, bucketSize);
				limitSSResultWriter.println(epsStr
						+ "," + C 
						+ "," + results.klDivergencePrivateVsLimited + "," + results.ksTestValuePrivateVsLimited
						+ "," + results.klDivergencePrivateVsActual + "," + results.ksTestValuePrivateVsActual
						+ "," + results.klDivergenceLimitedVsActual + "," + results.ksTestValueLimitedVsActual
						+ "," + results.MSEPrivateVsActual + "," + results.MSELimitedVsActual
						+ "," + results.MSEPrivateVsLimited);
				
				results = LocationEntropyDPMeasureEntropyEvaluator.evaluateEntropy(rawLocationEntropyList, limitCBLocationEntropyList, bucketSize);
				limitCBResultWriter.println(epsStr
						+ "," + C 
						+ "," + results.klDivergencePrivateVsLimited + "," + results.ksTestValuePrivateVsLimited
						+ "," + results.klDivergencePrivateVsActual + "," + results.ksTestValuePrivateVsActual
						+ "," + results.klDivergenceLimitedVsActual + "," + results.ksTestValueLimitedVsActual
						+ "," + results.MSEPrivateVsActual + "," + results.MSELimitedVsActual
						+ "," + results.MSEPrivateVsLimited);
			}
			
			limitResultWriter.close();
			limitSSResultWriter.close();
			limitCBResultWriter.close();
			System.out.println("DONE");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void testVaryC() {
		try {
			int k = 50;
			int N = 10000000;
			String inputDir = "/Users/kiennd/Downloads/location_entropy_data/synthetic_data/"
					+ "synthetic_data_L10000_N"
					+ N
					+ "_M100_maxC1000_ze1.0/"
					+ "FixM5Epsilon5VaryC/"
					+ "only_consider_published_locations/";
			String outputDir = inputDir + "only_published_" + k + "/";
			String rawEntropyInputFile = inputDir + "synthetic_data_L10000_N"
					+ N
					+ "_M100_maxC1000_ze1.0_C1000_entropy.csv";
			String rawEntropyOutputFile = outputDir + "synthetic_data_L10000_N"
					+ N
					+ "_M100_maxC1000_ze1.0_C1000_entropy.csv";
			double eps = 1;
			int M = 5;
			String testResultLimitFile = outputDir + "synthetic_data_L10000_N"
					+ N
					+ "_M5_maxC1000_ze1.0_DP_LIMIT_eps1_Delta1e-8_minSen1e-3_NOTuseM_histogram_bucketSize0.1_test_results.csv";
			String testResultLimitSSFile = outputDir + "synthetic_data_L10000_N"
					+ N
					+ "_M5_maxC1000_ze1.0_DP_LIMIT_SS_eps1_Delta1e-8_minSen1e-3_NOTuseM_histogram_bucketSize0.1_test_results.csv";
			String testResultLimitCBFile = outputDir + "synthetic_data_L10000_N"
					+ N
					+ "_M5_maxC1000_ze1.0_DP_LIMIT_CROWD_eps1_Delta1e-8_minSen1e-3_NOTuseM_histogram_bucketSize0.1_test_results.csv";
			
			PrintWriter limitResultWriter = new PrintWriter(testResultLimitFile);
			PrintWriter limitSSResultWriter = new PrintWriter(testResultLimitSSFile);
			PrintWriter limitCBResultWriter = new PrintWriter(testResultLimitCBFile);
			int startC = 1;
			int endC = 51;
			for (int i = startC; i < endC; i++) {
				int C = i;
				String epsStr = DPUtil.toEpsilonString(eps);
				String limitFile = "synthetic_data_L10000_N"
						+ N
						+ "_M"
						+ M
						+ "_maxC1000_ze1.0_C"
						+ C
						+ "_entropy_DP_"
						+ "LIMIT"
						+ "_eps"
						+ epsStr
						+ "_Delta1e-8_minSen1e-3_NOTuseM.csv";
				String limitSSFile = "synthetic_data_L10000_N"
						+ N
						+ "_M"
						+ M
						+ "_maxC1000_ze1.0_C"
						+ C
						+ "_entropy_DP_"
						+ "LIMIT_SS"
						+ "_eps"
						+ epsStr
						+ "_Delta1e-8_minSen1e-3_NOTuseM.csv";
				String limitCBFile = "synthetic_data_L10000_N"
						+ N
						+ "_M"
						+ M
						+ "_maxC1000_ze1.0_C"
						+ C
						+ "_entropy_DP_"
						+ "LIMIT_CROWD"
						+ "_eps"
						+ epsStr
						+ "_Delta1e-8_minSen1e-3_NOTuseM.csv";
				String inputLimitFile = inputDir + limitFile;
				String inputLimitSSFile = inputDir + limitSSFile;
				String inputLimitCBFile = inputDir + limitCBFile;
				
				String outputLimitFile = outputDir + limitFile;
				String outputLimitSSFile = outputDir + limitSSFile;
				String outputLimitCBFile = outputDir + limitCBFile;
				
				removeUnpublishedLocation(k, 
						inputLimitFile, inputLimitSSFile, inputLimitCBFile, rawEntropyInputFile,
						outputLimitFile, outputLimitSSFile, outputLimitCBFile, rawEntropyOutputFile);
				
				double bucketSize = 0.1;
				
				ArrayList<LocationEntropyInfo> rawLocationEntropyList = LocationEntropyIO.readLocationEntropy(rawEntropyOutputFile);
				ArrayList<LocationEntropyInfo> limitLocationEntropyList = LocationEntropyIO.readLocationEntropy(outputLimitFile);
				ArrayList<LocationEntropyInfo> limitSSLocationEntropyList = LocationEntropyIO.readLocationEntropy(outputLimitSSFile);
				ArrayList<LocationEntropyInfo> limitCBLocationEntropyList = LocationEntropyIO.readLocationEntropy(outputLimitCBFile);
				
				MeasurementResults results;
				results = LocationEntropyDPMeasureEntropyEvaluator.evaluateEntropy(rawLocationEntropyList, limitLocationEntropyList, bucketSize);
				limitResultWriter.println(epsStr
						+ "," + C 
						+ "," + results.klDivergencePrivateVsLimited + "," + results.ksTestValuePrivateVsLimited
						+ "," + results.klDivergencePrivateVsActual + "," + results.ksTestValuePrivateVsActual
						+ "," + results.klDivergenceLimitedVsActual + "," + results.ksTestValueLimitedVsActual
						+ "," + results.MSEPrivateVsActual + "," + results.MSELimitedVsActual
						+ "," + results.MSEPrivateVsLimited);
				
				results = LocationEntropyDPMeasureEntropyEvaluator.evaluateEntropy(rawLocationEntropyList, limitSSLocationEntropyList, bucketSize);
				limitSSResultWriter.println(epsStr
						+ "," + C 
						+ "," + results.klDivergencePrivateVsLimited + "," + results.ksTestValuePrivateVsLimited
						+ "," + results.klDivergencePrivateVsActual + "," + results.ksTestValuePrivateVsActual
						+ "," + results.klDivergenceLimitedVsActual + "," + results.ksTestValueLimitedVsActual
						+ "," + results.MSEPrivateVsActual + "," + results.MSELimitedVsActual
						+ "," + results.MSEPrivateVsLimited);
				
				results = LocationEntropyDPMeasureEntropyEvaluator.evaluateEntropy(rawLocationEntropyList, limitCBLocationEntropyList, bucketSize);
				limitCBResultWriter.println(epsStr
						+ "," + C 
						+ "," + results.klDivergencePrivateVsLimited + "," + results.ksTestValuePrivateVsLimited
						+ "," + results.klDivergencePrivateVsActual + "," + results.ksTestValuePrivateVsActual
						+ "," + results.klDivergenceLimitedVsActual + "," + results.ksTestValueLimitedVsActual
						+ "," + results.MSEPrivateVsActual + "," + results.MSELimitedVsActual
						+ "," + results.MSEPrivateVsLimited);
			}
			
			limitResultWriter.close();
			limitSSResultWriter.close();
			limitCBResultWriter.close();
			System.out.println("DONE");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Calculate number of locations that has at least n users
	 * @param locationEntropyList
	 * @param n
	 * @return
	 */
	public static int calNumberOfValidLocs(ArrayList<LocationEntropyInfo> locationEntropyList, int n) {
		int numValidLocs = 0;
		for (LocationEntropyInfo info : locationEntropyList) {
			numValidLocs += n <= info.getNumUser() ? 1 : 0;
		}
		return numValidLocs;
	}
	public static void calublishedRatio() {
		int M = 5;
		int C = 5;
		int NSparse = 100000;
		String inputDirSparse = "/Users/kiennd/Downloads/location_entropy_data/synthetic_data/"
				+ "synthetic_data_L10000_N"
				+ NSparse
				+ "_M100_maxC1000_ze1.0/";
		String rawEntropyFileSparse = inputDirSparse + "synthetic_data_L10000_N"
				+ NSparse
				+ "_M100_maxC1000_ze1.0_C1000_entropy.csv";
		String publishedEntropyFileSparse = inputDirSparse 
				+ "synthetic_data_L10000_N"
				+ NSparse
				+ "_M"
				+ M
				+ "_maxC1000_ze1.0_C"
				+ C
				+ "_entropy.csv";
		int NDense = 10000000;
		String inputDirDense = "/Users/kiennd/Downloads/location_entropy_data/synthetic_data/"
				+ "synthetic_data_L10000_N"
				+ NDense
				+ "_M100_maxC1000_ze1.0/";
		String rawEntropyFileDense = inputDirDense + "synthetic_data_L10000_N"
				+ NDense
				+ "_M100_maxC1000_ze1.0_C1000_entropy.csv";
		String publishedEntropyFileDense = inputDirDense 
				+ "synthetic_data_L10000_N"
				+ NDense
				+ "_M"
				+ M
				+ "_maxC1000_ze1.0_C"
				+ C
				+ "_entropy.csv";
		int maxK = 51;
		int validN = 20;
	
		ArrayList<LocationEntropyInfo> rawEntropyList = LocationEntropyIO.readLocationEntropy(rawEntropyFileSparse);
		double numValidLocsSparse = calNumberOfValidLocs(rawEntropyList, validN);
		rawEntropyList = LocationEntropyIO.readLocationEntropy(rawEntropyFileDense);
		double numValidLocsDense = calNumberOfValidLocs(rawEntropyList, validN);
		
		ArrayList<LocationEntropyInfo> locationEntropyListSparse = LocationEntropyIO.readLocationEntropy(publishedEntropyFileSparse);
		ArrayList<LocationEntropyInfo> locationEntropyListDense = LocationEntropyIO.readLocationEntropy(publishedEntropyFileDense);
		
		for (int k = validN; k < maxK; k++) {
			double publishedRatioSparse = (double) calNumberOfValidLocs(locationEntropyListSparse, k) / numValidLocsSparse;
			publishedRatioSparse *= 100;
			double publishedRatioDense = (double) calNumberOfValidLocs(locationEntropyListDense, k) / numValidLocsDense;
			publishedRatioDense *= 100;
			System.out.println(k + "," + publishedRatioSparse + "," + publishedRatioDense);
		}
	}
	
	public static void main(String[] args) {
		calublishedRatio();
	}
}
