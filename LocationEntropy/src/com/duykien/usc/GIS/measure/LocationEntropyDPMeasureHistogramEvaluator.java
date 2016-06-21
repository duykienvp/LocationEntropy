package com.duykien.usc.GIS.measure;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.StringTokenizer;

import com.duykien.usc.GIS.Constants;
import com.duykien.usc.GIS.FileNameUtil;
import com.duykien.usc.GIS.DP.DPUtil;

public class LocationEntropyDPMeasureHistogramEvaluator {

	/**
	 * Read histogram from format:
	 * Format: index,#original,#noise 
	 * @param inputFile
	 * @param bucketIndices
	 * @param orgCount
	 * @param noisyCount
	 */
	public static void readHistogram(String inputFile, 
			ArrayList<Double> bucketIndices, 
			ArrayList<Integer> orgCount,
			ArrayList<Integer> noisyCount,
			ArrayList<Double> origCDF,
			ArrayList<Double> noisyCDF) {
		try {
			bucketIndices.clear();
			orgCount.clear();
			noisyCount.clear();
			origCDF.clear();
			noisyCDF.clear();
			
			BufferedReader reader = new BufferedReader(new FileReader(inputFile));
			
			String line = null;
			while ((line = reader.readLine()) != null) {
				StringTokenizer tokenizer = new StringTokenizer(line, ",");
				bucketIndices.add(Double.parseDouble(tokenizer.nextToken()));
				orgCount.add(Integer.parseInt(tokenizer.nextToken()));
				noisyCount.add(Integer.parseInt(tokenizer.nextToken()));
				origCDF.add(Double.parseDouble(tokenizer.nextToken()));
				noisyCDF.add(Double.parseDouble(tokenizer.nextToken()));
			}
			
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Read histogram from format:
	 * Format: index,#original,#noise 
	 * @param inputFile
	 * @param bucketIndices
	 * @param orgCount
	 * @param noisyCount
	 */
	public static LEHistogramInfo readHistogram(String inputFile) {
		try {
			LEHistogramInfo info = new LEHistogramInfo();
			
			BufferedReader reader = new BufferedReader(new FileReader(inputFile));
			
			String line = null;
			while ((line = reader.readLine()) != null) {
				StringTokenizer tokenizer = new StringTokenizer(line, ",");
				info.getBucketIndices().add(Double.parseDouble(tokenizer.nextToken()));
				info.getOrgCount().add(Integer.parseInt(tokenizer.nextToken()));
				info.getNoisyCount().add(Integer.parseInt(tokenizer.nextToken()));
				info.getOrigCDF().add(Double.parseDouble(tokenizer.nextToken()));
				info.getNoisyCDF().add(Double.parseDouble(tokenizer.nextToken()));
			}
			
			reader.close();
			
			return info;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Evaluate histogram:
	 * - KL-digerence
	 * @param histogramFile
	 */
	public static MeasurementResults evaluateHistogram(LEHistogramInfo uncutHistogramInfo,
			LEHistogramInfo histogramInfo, 
			String histogramErrorFile) {
		MeasurementResults results = new MeasurementResults();
		
		results.klDivergencePrivateVsLimited = KLDivergenceCalculator.klDivergence(histogramInfo.getNoisyCount(), histogramInfo.getOrgCount());
		results.ksTestValuePrivateVsLimited = KSTestCalculator.calKolmogorovSmirnovTest(histogramInfo.getNoisyCDF(), histogramInfo.getOrigCDF());
		
		results.klDivergencePrivateVsActual = KLDivergenceCalculator.klDivergence(histogramInfo.getNoisyCount(), uncutHistogramInfo.getOrgCount());
		results.ksTestValuePrivateVsActual = KSTestCalculator.calKolmogorovSmirnovTest(histogramInfo.getNoisyCDF(), uncutHistogramInfo.getOrigCDF());
		
		results.klDivergenceLimitedVsActual = KLDivergenceCalculator.klDivergence(histogramInfo.getOrgCount(), uncutHistogramInfo.getOrgCount());
		results.ksTestValueLimitedVsActual = KSTestCalculator.calKolmogorovSmirnovTest(histogramInfo.getOrigCDF(), uncutHistogramInfo.getOrigCDF());
		
		try {
			PrintWriter writer = new PrintWriter(histogramErrorFile);
			writer.println(results.toString());
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		return results;
	}
	
	/**
	 * Evaluate histogram:
	 * - KL-digerence
	 * @param histogramFile
	 */
	public static MeasurementResults evaluateHistogramDisabled(String uncutHistogramFile,
			String histogramFile, 
			String histogramErrorFile) {
		ArrayList<Double> bucketIndices = new ArrayList<>();
		ArrayList<Integer> orgCount = new ArrayList<>();
		ArrayList<Integer> noisyCount = new ArrayList<>();
		ArrayList<Double> origCDF = new ArrayList<>();
		ArrayList<Double> noisyCDF = new ArrayList<>();
		
		readHistogram(histogramFile, bucketIndices, orgCount, noisyCount, origCDF, noisyCDF);
		
		ArrayList<Integer> uncutCount = new ArrayList<>();
		ArrayList<Integer> uncutCountJunk = new ArrayList<>(); 	//this list is used purely to keep files in the same format
		ArrayList<Double> uncutCDF = new ArrayList<>();
		ArrayList<Double> uncutCDFJunk = new ArrayList<>();		//this list is used purely to keep files in the same format
		
		readHistogram(uncutHistogramFile, bucketIndices, uncutCount, uncutCountJunk, uncutCDF, uncutCDFJunk);
		
		MeasurementResults results = new MeasurementResults();
		
		results.klDivergencePrivateVsLimited = KLDivergenceCalculator.klDivergence(noisyCount, orgCount);
		results.ksTestValuePrivateVsLimited = KSTestCalculator.calKolmogorovSmirnovTest(noisyCDF, origCDF);
		
		results.klDivergencePrivateVsActual = KLDivergenceCalculator.klDivergence(noisyCount, uncutCount);
		results.ksTestValuePrivateVsActual = KSTestCalculator.calKolmogorovSmirnovTest(noisyCDF, uncutCDF);
		
		results.klDivergenceLimitedVsActual = KLDivergenceCalculator.klDivergence(orgCount, uncutCount);
		results.ksTestValueLimitedVsActual = KSTestCalculator.calKolmogorovSmirnovTest(origCDF, uncutCDF);
		
		try {
			PrintWriter writer = new PrintWriter(histogramErrorFile);
			writer.println(results.toString());
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		return results;
	}
	
	
	public static void main(String[] args) {
		String prefix = Constants.DATASET_PREFIX;
		int L = Constants.L;
		int N = Constants.N;
		int M = Constants.M;
		int maxC = Constants.MAX_C;
		double ze = Constants.ZIPF_EXPONENT;
		DecimalFormat df = Constants.DOUBLE_FORMAT;

		String dataGenerationOutputDir = Constants.DATA_GENERATOR_OUTPUT_DIR;

		int C = Constants.C;

		// Differential privacy
		boolean useM = false;
		String useMStr = useM ? "" : "NOT";
		
		double eps = Constants.DP_EPSILON;
		String epsStr = DPUtil.toEpsilonString(eps);
		double bucketSize = Constants.BUCKET_SIZE;
		String noisePerturbationMethodStr = Constants.DP_NOISE_PERTURBATION_METHOD_STR; 
		
		String histogramFile = FileNameUtil.getHistogramFileName(prefix, L, N, M, maxC, ze, df, dataGenerationOutputDir, C,
				epsStr,
				useMStr, bucketSize, noisePerturbationMethodStr);
		
		String histogramErrorFile = FileNameUtil.getHistogramErrorFileName(prefix, L, N, M, maxC, ze, df, dataGenerationOutputDir, C, epsStr, useMStr, bucketSize, noisePerturbationMethodStr);
		
		String uncutHistogramFile = FileNameUtil.getOriginalHistogramFileName(prefix, L, N, M, maxC, ze, df, dataGenerationOutputDir, bucketSize);
		
		evaluateHistogramDisabled(uncutHistogramFile, histogramFile, histogramErrorFile);

		System.out.println("Finished");

	}

}
