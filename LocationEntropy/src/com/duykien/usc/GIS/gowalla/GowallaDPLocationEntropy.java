package com.duykien.usc.GIS.gowalla;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.duykien.usc.GIS.Constants;
import com.duykien.usc.GIS.DPLocationEntropy;
import com.duykien.usc.GIS.FileNameUtil;
import com.duykien.usc.GIS.DP.DPUtil;
import com.duykien.usc.GIS.DP.LocationEntropyInfo;
import com.duykien.usc.GIS.DP.PertubationMethodFactory.NoisePertubationMethod;
import com.duykien.usc.GIS.datagenerator.VisitingDatasetGenerator;
import com.duykien.usc.GIS.entropycalculator.LocationEntropyCalculator;
import com.duykien.usc.GIS.io.LocationEntropyIO;
import com.duykien.usc.GIS.io.VisitingDatasetIO;
import com.duykien.usc.GIS.measure.LEHistogramInfo;
import com.duykien.usc.GIS.measure.LocationEntropyDPMeasureEntropyEvaluator;
import com.duykien.usc.GIS.measure.LocationEntropyDPMeasureHistogramEvaluator;
import com.duykien.usc.GIS.measure.LocationEntropyDPMeasureHistogramGenerator;
import com.duykien.usc.GIS.measure.MeasurementResults;
import com.duykien.usc.locationentropy.grid.GridUtility;
import com.duykien.usc.locationentropy.grid.GridUtilityFactory;
import com.duykien.usc.locationentropy.grid.GridUtilityFactory.Area;
import com.duykien.usc.locationentropy.locationdata.Checkin;
import com.duykien.usc.locationentropy.locationdata.LocationDataIO;

public class GowallaDPLocationEntropy {
	
	/**
	 * Convert Gowalla's checkins file to format of the program:
	 * Output format:
	 * each line: locationid,user1,visitCount1,user2,visitCount2,...
	 * @param gowallaInputFile
	 * @param gridUtility
	 * @param maxLocationsOfOneUser
	 * @param outputFile
	 */
	public static void prepare(String gowallaInputFile, 
			GridUtility gridUtility,
			int maxLocationsOfOneUser,
			String outputFile) {
		LocationDataIO.Params readParams = new LocationDataIO.Params();
		readParams.file = gowallaInputFile;
		readParams.gridUtility = gridUtility;
		readParams.isUserId = true;
		readParams.isTimestamp = true;
		readParams.isLatitude = true;
		readParams.isLongitude = true;
		readParams.isLocationId = true;
		readParams.maxLocationsOfOneUser = maxLocationsOfOneUser;
		ArrayList<Checkin> checkins = LocationDataIO.read(readParams);
		
		Map<Integer, Map<Integer, Integer>> locationCheckins = convertToCheckinsToLocations(checkins);
		//convert
		
		Set<Integer> userSet = new HashSet<>();
		
		ArrayList<ArrayList<Integer>> locationUserCheckinsList = new ArrayList<>();
		
		for (Integer l : locationCheckins.keySet()) {
			ArrayList<Integer> userCount = new ArrayList<>();
			for (Map.Entry<Integer, Integer> entry : locationCheckins.get(l).entrySet()) {
				userCount.add(entry.getKey());
				userCount.add(entry.getValue());
				userSet.add(entry.getKey());
			}
			
			locationUserCheckinsList.add(userCount);
		}
		
		//short by number of users;
		for (int i = 0; i < locationUserCheckinsList.size(); i++) {
			for (int j = i + 1; j < locationUserCheckinsList.size(); j++) {
				ArrayList<Integer> iList = locationUserCheckinsList.get(i);
				ArrayList<Integer> jList = locationUserCheckinsList.get(j);
				if (iList.size() < jList.size()) {
					ArrayList<Integer> tmpList = new ArrayList<>(iList);
					iList.clear();
					iList.addAll(jList);
					jList.clear();
					jList.addAll(tmpList);
				}
			}
		}
		
		Map<Integer, ArrayList<Integer>> convertedMap = new TreeMap<>();
		for (int i = 0; i < locationUserCheckinsList.size(); i++) {
			int locId = i+1;
			convertedMap.put(locId, locationUserCheckinsList.get(i));
		}
		VisitingDatasetIO.writeData(convertedMap, outputFile);
		System.out.println(locationCheckins.keySet().size() + ", " + userSet.size());
	}
	
