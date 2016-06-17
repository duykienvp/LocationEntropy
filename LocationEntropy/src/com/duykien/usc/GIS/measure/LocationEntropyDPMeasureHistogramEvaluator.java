package com.duykien.usc.GIS.measure;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.StringTokenizer;

import com.duykien.usc.GIS.Constants;
import com.duykien.usc.GIS.FileNameUtil;

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
	 * Evaluate histogram:
	 * - KL-digerence
	 * @param histogramFile
	 */
	public static void evaluateHistogram(String histogramFile, String histogramErrorFile) {
		ArrayList<Double> bucketIndices = new ArrayList<>();
		ArrayList<Integer> orgCount = new ArrayList<>();
		ArrayList<Integer> noisyCount = new ArrayList<>();
		ArrayList<Double> origCDF = new ArrayList<>();
		ArrayList<Double> noisyCDF = new ArrayList<>();
		
		readHistogram(histogramFile, bucketIndices, orgCount, noisyCount, origCDF, noisyCDF);
		
		double klDivergence = KLDivergenceCalculator.klDivergence(noisyCount, orgCount);
		double ksTest = KSTestCalculator.calKolmogorovSmirnovTest(origCDF, noisyCDF);
		
		try {
			PrintWriter writer = new PrintWriter(histogramErrorFile);
			writer.println("KL-Divergence = " + klDivergence);
			writer.println("Kolmogorov–Smirnov test = " + ksTest);
			writer.close();
		} catch (Exception e) {
			
		}
	}
	
	
	public static void main(String[] args) {
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
		
		double bucketSize = Constants.BUCKET_SIZE;
		String histogramFile = FileNameUtil.getHistogramFileName(L, N, M, maxC, ze, df, dataGenerationOutputDir, C,
				useMStr, bucketSize);
		
		String histogramErrorFile = FileNameUtil.getHistogramErrorFileName(L, N, M, maxC, ze, df, dataGenerationOutputDir, C, useMStr, bucketSize);
		
		evaluateHistogram(histogramFile, histogramErrorFile);

		System.out.println("Finished");

	}

}
