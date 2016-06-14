package com.duykien.usc.EBM.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.duykien.usc.locationentropy.locationdata.Checkin;
import com.duykien.usc.locationentropy.locationdata.IntIntPair;

public class EBMUtil {

	/**
	 * Get set of all users in a list of checkins
	 * 
	 * @param checkins
	 * @return
	 */
	public static Set<Integer> getUserSet(ArrayList<Checkin> checkins) {
		Set<Integer> users = new HashSet<>();

		for (Checkin checkin : checkins) {
			users.add(checkin.getUserId());
		}

		return users;
	}
	
	/**
	 * Get set of all locations in a list of checkins
	 * 
	 * @param checkins
	 * @return
	 */
	public static Set<Integer> getLocationSet(ArrayList<Checkin> checkins) {
		Set<Integer> locations = new HashSet<>();

		for (Checkin checkin : checkins) {
			locations.add(checkin.getLocationId());
		}

		return locations;
	}
	
	/**
	 * Calculate the set of locations a user checked in
	 * Format: user id -> (set of locations)
	 * @param checkins
	 * @return
	 */
	public static Map<Integer, Set<Integer>> getLocationsOfEachUser(ArrayList<Checkin> checkins) {
		Map<Integer, Set<Integer>> res = new HashMap<>();
		
		for (Checkin checkin : checkins) {
			Set<Integer> locs = res.get(checkin.getUserId());
			if (locs == null) {
				locs = new HashSet<>();
			}
			
			locs.add(checkin.getLocationId());
			
			res.put(checkin.getUserId(), locs);
		}
		
		return res;
	}
	
	/**
	 * Calculate users who checked in to a location.
	 * Format: location -> (set of user ids)
	 * @param checkins
	 * @return
	 */
	public static Map<Integer, Set<Integer>> getUsersOfEachLocation(ArrayList<Checkin> checkins) {
		Map<Integer, Set<Integer>> res = new HashMap<>();
		
		for (Checkin checkin : checkins) {
			Set<Integer> users = res.get(checkin.getLocationId());
			if (users == null) {
				users = new HashSet<>();
			}
			
			users.add(checkin.getUserId());
			
			res.put(checkin.getLocationId(), users);
		}
		
		return res;
	}
	
	/**
	 * Calculate the number of checkins of a user to a location 
	 * Format: (user, location) -> count
	 * @param checkins
	 * @return
	 */
	public static Map<IntIntPair, Integer> getUserLocationCheckinsCount(ArrayList<Checkin> checkins) {
		return getUserLocationCheckinsCount(checkins, Integer.MAX_VALUE);
	}
	
	/**
	 * Calculate the number of checkins of a user to a location 
	 * with limitation to the maximum number of locations a user can checkin
	 * Format: (user, location) -> count
	 * @param checkins
	 * @return
	 */
	public static Map<IntIntPair, Integer> getUserLocationCheckinsCount(ArrayList<Checkin> checkins,
			int maxLocationsOfOneUser) {
		Map<IntIntPair, Integer> res = new HashMap<>();
		
		for (Checkin c : checkins) {
			IntIntPair ulp = new IntIntPair(c.getUserId(), c.getLocationId());
			Integer count = 0;
			if (res.containsKey(ulp)) {
				//get current count
				count = res.get(ulp);
			}
			
			//only add if we do not reach the maximum
			if (count < maxLocationsOfOneUser) {
				count++;
				res.put(ulp, count);
			}
		}
		
		return res;
	}
	
	/**
	 * Calculate total number of checkins that all users checkins to a location:
	 * Format: location -> count
	 * @param userLocCount
	 * @return
	 */
	public static Map<Integer, Integer> getLocationCheckinsCount(Map<IntIntPair, Integer> userLocCount) {
		Map<Integer, Integer> res = new HashMap<>();
		
		for (Map.Entry<IntIntPair, Integer> entry : userLocCount.entrySet()) {
			Integer locId = entry.getKey().num2;
			Integer count = res.get(locId);
			if (count == null) {
				count = 0;
			}
			count += entry.getValue();
			
			res.put(locId, count);
		}
		
		
		
		return res;
	}
}