	/**
	 * Convert checkins to Map: location-> (userid, checkin_count)
	 * @param checkins
	 * @return the converted map or null if error occurred
	 */
	public static Map<Integer, Map<Integer, Integer>> convertToCheckinsToLocations(ArrayList<Checkin> checkins) {
		try {
			Map<Integer, Map<Integer, Integer>> locationCheckins = new TreeMap<>();
			for (Checkin c : checkins) {
				VisitingDatasetGenerator.addData(locationCheckins, c.getLocationId(), c.getUserId(), 1);
			}
			return locationCheckins;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void testVaryM() {
		String prefix = GowallaContants.GOWALLA_DATASET_NY_PREFIX;
		int L = GowallaContants.GOWALLA_L;
		int N = GowallaContants.GOWALLA_N;
		int M = GowallaContants.GOWALLA_M;
		int maxM = GowallaContants.GOWALLA_MAX_M;
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
		String dataGenerationOutputFile = FileNameUtil.getDataGenerationOutputFile(prefix, L, N, maxM, maxC, ze, df, dataGenerationOutputDir);

		
		/*
		 * Original, uncut entropy
		 */		
		int C = GowallaContants.GOWALLA_C;
		String rawLocationEntropyFile = FileNameUtil.getOriginalEntropyFileName(prefix, L, N, maxM, maxC, ze, df, dataGenerationOutputDir);
		String uncutHistogramFile = FileNameUtil.getOriginalHistogramFileName(prefix, L, N, maxM, maxC, ze, df, dataGenerationOutputDir, bucketSize);


		boolean calActuals = false;
		if (calActuals) {
			Map<Integer, ArrayList<Integer>> visitMap = VisitingDatasetIO.readData(dataGenerationOutputFile);
			ArrayList<LocationEntropyInfo> leInfos = LocationEntropyCalculator.calLocationEntropy(visitMap, C, rawLocationEntropyFile, true);
			LocationEntropyDPMeasureHistogramGenerator.generateHistogram(leInfos, N, bucketSize, df, uncutHistogramFile);
		}
		
		int[] Ms = new int[] {1, 2, 5, 10, 20, 30};
		double fixedEps = 5;
		int kCrowd = 10;
		
		NoisePertubationMethod noisePertubationMethod = NoisePertubationMethod.LIMIT_CROWD;
		DPLocationEntropy.runTestFixCAndEpsilonVaryM(prefix, L, N, maxM, maxC, ze, df, dataGenerationOutputDir, delta, minSensitivity, kCrowd, useM, useMStr, bucketSize, uncutHistogramFile, C, noisePertubationMethod, rawLocationEntropyFile, fixedEps, Ms, false);
		
		/*
		for (int i = 0; i < Ms.length; i++) {
			M = Ms[i];
			eps = fixedEps / (double)M;
			String limitedFile = FileNameUtil.getDataGenerationOutputFile(prefix, L, N, M, maxC, ze, df, dataGenerationOutputDir);
			VisitingDatasetGenerator.limitMaximumNumOfLocationsPerUser(dataGenerationOutputFile, M, limitedFile);
//			int startC = Constants.START_C;
//			int endC = Constants.END_C;
			NoisePertubationMethod noisePertubationMethod = NoisePertubationMethod.LIMIT_CROWD;
			String noisePerturbationMethodStr = noisePertubationMethod.toString();
			int kCrowd = 10;
			ArrayList<LocationEntropyInfo> rawLocationEntropyList = LocationEntropyIO.readLocationEntropy(rawLocationEntropyFile);
			LEHistogramInfo uncutHistogramInfo = LocationEntropyDPMeasureHistogramEvaluator.readHistogram(uncutHistogramFile);
			Map<Integer, ArrayList<Integer>> visitMap = VisitingDatasetIO.readData(limitedFile);
			
			MeasurementResults results = DPLocationEntropy.runDPLocationEntropy(prefix, 
					L, 
					N,
					M, 
					maxC, 
					ze, 
					df, 
					dataGenerationOutputDir, 
					eps, 
					delta, 
					minSensitivity, 
					kCrowd, 
					useM, 
					useMStr, 
					bucketSize, 
					C, 
					noisePertubationMethod, 
					noisePerturbationMethodStr, 
					uncutHistogramInfo, 
					rawLocationEntropyList, 
					visitMap);
			String testResultFile = FileNameUtil.getTestResultsFileName(prefix, L, N, M, maxC, ze, df, DPUtil.toEpsilonString(eps), dataGenerationOutputDir, useMStr, bucketSize, noisePerturbationMethodStr);
			try {
				PrintWriter writer = new PrintWriter(testResultFile);
				writer.println(results.toString());
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		*/
	}
	
	public static void testVaryEpsilon() {
		String prefix = GowallaContants.GOWALLA_DATASET_NY_PREFIX;
		int L = GowallaContants.GOWALLA_L;
		int N = GowallaContants.GOWALLA_N;
		int M = GowallaContants.GOWALLA_M;
		int maxM = GowallaContants.GOWALLA_MAX_M;
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
		String dataGenerationOutputFile = FileNameUtil.getDataGenerationOutputFile(prefix, L, N, maxM, maxC, ze, df, dataGenerationOutputDir);

		
		/*
		 * Original, uncut entropy
		 */		
		int C = GowallaContants.GOWALLA_C;
		String rawLocationEntropyFile = FileNameUtil.getOriginalEntropyFileName(prefix, L, N, maxM, maxC, ze, df, dataGenerationOutputDir);
		String uncutHistogramFile = FileNameUtil.getOriginalHistogramFileName(prefix, L, N, maxM, maxC, ze, df, dataGenerationOutputDir, bucketSize);


		boolean calActuals = false;
		if (calActuals) {
			Map<Integer, ArrayList<Integer>> visitMap = VisitingDatasetIO.readData(dataGenerationOutputFile);
			ArrayList<LocationEntropyInfo> leInfos = LocationEntropyCalculator.calLocationEntropy(visitMap, C, rawLocationEntropyFile, true);
			LocationEntropyDPMeasureHistogramGenerator.generateHistogram(leInfos, N, bucketSize, df, uncutHistogramFile);
		}
		
		double[] epsilons = new double[] {0.02, 0.1, 0.2, 1, 2};
		double fixedEps = 5;
		int kCrowd = 10;
		
		NoisePertubationMethod noisePertubationMethod = NoisePertubationMethod.LIMIT_SS;
		DPLocationEntropy.runTestFixMAndCVaryEpsilon(prefix, L, N, M, maxM, maxC, ze, df, dataGenerationOutputDir, delta, minSensitivity, kCrowd, useM, useMStr, bucketSize, uncutHistogramFile, C, noisePertubationMethod, rawLocationEntropyFile, epsilons, false);

	}
	
	public static void main(String[] args) {
//		testVaryM();
		testVaryEpsilon();
		boolean shouldStop = true;
		if (shouldStop)
			return;
		
		String prefix = GowallaContants.GOWALLA_DATASET_NY_PREFIX;
		int L = GowallaContants.GOWALLA_L;
		int N = GowallaContants.GOWALLA_N;
		int M = GowallaContants.GOWALLA_M;
		int maxM = GowallaContants.GOWALLA_MAX_M;
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
		String dataGenerationOutputFile = FileNameUtil.getDataGenerationOutputFile(prefix, L, N, maxM, maxC, ze, df, dataGenerationOutputDir);
		
//		System.out.println("Started preparing checkins");
//		prepare(gowallaInputFile, gridUtility, maxM, dataGenerationOutputFile);
//		System.out.println("Finished preparing checkins");
		
		/*
		 * Original, uncut entropy
		 */		
		int C = GowallaContants.GOWALLA_C;
		String rawLocationEntropyFile = FileNameUtil.getOriginalEntropyFileName(prefix, L, N, maxM, maxC, ze, df, dataGenerationOutputDir);
		String uncutHistogramFile = FileNameUtil.getOriginalHistogramFileName(prefix, L, N, maxM, maxC, ze, df, dataGenerationOutputDir, bucketSize);


		boolean calActuals = false;
		if (calActuals) {
			Map<Integer, ArrayList<Integer>> visitMap = VisitingDatasetIO.readData(dataGenerationOutputFile);
			ArrayList<LocationEntropyInfo> leInfos = LocationEntropyCalculator.calLocationEntropy(visitMap, C, rawLocationEntropyFile, true);
			LocationEntropyDPMeasureHistogramGenerator.generateHistogram(leInfos, N, bucketSize, df, uncutHistogramFile);
		}
		
		testVaryM();
	}
}
