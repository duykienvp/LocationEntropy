package com.duykien.usc.EBM.calculator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

import com.duykien.usc.EBM.dataIO.EBMDataIO;
import com.duykien.usc.EBM.util.EBMUtil;
import com.duykien.usc.locationentropy.grid.GridUtilityFactory;
import com.duykien.usc.locationentropy.grid.GridUtilityFactory.Area;
import com.duykien.usc.locationentropy.locationdata.Checkin;
import com.duykien.usc.locationentropy.locationdata.IntIntPair;
import com.duykien.usc.locationentropy.locationdata.LocationDataIO;

public class LocationEntropyCalculator {
	public static final double PREC = 1e-12;
	private static final Logger LOG = Logger.getLogger(CooccurenceCalculator.class);

	public static void calculateLocationEntropy(String inputFile, 
			String outputFile,
			int maxLocationsOfOneUser,
			int maxCheckinsOfOneUserToOneLocation) {
		LocationDataIO.Params readParams = new LocationDataIO.Params();
		readParams.file = inputFile;
		readParams.gridUtility = GridUtilityFactory.createGridUtility(Area.GLOBAL);
		readParams.isUserId = true;
		readParams.isTimestamp = true;
		readParams.isLatitude = true;
		readParams.isLongitude = true;
		readParams.isLocationId = true;
		readParams.maxCheckinsOfOneUserToOneLocation = maxCheckinsOfOneUserToOneLocation;
		readParams.maxLocationsOfOneUser = maxLocationsOfOneUser;
		ArrayList<Checkin> checkins = LocationDataIO.read(readParams);
		
		calculateLocationEntropy(checkins, outputFile, maxLocationsOfOneUser);
	}

	public static void calculateLocationEntropy(ArrayList<Checkin> checkins, String outputFile, int maxLocationsOfOneUser) {
		try {
			Map<IntIntPair, Integer> userLocCount = EBMUtil.getUserLocationCheckinsCount(checkins, maxLocationsOfOneUser);
			Map<Integer, Integer> locationCheckinsCount = EBMUtil.getLocationCheckinsCount(userLocCount);
			
			Map<Integer, Double> leMap = new HashMap<>();
			
			for (IntIntPair ulp : userLocCount.keySet()) {
				int loc = ulp.num2;
				double ulCount = userLocCount.get(ulp);
				double lCount = locationCheckinsCount.get(loc);
				double pul = ulCount / lCount;
				
				Double hl = leMap.get(loc);
				if (hl == null) {
					hl = 0.0;
				}
				hl += - pul * Math.log(pul);
				leMap.put(loc, hl);
			}
				
			EBMDataIO.writeMap(leMap, outputFile);
		} catch (Exception e) {
			LOG.error("Error calculating location entropy", e);
		}
	}
}
