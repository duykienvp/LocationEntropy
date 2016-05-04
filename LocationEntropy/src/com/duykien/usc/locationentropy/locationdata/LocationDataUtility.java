package com.duykien.usc.locationentropy.locationdata;

import java.io.PrintWriter;
import java.util.ArrayList;

import org.apache.log4j.Logger;

public class LocationDataUtility {
	
	
	private static final Logger LOG = Logger.getLogger(LocationDataUtility.class);
	
	/**
	 * Find the min and max of latitudes and longtitudes
	 * @param checkins
	 */
	public static void findMinMaxLatLong(ArrayList<Checkin> checkins) {
		try {
			final double INF = 1000000000;
			double minLong = INF;
			double maxLong = -INF;
			double minLat = INF;
			double maxLat = -INF;
			
			for (Checkin checkin : checkins) {
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
	
	public static void divideByLongitude(ArrayList<Checkin> checkins, ArrayList<Checkin> left, ArrayList<Checkin> right, double lng) {
		try {
			left.clear();
			right.clear();
			for (Checkin checkin : checkins) {
				if (checkin.getLongitude() < lng) 
					left.add(checkin);
				else
					right.add(checkin);
			}
		} catch (Exception e) {
			LOG.error("Error dividing", e);
		}
	}
	
	public static void saveLatLong(ArrayList<Checkin> checkins, String file) {
		try {
			PrintWriter writer = new PrintWriter(file);
			
			for (Checkin checkin : checkins) {
				writer.println(checkin.getLatitude() + "\t" + checkin.getLongitude());
			}
			
			writer.close();
		} catch (Exception e) {
			LOG.error("Error writing latitudes and longtitudes", e);
		}
	}
}
