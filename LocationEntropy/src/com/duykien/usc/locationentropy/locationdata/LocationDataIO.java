package com.duykien.usc.locationentropy.locationdata;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.duykien.usc.locationentropy.grid.GridUtility;

public class LocationDataIO {
	public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssXXX";

	private static final Logger LOG = Logger.getLogger(LocationDataIO.class);

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
	
	private static final String SEPARATOR = "\t";
	
	public static class Params {
		public String file;
		public boolean isUserId;
		public boolean isTimestamp;
		public boolean isDatetimeFormat;
		public boolean isLatitude;
		public boolean isLongitude;
		public boolean isLocationId;
		public int limit;
		public int maxLocationsOfOneUser;
		public int maxCheckinsOfOneUserToOneLocation;
		public GridUtility gridUtility;
		
		public Params() {
			file = "";
			isUserId = false;
			isTimestamp = false;
			isDatetimeFormat = false;
			isLatitude = false;
			isLongitude = false;
			isLocationId = false;
			limit = 0;
			maxLocationsOfOneUser = Integer.MAX_VALUE;
			maxCheckinsOfOneUserToOneLocation = Integer.MAX_VALUE;
			gridUtility = null;
		}
	}

	/**
	 * Read check-ins.
	 * 
	 * See: http://snap.stanford.edu/data/loc-gowalla.html
	 * 
	 * @param filePath
	 * @return list of checkins or empty list if error occurred
	 */
	public static ArrayList<Checkin> read(Params params) {
		ArrayList<Checkin> data = new ArrayList<>();
		try {
			//map for limiting number of locations a user can checkin
			Map<Integer, Set<Integer>> userToSetOfLocations = new HashMap<>();
			Map<String, Integer> str2IdMap = new HashMap<String, Integer>();
			BufferedReader reader = new BufferedReader(new FileReader(params.file));
			String line = null;

			while ((line = reader.readLine()) != null) {
				Checkin checkin = parseCheckin(line, str2IdMap, params);

				if (checkin != null
						&& params.gridUtility != null
						&& params.gridUtility.isWithin(checkin.getLatitude(), checkin.getLongitude())
						&& checkLimitingNumberOfLocationsPerUser(params.maxLocationsOfOneUser, userToSetOfLocations, checkin)) {
					data.add(checkin);
				}
			}
			reader.close();
		} catch (Exception e) {
			LOG.error("Error reading data from: " + params.file, e);
			data = new ArrayList<>();
		}
		return data;
	}
	
	public static boolean checkLimitingNumberOfLocationsPerUser(int maxLoc, 
			Map<Integer, Set<Integer>> userToSetOfLocations, 
			Checkin checkin) {
		Set<Integer> locs = userToSetOfLocations.get(checkin.getUserId());
		if (locs == null) {
			locs = new HashSet<>();
		}
		//if this location was already accepted
		if (locs.contains(checkin.getLocationId()))
			return true;
		//if it is not, only accept when we still not reach the maximum
		if (locs.size() < maxLoc) {
			locs.add(checkin.getLocationId());
			userToSetOfLocations.put(checkin.getUserId(), locs);
			return true;
		}
		
		//reached the maximum
		return false;
	}

	/**
	 * Parse a checkin from a line
	 * @param line
	 * @param str2IdMap
	 * @param params
	 * @return
	 */
	public static Checkin parseCheckin(String line, Map<String, Integer> str2IdMap, Params params) {
		try {
			Checkin c = new Checkin();
			StringTokenizer tokenizer = new StringTokenizer(line);
			
			if (params.isUserId) {
				c.setUserId(Integer.parseInt(tokenizer.nextToken()));
			}
			
			if (params.isDatetimeFormat || params.isTimestamp) {
				if (params.isDatetimeFormat) {
					c.setTimestamp(dateFormat.parse(tokenizer.nextToken()).getTime());
				} else {
					c.setTimestamp(Long.parseLong(tokenizer.nextToken()));
				}
			}
			
			if (params.isLatitude) {
				c.setLatitude(Double.parseDouble(tokenizer.nextToken()));
			}
			
			if (params.isLongitude) {
				c.setLongitude(Double.parseDouble(tokenizer.nextToken()));
			}
			
			if (params.isLocationId) {
				String locationIdStr = tokenizer.nextToken();
				Integer locationId = 0;
				try {
					locationId = Integer.parseInt(locationIdStr);
				} catch (NumberFormatException nfe) {
					locationId = str2IdMap.get(locationIdStr);
					if (locationId == null) {
						locationId = str2IdMap.size() + 1;
						str2IdMap.put(locationIdStr, locationId);
					}
				}
				
				c.setLocationId(locationId);
			}

			return c;
		} catch (Exception e) {
			LOG.error("Error parsing Gowalla checkin: " + line, e);
			return null;
		}
	}
	
	/**
	 * Write data to a file in the same format in Gowalla dataset but use timestamp
	 * @param checkins
	 * @param params 
	 */
	public static void write(ArrayList<Checkin> checkins, Params params) {
		try {
			PrintWriter writer = new PrintWriter(params.file);
			
			for (int i = 0; i < checkins.size(); i++) {
				Checkin checkin = checkins.get(i);
				
				if (params.isUserId)
					writer.write("" + checkin.getUserId() + SEPARATOR);
				
				if (params.isTimestamp)
					writer.write("" + checkin.getTimestamp() + SEPARATOR);
				
				if (params.isLatitude)
					writer.write("" + checkin.getLatitude() + SEPARATOR);
				
				if (params.isLongitude)
					writer.write("" + checkin.getLongitude() + SEPARATOR);
				
				if (params.isLocationId)
					writer.write("" + checkin.getLocationId());
				
				if (i < checkins.size() - 1) 
					writer.println();
				
				writer.flush();
				
				if (0 < params.limit && params.limit < i)
					break;
			}
			
			writer.close();
		} catch (Exception e) {
			LOG.error("Error writing data to " + params.file, e);
		}
	}
}
