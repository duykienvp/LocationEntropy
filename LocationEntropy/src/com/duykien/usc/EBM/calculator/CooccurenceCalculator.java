package com.duykien.usc.EBM.calculator;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.duykien.usc.EBM.dataIO.EBMDataIO;
import com.duykien.usc.EBM.util.EBMUtil;
import com.duykien.usc.locationentropy.grid.GridUtilityFactory;
import com.duykien.usc.locationentropy.grid.GridUtilityFactory.Area;
import com.duykien.usc.locationentropy.locationdata.Checkin;
import com.duykien.usc.locationentropy.locationdata.LocationDataIO;
import com.duykien.usc.locationentropy.locationdata.IntIntPair;

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
		
		calculateCooccurrence(checkins, outputFile);
	}

	public static void calculateCooccurrence(ArrayList<Checkin> checkins, String outputFile) {
		try {
			PrintWriter coocWriter = new PrintWriter(outputFile);
			
			Map<IntIntPair, Integer> userLocCount = EBMUtil.getUserLocationCheckinsCount(checkins);

			// Get user->locations in the West and East
			Map<Integer, Set<Integer>> locsOfUser = EBMUtil.getLocationsOfEachUser(checkins);
			Map<Integer, Set<Integer>> usersOfLoc = EBMUtil.getUsersOfEachLocation(checkins);
			
			//Calculate cooccurrences
			ArrayList<Integer> userList = new ArrayList<>(EBMUtil.getUserSet(checkins));
			Collections.sort(userList);
			
			for (Integer u : userList) { // for each user u
				coocWriter.write(u + EBMDataIO.USER_SEPARATOR);
				ArrayList<Integer> uLocs = new ArrayList<>(locsOfUser.get(u)); // get locations of u
				Collections.sort(uLocs);

				for (Integer loc : uLocs) { // for each location l of u
					IntIntPair ulp = new IntIntPair(u, loc);
					int ulCount = userLocCount.get(ulp);
					
					ArrayList<Integer> vs = new ArrayList<>(usersOfLoc.get(loc)); // get users of l
					Collections.sort(vs);

					for (Integer v : vs) { // for each user v of l
						//(u, v, l)
						//NOTE: number of co-occurrences is calculated regardless of the checkin TIME
						IntIntPair vlp = new IntIntPair(v, loc);
						int vlCount = userLocCount.get(vlp);
						
						int coocurrencesCount = Math.min(ulCount, vlCount);
						coocWriter.write(v + EBMDataIO.COUNT_SEPARATOR 
								+ loc + EBMDataIO.COUNT_SEPARATOR 
								+ coocurrencesCount + EBMDataIO.USER_SEPARATOR);
					}
				}
				
				coocWriter.println();
				coocWriter.flush();
			}
			coocWriter.close();
		} catch (Exception e) {
			LOG.error("", e);
		}
	}
}
