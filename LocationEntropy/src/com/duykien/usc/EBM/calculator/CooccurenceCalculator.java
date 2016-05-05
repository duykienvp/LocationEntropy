package com.duykien.usc.EBM.calculator;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.duykien.usc.EBM.dataIO.EBMDataIO;
import com.duykien.usc.EBM.dataprepare.DataPreparator;
import com.duykien.usc.EBM.util.EBMUtil;
import com.duykien.usc.locationentropy.grid.GridUtilityFactory;
import com.duykien.usc.locationentropy.grid.GridUtilityFactory.Area;
import com.duykien.usc.locationentropy.locationdata.Checkin;
import com.duykien.usc.locationentropy.locationdata.IntIntIntIntMap;
import com.duykien.usc.locationentropy.locationdata.IntIntIntMap;
import com.duykien.usc.locationentropy.locationdata.LocationDataIO;
import com.duykien.usc.locationentropy.locationdata.LocationDataUtility;
import com.duykien.usc.locationentropy.locationdata.IntIntPair;
import com.google.common.collect.Sets;

public class CooccurenceCalculator {
	public static final double PREC = 1e-12;
	private static final Logger LOG = Logger.getLogger(CooccurenceCalculator.class);

	public static void calculateCooccurrence(String inputFile, String outputFile) {
		LocationDataIO.Params readParams = new LocationDataIO.Params();
		readParams.file = inputFile;
		readParams.gridUtility = GridUtilityFactory.createGridUtility(Area.GLOBAL);
		readParams.isUserId = true;
		readParams.isTimestamp = true;
		readParams.isLatitude = true;
		readParams.isLongitude = true;
		readParams.isLocationId = true;
		ArrayList<Checkin> checkins = LocationDataIO.read(readParams);
		LOG.info("Size GLOBAL = " + checkins.size());

		// Divide checkins by longitude
		ArrayList<Checkin> checkinsWest = new ArrayList<>();
		ArrayList<Checkin> checkinsEast = new ArrayList<>();
		double lng = DataPreparator.GOWALLA_WEST_EAST_DIVIDED_LONGITUDE;
		LocationDataUtility.divideByLongitude(checkins, checkinsWest, checkinsEast, lng);
		LOG.info("lng = " + lng + ", size west = " + checkinsWest.size() + ", size east = " + checkinsEast.size());

		checkins.clear(); // REMOVE RAW CHECKINS

		String outputFileWest = outputFile + "_west";
		String outputFileEast = outputFile + "_east";
	}

	public static void calculateCooccurrence(ArrayList<Checkin> checkins, String prefix, String suffix) {
		try {
			PrintWriter coocWriter = new PrintWriter(prefix + "_cooccurences" + suffix);
			PrintWriter fWriter = new PrintWriter(prefix + "_F" + suffix);
			PrintWriter dWriter = new PrintWriter(prefix + "_D" + suffix);
			PrintWriter freqWriter = new PrintWriter(prefix + "_freq" + suffix);
			
			// Get users 
			Set<Integer> users = EBMUtil.getUserSet(checkins);

			// Get locations
			Set<Integer> locs = EBMUtil.getLocationSet(checkins);
			
			Map<IntIntPair, Integer> userLocCount = EBMUtil.getUserLocationCheckinsCount(checkins);

			// Get user->locations in the West and East
			Map<Integer, Set<Integer>> locsOfUser = EBMUtil.getLocationsOfEachUser(checkins);
			Map<Integer, Set<Integer>> usersOfLoc = EBMUtil.getUsersOfEachLocation(checkins);
			
			//Calculate cooccurrences
			IntIntIntMap freq = new IntIntIntMap();
			IntIntIntIntMap coocMap = new IntIntIntIntMap();
			
			int countUser = 0;
			for (Integer u : users) { // for each user u
				coocWriter.write(u + EBMDataIO.USER_SEPARATOR);
				Set<Integer> uLocs = locsOfUser.get(u); // get locations of u

				for (Integer loc : uLocs) { // for each location l of u
					IntIntPair ulp = new IntIntPair(u, loc);
					int ulCount = userLocCount.get(ulp);
					
					Set<Integer> vs = usersOfLoc.get(loc); // get users of l

					for (Integer v : vs) { // for each user v of l
						//(u, v, l)
						//NOTE: number of co-occurrences is calculated regardless of the checkin TIME
						IntIntPair vlp = new IntIntPair(v, loc);
						int vlCount = userLocCount.get(vlp);
						
						int coocurrencesCount = Math.min(ulCount, vlCount);
						coocMap.addEntry(u, v, loc, coocurrencesCount);
						freq.add(u, v, coocurrencesCount);
						coocWriter.write(v + EBMDataIO.COUNT_SEPARATOR 
								+ loc + EBMDataIO.COUNT_SEPARATOR 
								+ coocurrencesCount + EBMDataIO.USER_SEPARATOR);
					}
				}


				countUser++;
				if (countUser % 100 == 0) {
					LOG.info("Calculated cooccurence for " + countUser + " users");
				}
				
				coocWriter.println();
				coocWriter.flush();
			}
			coocWriter.close();
			freqWriter.close();
			fWriter.close();
			dWriter.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
