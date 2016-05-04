package com.duykien.usc.EBM.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.duykien.usc.locationentropy.locationdata.Checkin;

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
}
