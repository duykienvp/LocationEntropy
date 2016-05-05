package com.duykien.usc.EBM.dataprepare;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.duykien.usc.EBM.dataIO.EBMDataIO;
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
	public static void createIndexMap(String inputFile, String userIdMapFile, String locationIdMapFile) {
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

		EBMDataIO.writeIdToIndex(locationIdToIndex, locationIdMapFile);

		// Find user ids
		Set<Integer> userIdsSet = new HashSet<>();
		for (Checkin c : checkins) {
			userIdsSet.add(c.getUserId());
		}
		ArrayList<Integer> userIdToIndex = new ArrayList<>(userIdsSet);

		Collections.sort(userIdToIndex);
		LOG.info("userIdToIndex size = " + userIdToIndex.size() + ", last id = "
				+ userIdToIndex.get(userIdToIndex.size() - 1));

		EBMDataIO.writeIdToIndex(userIdToIndex, userIdMapFile);
	}

	/**
	 * Convert Gowalla dataset to [user_id_index] [timestamp] [lat] [long]
	 * [location_id_index]
	 */
	public static void convertToIndex(String inputFile, String userIdToIndexMapFile, String locationIdToIndexMapFile,
			String outputFile) {
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

		// create map: location id -> index
		Map<Integer, Integer> locationIdToIndexMap = EBMDataIO.readIdToIndexMap(locationIdToIndexMapFile);

		// create map: user id -> index
		Map<Integer, Integer> userIdToIndexMap = EBMDataIO.readIdToIndexMap(userIdToIndexMapFile);

		// write
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
	public static void divideCheckinData(String inputFile, String outputEast, String outputWest) {
		LocationDataIO.Params params = new LocationDataIO.Params();
		params.file = inputFile;
		params.gridUtility = GridUtilityFactory.createGridUtility(Area.GLOBAL);
		params.isUserId = true;
		params.isTimestamp = true;
		params.isLatitude = true;
		params.isLongitude = true;
		params.isLocationId = true;
		ArrayList<Checkin> checkins = LocationDataIO.read(params);
		LOG.info("Size GLOBAL = " + checkins.size());

		// Divide checkins by longitude
		ArrayList<Checkin> west = new ArrayList<>();
		ArrayList<Checkin> east = new ArrayList<>();
		double lng = GOWALLA_WEST_EAST_DIVIDED_LONGITUDE;
		LocationDataUtility.divideByLongitude(checkins, west, east, lng);
		LOG.info("lng = " + lng + ", size west = " + west.size() + ", size east = " + east.size());

		params.file = outputWest;
		LocationDataIO.write(west, params);

		params.file = outputEast;
		LocationDataIO.write(east, params);
	}
	
	/**
	 * Divide Gowalla dataset to WEST and EAST part
	 */
	public static void calculateUserAndLocationSet(String checkinsFile, String usersFile, String locationFile) {
		LocationDataIO.Params params = new LocationDataIO.Params();
		params.file = checkinsFile;
		params.gridUtility = GridUtilityFactory.createGridUtility(Area.GLOBAL);
		params.isUserId = true;
		params.isTimestamp = true;
		params.isLatitude = true;
		params.isLongitude = true;
		params.isLocationId = true;
		ArrayList<Checkin> checkins = LocationDataIO.read(params);
		LOG.info("Size GLOBAL = " + checkins.size());

		ArrayList<Integer> users = new ArrayList<>(EBMUtil.getUserSet(checkins));
		Collections.sort(users);
		ArrayList<Integer> locs = new ArrayList<>(EBMUtil.getLocationSet(checkins));
		Collections.sort(locs);
		
		EBMDataIO.writeList(users, usersFile);
		EBMDataIO.writeList(locs, locationFile);
	}
	
	
	public static void divideRelationshipData(String relationshipFile, String userFileWest, String userFileEast, String outputFileWest, String outputFileEast) {
		try {
			Map<Integer, Set<Integer>> relationships = EBMDataIO.readRelationships(relationshipFile);
			Set<Integer> westUsers = new HashSet<>(EBMDataIO.readList(userFileWest));
			Set<Integer> eastUsers = new HashSet<>(EBMDataIO.readList(userFileEast));
			PrintWriter westWriter = new PrintWriter(outputFileWest);
			PrintWriter eastWriter = new PrintWriter(outputFileEast);
			
			ArrayList<Integer> us = new ArrayList<>(relationships.keySet());
			Collections.sort(us);
			for (Integer u : us) {
				ArrayList<Integer> vs = new ArrayList<>(relationships.get(u));
				Collections.sort(vs);
				
				for (Integer v : vs) {
					if (westUsers.contains(u) && westUsers.contains(v)) {
						westWriter.println(u + EBMDataIO.USER_SEPARATOR + v);
					}
					if (eastUsers.contains(u) && eastUsers.contains(v)) {
						eastWriter.println(u + EBMDataIO.USER_SEPARATOR + v);
					}
				}
			}
			
			westWriter.close();
			eastWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
