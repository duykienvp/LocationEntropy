package com.duykien.usc.locationentropy;

import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.duykien.usc.locationentropy.calculator.EntropyCalculator;
import com.duykien.usc.locationentropy.calculator.LocationInfoUtil;
import com.duykien.usc.locationentropy.grid.GridUtilityFactory;
import com.duykien.usc.locationentropy.grid.GridUtilityFactory.Area;
import com.duykien.usc.locationentropy.locationdata.Checkin;
import com.duykien.usc.locationentropy.locationdata.LocationDataIO;
import com.duykien.usc.locationentropy.locationdata.LocationDataUtility;
import com.duykien.usc.locationentropy.util.Util;

public class LocationEntropyMain {
	public static final String LOG4J_PROPERTIES_FILE = "log4j.properties";
	private static final Logger LOG = Logger.getLogger(LocationEntropyMain.class);
	
	public static void main(String[] args) {
		PropertyConfigurator.configure(LOG4J_PROPERTIES_FILE);

		
	}
	
	public static void testDivide(ArrayList<Checkin> checkins) {
		ArrayList<Checkin> left = new ArrayList<>();
		ArrayList<Checkin> right = new ArrayList<>();
		for (double lng = -81.4659; lng < -81.4658; lng+=0.000001) {
			LocationDataUtility.divideByLongitude(checkins, left, right, lng);
			System.out.println("lng = " + lng + ", size left = " + left.size() + ", size right = " + right.size());
		}
	}
	
	public static void oldTest() {
//		String file = "/Users/kiennd/Downloads/loc-gowalla_totalCheckins.txt";
//		String file = "/Users/kiennd/Downloads/loc-brightkite_totalCheckins.txt";
//		GridUtility laGridUtility = GridUtilityFactory.createGridUtility(Area.LOS_ANLEGES_500);
		
//		ArrayList<Checkin> checkins = LocationDataReader.read(gowallaFile, laGridUtility);
//		LOG.info("Size LA = " + checkins.size());
//		GowallaUtility.findMinMaxLatLong(checkins);
//		GowallaUtility.saveLatLong(checkins, "/Users/kiennd/Downloads/loc-gowalla_totalCheckins_LatLong_LA.txt");
		
//		Map<Integer, Map<Integer, Integer>> locationInfos = LocationEntropyCalculator.calLocationInfos(checkins, laGridUtility);
//		LocationEntropyCalculator.calLocationInfosStatistics(locationInfos);
		
//		ArrayList<Checkin> checkins = LocationDataIO.read(file, GridUtilityFactory.createGridUtility(Area.GLOBAL));
//		LOG.info("Size GLOBAL = " + checkins.size());
//		testDivide(checkins);
		
//		Map<Integer, Map<Integer, Integer>> locationInfos = LocationInfoUtil.calLocationInfos(checkins, null);
//		LocationInfoUtil.calLocationInfosStatistics(locationInfos);
		
//		Map<Integer, Double> entropies = EntropyCalculator.calShannonEntropyMultiple(locationInfos);
//		DescriptiveStatistics stat = Util.calDescriptiveStatistics(entropies);
//		DescriptiveStatistics stat = LocationInfoUtil.calStatOfEntropyChangesWhenRemovingOneUser(locationInfos);
//		LOG.info("Entropy stat: " + stat.toString());
	}

}
