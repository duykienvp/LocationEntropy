package com.duykien.usc.EBM.calculator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.duykien.usc.EBM.dataIO.CooccurenceIO;
import com.duykien.usc.EBM.dataprepare.DataPreparator;
import com.duykien.usc.EBM.util.EBMUtil;
import com.duykien.usc.locationentropy.grid.GridUtilityFactory;
import com.duykien.usc.locationentropy.grid.GridUtilityFactory.Area;
import com.duykien.usc.locationentropy.locationdata.Checkin;
import com.duykien.usc.locationentropy.locationdata.LocationDataIO;
import com.duykien.usc.locationentropy.locationdata.LocationDataUtility;
import com.google.common.collect.Sets;

public class CooccurenceCalculator {
	private static final Logger LOG = Logger.getLogger(CooccurenceCalculator.class);
	
	public static void calculateCooccurrence(String inputFile, String outputFile) {
		LocationDataIO.Params readParams = new LocationDataIO.Params();
		readParams.file = inputFile;
		readParams.gridUtility = GridUtilityFactory.createGridUtility(Area.GLOBAL);
		readParams.isUserId = true;
		readParams.isTimestamp = false;
		readParams.isLatitude = true;
		readParams.isLongitude = true;
		readParams.isLocationId = true;
		ArrayList<Checkin> checkins = LocationDataIO.read(readParams);
		LOG.info("Size GLOBAL = " + checkins.size());

		// Divide checkins by longitude
		ArrayList<Checkin> checkinsWest = new ArrayList<>();
		ArrayList<Checkin> checkinsEast = new ArrayList<>();
		double lng = DataPreparator.GOWALLA_WEST_EAST_DIVIDED_LONGITUDE;
		LocationDataUtility.divideByLongitude(checkins, checkinsWest, checkinsEast, lng);
		LOG.info("lng = " + lng + ", size west = " + checkinsWest.size() + ", size east = " + checkinsEast.size());
		checkins.clear(); //REMOVE RAW CHECKINS

		// Get users in the West and East
		Set<Integer> userWest = EBMUtil.getUserSet(checkinsWest);
		Set<Integer> userEast = EBMUtil.getUserSet(checkinsEast);
		LOG.info("User set size: west = " + userWest.size() + ", east = " + userEast.size() + ", intersect = "
				+ Sets.intersection(userWest, userEast).size());
		
		// Get locations in the West and East
		Set<Integer> locationWest = EBMUtil.getLocationSet(checkinsWest);
		Set<Integer> locationEast = EBMUtil.getLocationSet(checkinsEast);
		LOG.info("Location set size: west = " + locationWest.size() + ", east = " + locationEast.size() + ", intersect = "
				+ Sets.intersection(locationWest, locationEast).size());
		
		// Get user->locations in the West and East
		Map<Integer, Set<Integer>> locsOfUserWest = EBMUtil.getLocationsOfEachUser(checkinsWest);
		Map<Integer, Set<Integer>> usersOfLocWest = EBMUtil.getUsersOfEachLocation(checkinsWest);
		Map<Integer, Map<Integer, Integer>> cooc = calculateCooccurrences(locsOfUserWest, usersOfLocWest, userWest);
		CooccurenceIO.writeCooccurence(cooc, outputFile + "_west.txt", 2);
	}
	
	public static Map<Integer, Map<Integer, Integer>> calculateCooccurrences(Map<Integer, Set<Integer>> locsOfUser, Map<Integer, Set<Integer>> usersOfLoc, Set<Integer> users) {
		Map<Integer, Map<Integer, Integer>> res = new HashMap<>();
		
		int countUser = 0;
		for (Integer u : users) { //for each user u
			Map<Integer, Integer> cooccurrences = new HashMap<>();
			Set<Integer> uLocs = locsOfUser.get(u); //get locations of u
			
			for (Integer loc : uLocs) { //for each location l of u
				Set<Integer> locUsers = usersOfLoc.get(loc); //get users of l
				
				for (Integer v : locUsers) { //for each user v of l
					//co-occurrence between u and v 
					Integer count = cooccurrences.get(v); //get current count of co-occurrence between u and v
					if (count == null) {
						count = new Integer(0);
					}
					count++;
					
					cooccurrences.put(v, count);
				}
			}
			
			res.put(u, cooccurrences);
			
			countUser++;
			if (countUser % 100 == 0) {
				LOG.info("Calculated cooccurence for " + countUser + " users");
			}
		}
		
		return res;
	}
}
