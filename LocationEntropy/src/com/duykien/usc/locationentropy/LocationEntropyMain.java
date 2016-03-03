package com.duykien.usc.locationentropy;

import java.util.ArrayList;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.duykien.usc.locationentropy.calculator.LocationEntropyCalculator;
import com.duykien.usc.locationentropy.grid.GridUtility;
import com.duykien.usc.locationentropy.grid.GridUtilityFactory;
import com.duykien.usc.locationentropy.grid.GridUtilityFactory.Area;
import com.duykien.usc.locationentropy.locationdata.Checkin;
import com.duykien.usc.locationentropy.locationdata.LocationDataReader;
import com.duykien.usc.locationentropy.locationdata.LocationDataUtility;

public class LocationEntropyMain {
	public static final String LOG4J_PROPERTIES_FILE = "log4j.properties";
	private static final Logger LOG = Logger.getLogger(LocationEntropyMain.class);
	
	public static void main(String[] args) {
		PropertyConfigurator.configure(LOG4J_PROPERTIES_FILE);
		
		String gowallaFile = "/Users/kiennd/Downloads/loc-gowalla_totalCheckins.txt";
		GridUtility laGridUtility = GridUtilityFactory.createGridUtility(Area.LOS_ANLEGES_500);
		
		ArrayList<Checkin> checkins = LocationDataReader.read(gowallaFile, laGridUtility);
		LOG.info("Size LA = " + checkins.size());
//		GowallaUtility.findMinMaxLatLong(checkins);
//		GowallaUtility.saveLatLong(checkins, "/Users/kiennd/Downloads/loc-gowalla_totalCheckins_LatLong_LA.txt");
		
		Map<Integer, Map<Integer, Integer>> locationInfos = LocationEntropyCalculator.calLocationInfos(checkins, laGridUtility);
		LocationEntropyCalculator.calLocationInfosStatistics(locationInfos);
		
//		checkins = GowallaReader.read(gowallaFile, GridUtilityFactory.createGridUtility(Area.GLOBAL));
//		LOG.info("Size GLOBAL = " + checkins.size());
//		
//		locationInfos = LocationEntropyCalculator.calLocationInfos(checkins, null);
//		LocationEntropyCalculator.calLocationInfosStatistics(locationInfos);
	}

}
