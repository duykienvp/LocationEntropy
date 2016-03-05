package com.duykien.usc.locationentropy.calculator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;

import com.duykien.usc.locationentropy.grid.GridUtility;
import com.duykien.usc.locationentropy.locationdata.Checkin;

public class LocationInfoUtil {
	
	private static final Logger LOG = Logger.getLogger(LocationInfoUtil.class);
	
	/**
	 * Calculate list of user infos of each location from checkins.
	 * 
	 * A location is a cell from the grid. Key of the map is the cell index.
	 * Map: CellIndex -> (Map: UserId -> Num_checkins)
	 * 
	 * @param checkins
	 * @param gridUtil GridUtility to calculate cell index. If null, use location id from the checkin
	 * @return data or null if error occurred
	 */
	public static Map<Integer, Map<Integer, Integer>> calLocationInfos(ArrayList<Checkin> checkins, GridUtility gridUtil) {
		Map<Integer, Map<Integer, Integer>> result = new HashMap<>();
		try {
			for (Checkin checkin : checkins) {
				Integer locationId = checkin.getLocationId();
				if (gridUtil != null) {
					locationId = gridUtil.getCellIndex(checkin.getLatitude(), checkin.getLongitude());
				}
				Map<Integer, Integer> userInfos = result.get(locationId);
				if (userInfos == null) {
					userInfos = new HashMap<>();
				}
				
				Integer userId = checkin.getUserId();
				Integer numCheckins = userInfos.get(userId);
				if (numCheckins == null) {
					numCheckins = 0;
				}
				numCheckins++;
				
				userInfos.put(userId, numCheckins);
				
				result.put(locationId, userInfos);
			}
		} catch (Exception e) {
			LOG.error("Error calculating location infos", e);
			result = null;
		}
		return result;
	}
	
	/**
	 * Calculate statistics of number of users visiting locations
	 * @param locationInfos
	 */
	public static void calLocationInfosStatistics(Map<Integer, Map<Integer, Integer>> locationInfos) {
		try {
			int min = 0;
			LOG.info("min = " + min);
			LOG.info("Location size: " + locationInfos.size());
			DescriptiveStatistics numUserStat = new DescriptiveStatistics();
			int maxNumUser = -1;
			for (Map.Entry<Integer, Map<Integer, Integer>> entry : locationInfos.entrySet()) {
				if (min <= entry.getValue().size()) {
					numUserStat.addValue(entry.getValue().size());
					maxNumUser = Math.max(maxNumUser, entry.getValue().size());
				}
			}
			LOG.info("Location users statistics: " + numUserStat.toString());
			/*
			int[] numUserCount = new int[maxNumUser + 1];
			for (int i = 0; i < maxNumUser + 1; i++) {
				numUserCount[i] = 0;
			}
			
			for (Map.Entry<Integer, Map<Integer, Integer>> entry : locationInfos.entrySet()) {
				if (min <= entry.getValue().size()) {
					numUserCount[entry.getValue().size()]++;
				}
			}
			for (int i = 1; i < maxNumUser + 1; i++) {
				System.out.println(i + " " + numUserCount[i]);
			}
			
			*/
			
			DescriptiveStatistics numCheckinStat = new DescriptiveStatistics();
			for (Map.Entry<Integer, Map<Integer, Integer>> entry : locationInfos.entrySet()) {
				for (Map.Entry<Integer, Integer> checkinEntry: entry.getValue().entrySet()) {
					if (min <= checkinEntry.getValue()) {
						numCheckinStat.addValue(checkinEntry.getValue());
					}
				}
			}
			LOG.info("Location checkins statistics: " + numCheckinStat.toString());
			
		} catch (Exception e) {
			LOG.error("Error calculatin location infos statistics", e);
		}
	}

}
