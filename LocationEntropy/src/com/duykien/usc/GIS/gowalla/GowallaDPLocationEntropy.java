package com.duykien.usc.GIS.gowalla;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.duykien.usc.GIS.Constants;
import com.duykien.usc.GIS.DPLocationEntropy;
import com.duykien.usc.GIS.FileNameUtil;
import com.duykien.usc.GIS.DP.DifferentialPrivacyNoisePerturbator.NoisePertubationMethod;
import com.duykien.usc.GIS.datagenerator.VisitingDatasetGenerator;
import com.duykien.usc.GIS.entropycalculator.LocationEntropyCalculator;
import com.duykien.usc.GIS.measure.LocationEntropyDPMeasureHistogramGenerator;
import com.duykien.usc.locationentropy.grid.GridUtility;
import com.duykien.usc.locationentropy.grid.GridUtilityFactory;
import com.duykien.usc.locationentropy.grid.GridUtilityFactory.Area;
import com.duykien.usc.locationentropy.locationdata.Checkin;
import com.duykien.usc.locationentropy.locationdata.LocationDataIO;

public class GowallaDPLocationEntropy {
	
	public static void prepare(String gowallaInputFile, 
			GridUtility gridUtility,
			int numLoc,
			int maxLocationsOfOneUser,
			String outputFile) {
		LocationDataIO.Params readParams = new LocationDataIO.Params();
		readParams.file = gowallaInputFile;
		readParams.gridUtility = GridUtilityFactory.createGridUtility(Area.GLOBAL);
		readParams.isUserId = true;
		readParams.isTimestamp = true;
		readParams.isLatitude = true;
		readParams.isLongitude = true;
		readParams.isLocationId = true;
		readParams.maxLocationsOfOneUser = maxLocationsOfOneUser;
		ArrayList<Checkin> checkins = LocationDataIO.read(readParams);
		
		Map<Integer, Map<Integer, Integer>> locationCheckins = convertToCheckinsToLocations(checkins);
		
		VisitingDatasetGenerator.writeData(locationCheckins, numLoc, outputFile);
	}
	
	/**
	 * Convert checkins to Map: location-> (userid, checkin_count)
	 * @param checkins
	 * @return the converted map or null if error occurred
	 */
	public static Map<Integer, Map<Integer, Integer>> convertToCheckinsToLocations(ArrayList<Checkin> checkins) {
		try {
			Map<Integer, Map<Integer, Integer>> locationCheckins = new HashMap<>();
			for (Checkin c : checkins) {
				VisitingDatasetGenerator.addData(locationCheckins, c.getLocationId(), c.getUserId(), 1);
			}
			return locationCheckins;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args) {
		String prefix = GowallaContants.GOWALLA_DATASET_NY_PREFIX;
		int L = GowallaContants.GOWALLA_L;
		int N = GowallaContants.GOWALLA_N;
		int M = GowallaContants.GOWALLA_M;
		int maxC = GowallaContants.GOWALLA_MAX_C;
		double ze = GowallaContants.GOWALLA_ZIPF_EXPONENT;
		DecimalFormat df = GowallaContants.DOUBLE_FORMAT; 
		
		double eps = Constants.DP_EPSILON; 
		double delta = Constants.DP_DELTA;
		double minSensitivity = Constants.MIN_SENSITIVITY;
		
		boolean useM = Constants.USE_M;
		String useMStr = useM ? "" : "NOT";
		
		double bucketSize = Constants.BUCKET_SIZE;
		
		GridUtility gridUtility = GridUtilityFactory.createGridUtility(Area.NEWYORK_100);
		
		String dataGenerationOutputDir = GowallaContants.GOWALLA_DATA_GENERATOR_OUTPUT_DIR;
		String gowallaInputFile = dataGenerationOutputDir + GowallaContants.GOWALLA_CHECKINS_FILE;
		String dataGenerationOutputFile = FileNameUtil.getDataGenerationOutputFile(prefix, L, N, M, maxC, ze, df, dataGenerationOutputDir);
		
		System.out.println("Started preparing checkins");
//		prepare(gowallaInputFile, gridUtility, L, M, dataGenerationOutputFile);
		System.out.println("Finished preparing checkins");
		
		/*
		 * Original, uncut entropy
		 */
		String uncutHistogramFile = FileNameUtil.getOriginalHistogramFileName(prefix, L, N, M, maxC, ze, df, dataGenerationOutputDir, bucketSize);
		/*
		String locationEntropyOutputFile = FileNameUtil.getOriginalEntropyFileName(prefix, L, N, M, maxC, ze, df, dataGenerationOutputDir);
		LocationEntropyCalculator.calLocationEntropy(dataGenerationOutputFile, maxC, locationEntropyOutputFile);
		LocationEntropyDPMeasureHistogramGenerator.generateHistogram(locationEntropyOutputFile, N, bucketSize, df, uncutHistogramFile);
		*/		
		int startC = Constants.START_C;
		int endC = Constants.END_C;
		NoisePertubationMethod noisePertubationMethod = Constants.DP_NOISE_PERTURBATION_METHOD;
		String noisePerturbationMethodStr = Constants.DP_NOISE_PERTURBATION_METHOD_STR; 
		DPLocationEntropy.runTestForAllC(prefix, L, N, M, maxC, ze, df, dataGenerationOutputDir, eps, delta, minSensitivity, useM, useMStr, bucketSize, uncutHistogramFile, startC, endC, noisePertubationMethod, noisePerturbationMethodStr);
	}
}
