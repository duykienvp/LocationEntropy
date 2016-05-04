package com.duykien.usc.EBM.dataprepare;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.duykien.usc.EBM.util.EBMUtil;
import com.duykien.usc.locationentropy.grid.GridUtilityFactory;
import com.duykien.usc.locationentropy.grid.GridUtilityFactory.Area;
import com.duykien.usc.locationentropy.locationdata.Checkin;
import com.duykien.usc.locationentropy.locationdata.LocationDataIO;
import com.duykien.usc.locationentropy.locationdata.LocationDataUtility;

public class DataPreparator {
	private static final Logger LOG = Logger.getLogger(DataPreparator.class);

	public static final double GOWALLA_WEST_EAST_DIVIDED_LONGITUDE = -81.465882;

	/**
	 * Convert Gowalla dataset to [user_id_index] [timestamp] [lat] [long]
	 * [location_id_index]
	 */
	public static void convertToIndex(String inputFile, String outputFile) {
		// Read data from file
		LocationDataIO.Params readParams = new LocationDataIO.Params();
		readParams.file = inputFile;
		readParams.gridUtility = GridUtilityFactory.createGridUtility(Area.GLOBAL);
		readParams.isUserId = true;
		readParams.isDatetimeFormat = true;
		readParams.isLatitude = true;
		readParams.isLongitude = true;
		readParams.isLocationId = true;
		
		ArrayList<Checkin> checkins = LocationDataIO.read(readParams);
		LOG.info("Size GLOBAL = " + checkins.size());

		// Find location ids
		Set<Integer> locationIdsSet = new HashSet<>();
		for (Checkin c : checkins) {
			locationIdsSet.add(c.getLocationId());
		}
		ArrayList<Integer> locationIdToIndex = new ArrayList<>(locationIdsSet);

		Collections.sort(locationIdToIndex);
		LOG.info("LocationIds size = " + locationIdToIndex.size() + ", last id = "
				+ locationIdToIndex.get(locationIdToIndex.size() - 1));

		// create map: location id -> index
		Map<Integer, Integer> locationIdToIndexMap = new HashMap<>();
		for (int i = 0; i < locationIdToIndex.size(); i++) {
			locationIdToIndexMap.put(locationIdToIndex.get(i), i);
		}

		// Find location ids
		Set<Integer> userIdsSet = new HashSet<>();
		for (Checkin c : checkins) {
			userIdsSet.add(c.getUserId());
		}
		ArrayList<Integer> userIdToIndex = new ArrayList<>(userIdsSet);

		Collections.sort(userIdToIndex);
		LOG.info("userIdToIndex size = " + userIdToIndex.size() + ", last id = "
				+ userIdToIndex.get(userIdToIndex.size() - 1));

		// create map: user id -> index
		Map<Integer, Integer> userIdToIndexMap = new HashMap<>();
		for (int i = 0; i < userIdToIndex.size(); i++) {
			userIdToIndexMap.put(userIdToIndex.get(i), i);
		}
		
		//write
		ArrayList<Checkin> convertedCheckins = new ArrayList<>();
		for (int i = 0; i < checkins.size(); i++) {
			Checkin org = checkins.get(i);
			Checkin c = new Checkin();
			c.setUserId(userIdToIndexMap.get(org.getUserId()));
			c.setTimestamp(org.getTimestamp());
			c.setLatitude(org.getLatitude());
			c.setLongitude(org.getLongitude());
			c.setLocationId(locationIdToIndexMap.get(org.getLocationId()));
			
			convertedCheckins.add(c);
		}
		
		LocationDataIO.Params writeParams = new LocationDataIO.Params();
		writeParams.file = outputFile;
		writeParams.isUserId = true;
		writeParams.isTimestamp = true;
		writeParams.isLatitude = true;
		writeParams.isLongitude = true;
		writeParams.isLocationId = true;
		
		LocationDataIO.write(convertedCheckins, writeParams);
	}

	/**
	 * Divide Gowalla dataset to WEST and EAST part
	 */
	public static void divideData(String inputFile, String outputDir) {
		LocationDataIO.Params readParams = new LocationDataIO.Params();
		readParams.file = inputFile;
		readParams.gridUtility = GridUtilityFactory.createGridUtility(Area.GLOBAL);
		readParams.isUserId = true;
		readParams.isTimestamp = true;
		readParams.isLatitude = true;
		readParams.isLongitude = true;
		readParams.isLocationId = true;
		ArrayList<Checkin> checkins = LocationDataIO.read(readParams);
		LOG.info("Size GLOBAL = " + checkins.size());

		// Divide checkins by longitude
		ArrayList<Checkin> west = new ArrayList<>();
		ArrayList<Checkin> east = new ArrayList<>();
		double lng = GOWALLA_WEST_EAST_DIVIDED_LONGITUDE;
		LocationDataUtility.divideByLongitude(checkins, west, east, lng);
		LOG.info("lng = " + lng + ", size west = " + west.size() + ", size east = " + east.size());

		// Get users in the West and East
		Set<Integer> userWest = EBMUtil.getUserSet(west);
		Set<Integer> userEast = EBMUtil.getUserSet(east);
		int countUserIntersect = 0;
		for (Integer i : userWest) {
			countUserIntersect += (userEast.contains(i) ? 1 : 0);
		}
		LOG.info("User set size: west = " + userWest.size() + ", east = " + userEast.size() + ", intersect = "
				+ countUserIntersect);
	}
}
