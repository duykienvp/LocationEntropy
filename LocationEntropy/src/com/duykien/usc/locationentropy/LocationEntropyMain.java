package com.duykien.usc.locationentropy;

import java.util.ArrayList;
import java.util.Map;

import org.apache.log4j.PropertyConfigurator;

import com.duykien.usc.locationentropy.calculator.LocationEntropyCalculator;
import com.duykien.usc.locationentropy.gowalla.GowallaCheckin;
import com.duykien.usc.locationentropy.gowalla.GowallaReader;
import com.duykien.usc.locationentropy.gowalla.GowallaUtility;
import com.duykien.usc.locationentropy.grid.GridUtility;
import com.duykien.usc.locationentropy.grid.GridUtilityFactory;
import com.duykien.usc.locationentropy.grid.GridUtilityFactory.Area;

public class LocationEntropyMain {
	public static final String LOG4J_PROPERTIES_FILE = "log4j.properties";
	
	public static void main(String[] args) {
		PropertyConfigurator.configure(LOG4J_PROPERTIES_FILE);
		
		String gowallaFile = "/Users/kiennd/Downloads/loc-gowalla_totalCheckins.txt";
		GridUtility laGridUtility = GridUtilityFactory.createGridUtility(Area.LOS_ANLEGES);
		ArrayList<GowallaCheckin> checkins = GowallaReader.read(gowallaFile, laGridUtility);
		System.out.println("Size = " + checkins.size());
		GowallaUtility.findMinMaxLatLong(checkins);
//		GowallaUtility.saveLatLong(checkins, "/Users/kiennd/Downloads/loc-gowalla_totalCheckins_LatLong_LA.txt");
		
		Map<Integer, Map<Integer, Integer>> locationInfos = LocationEntropyCalculator.calLocationInfos(checkins, laGridUtility);
		LocationEntropyCalculator.calLocationInfosStatistics(locationInfos);
	}

}
