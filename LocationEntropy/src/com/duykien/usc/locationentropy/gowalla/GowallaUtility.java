package com.duykien.usc.locationentropy.gowalla;

import java.io.PrintWriter;
import java.util.ArrayList;

import org.apache.log4j.Logger;

public class GowallaUtility {
	
	
	private static final Logger LOG = Logger.getLogger(GowallaUtility.class);
	
	/**
	 * Find the min and max of latitudes and longtitudes
	 * @param checkins
	 */
	public static void findMinMaxLatLong(ArrayList<GowallaCheckin> checkins) {
		try {
			final double INF = 1000000000;
			double minLong = INF;
			double maxLong = -INF;
			double minLat = INF;
			double maxLat = -INF;
			
			for (GowallaCheckin checkin : checkins) {
				minLong = Math.min(minLong, checkin.getLongitude());
				minLat = Math.min(minLat, checkin.getLatitude());
				maxLong = Math.max(maxLong, checkin.getLongitude());
				maxLat = Math.max(maxLat, checkin.getLatitude());
			}
			
			LOG.info("minLong = " + minLong + "; maxLong = " + maxLong
					+ "; minLat = " + minLat + "; maxLat = " + maxLat);
		} catch (Exception e) {
			LOG.error("Error finding the min and max of latitudes and longtitudes", e);
		}
	}
	
	public static void saveLatLong(ArrayList<GowallaCheckin> checkins, String file) {
		try {
			PrintWriter writer = new PrintWriter(file);
			
			for (GowallaCheckin checkin : checkins) {
				writer.println(checkin.getLatitude() + "\t" + checkin.getLongitude());
			}
			
			writer.close();
		} catch (Exception e) {
			LOG.error("Error writing latitudes and longtitudes", e);
		}
	}
}
