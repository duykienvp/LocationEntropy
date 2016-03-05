package com.duykien.usc.locationentropy.locationdata;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.duykien.usc.locationentropy.grid.GridUtility;

public class LocationDataReader {
	public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssXXX";

	private static final Logger LOG = Logger.getLogger(LocationDataReader.class);

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

	/**
	 * Read check-ins.
	 * 
	 * See: http://snap.stanford.edu/data/loc-gowalla.html
	 * 
	 * @param filePath
	 * @return list of checkins or empty list if error occurred
	 */
	public static ArrayList<Checkin> read(String filePath, GridUtility gridUtility) {
		ArrayList<Checkin> data = new ArrayList<>();
		try {
			Map<String, Integer> str2IdMap = new HashMap<String, Integer>();
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			String line = null;

			while ((line = reader.readLine()) != null) {
				Checkin checkin = parseCheckin(line, str2IdMap);

				if (checkin != null && gridUtility.isWithin(checkin.getLatitude(), checkin.getLongitude())) {
					data.add(checkin);
				}
			}
			reader.close();
		} catch (Exception e) {
			LOG.error("Error reading Gowalla data from: " + filePath, e);
			data = new ArrayList<>();
		}
		return data;
	}

	/**
	 * Parse a checkin from a line
	 * 
	 * @param line
	 * @param str2IdMap mapping from string location id to integer id if current id format is not a number
	 * @return the checkin or null if error occurred
	 */
	public static Checkin parseCheckin(String line, Map<String, Integer> str2IdMap) {
		try {
			StringTokenizer tokenizer = new StringTokenizer(line);

			Integer uid = Integer.parseInt(tokenizer.nextToken());
			Date parsedDate = dateFormat.parse(tokenizer.nextToken());
			double latitude = Double.parseDouble(tokenizer.nextToken());
			double longitude = Double.parseDouble(tokenizer.nextToken());
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

			return new Checkin(uid, parsedDate.getTime(), latitude, longitude, locationId);
		} catch (Exception e) {
			LOG.error("Error parsing Gowalla checkin: " + line, e);
			return null;
		}
	}
}
