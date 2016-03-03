package com.duykien.usc.locationentropy.calculator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;

import com.duykien.usc.locationentropy.gowalla.GowallaCheckin;
import com.duykien.usc.locationentropy.grid.GridUtility;

public class LocationEntropyCalculator {
	private static final Logger LOG = Logger.getLogger(LocationEntropyCalculator.class);
	
	/**
	 * Calculate list of user infos of each location from checkins.
	 * 
	 * A location is a cell from the grid. Key of the map is the cell index.
	 * Map: CellIndex -> (Map: UserId -> Num_checkins)
	 * 
	 * @param checkins
	 * @return data or null if error occurred
	 */
	public static Map<Integer, Map<Integer, Integer>> calLocationInfos(ArrayList<GowallaCheckin> checkins, GridUtility gridUtil) {
		Map<Integer, Map<Integer, Integer>> result = new HashMap<>();
		try {
			for (GowallaCheckin checkin : checkins) {
				Integer cellIndex = gridUtil.getCellIndex(checkin.getLatitude(), checkin.getLongitude());
				Map<Integer, Integer> userInfos = result.get(cellIndex);
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
				
				result.put(cellIndex, userInfos);
			}
		} catch (Exception e) {
			LOG.error("Error calculating location infos", e);
			result = null;
		}
		return result;
	}
	
	public static void calLocationInfosStatistics(Map<Integer, Map<Integer, Integer>> locationInfos) {
		try {
			LOG.info("Location size: " + locationInfos.size());
			DescriptiveStatistics numUserStat = new DescriptiveStatistics();
			for (Map.Entry<Integer, Map<Integer, Integer>> entry : locationInfos.entrySet()) {
				
			}
			
		} catch (Exception e) {
			LOG.error("Error calculatin location infos statistics", e);
		}
	}
}
