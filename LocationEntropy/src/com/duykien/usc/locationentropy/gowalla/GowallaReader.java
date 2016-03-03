package com.duykien.usc.locationentropy.gowalla;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.duykien.usc.locationentropy.grid.GridUtility;

public class GowallaReader {
	public static final String GOWALLA_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssXXX";

	private static final Logger LOG = Logger.getLogger(GowallaReader.class);

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(GOWALLA_DATE_FORMAT);

	/**
	 * Read Gowalla check-ins.
	 * 
	 * See: http://snap.stanford.edu/data/loc-gowalla.html
	 * 
	 * @param filePath
	 * @return list of checkins or empty list if error occurred
	 */
	public static ArrayList<GowallaCheckin> read(String filePath, GridUtility gridUtility) {
		ArrayList<GowallaCheckin> data = new ArrayList<>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			String line = null;

			while ((line = reader.readLine()) != null) {
				GowallaCheckin checkin = parseGowallaCheckin(line);

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
	 * Parse a Gowalla checkin from a line
	 * 
	 * @param line
	 * @return the checkin or null if error occurred
	 */
	public static GowallaCheckin parseGowallaCheckin(String line) {
		try {
			StringTokenizer tokenizer = new StringTokenizer(line);

			Integer uid = Integer.parseInt(tokenizer.nextToken());
			Date parsedDate = dateFormat.parse(tokenizer.nextToken());
			double latitude = Double.parseDouble(tokenizer.nextToken());
			double longitude = Double.parseDouble(tokenizer.nextToken());
			Integer locationId = Integer.parseInt(tokenizer.nextToken());

			return new GowallaCheckin(uid, parsedDate.getTime(), latitude, longitude, locationId);
		} catch (Exception e) {
			LOG.error("Error parsing Gowalla checkin: " + line, e);
			return null;
		}
	}
}
