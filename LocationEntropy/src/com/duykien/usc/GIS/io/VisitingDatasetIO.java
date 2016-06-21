package com.duykien.usc.GIS.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class VisitingDatasetIO {

	/**
	 * Write generated data.
	 * for each line: locationId,userid1,visits1,userid3,visits2,.....
	 * @param dataset Map: location => (map: userid -> visits)
	 * @param L number of locations
	 * @param outputFile output file
	 */
	public static void writeData(Map<Integer, ArrayList<Integer>> dataset, String outputFile) {
		try {
			int L = Collections.max(dataset.keySet());
			PrintWriter writer = new PrintWriter(new FileWriter(outputFile));
			
			for (int l = 1; l <= L; l++) {
				if (dataset.containsKey(l) == false)
					continue;
				
				ArrayList<Integer> visitList = dataset.get(l);
				
				if (visitList != null) {
					writer.print("" + l);
					int i = 0;
					while (i < visitList.size()) {
						int userId = visitList.get(i);
						int count = visitList.get(i + 1);
						writer.print("," + userId + "," + count);
						i += 2;
					}
					writer.println();
				}
				
			}
			
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Read visiting data
	 * @param inputFile
	 * @return Map of : Location id -> (visitCount1, visitCount1, ...)
	 */
	public static Map<Integer, ArrayList<Integer>> readData(String inputFile) {
		try {
			Map<Integer, ArrayList<Integer>> data = new HashMap<>();
			BufferedReader reader = new BufferedReader(new FileReader(inputFile));
			
			String line = null;
			while ((line = reader.readLine()) != null) {
				//get data of a location: locationId,userid1,visits1,userid3,visits2,.....
				StringTokenizer tokenizer = new StringTokenizer(line, ",");
				int locationId = Integer.parseInt(tokenizer.nextToken());
				
				ArrayList<Integer> counts = new ArrayList<>();
				while (tokenizer.hasMoreTokens()) {
					@SuppressWarnings("unused")
					Integer userid = Integer.parseInt(tokenizer.nextToken());
					Integer count = Integer.parseInt(tokenizer.nextToken());
					
					counts.add(count);
				}
				
				data.put(locationId, counts);
			}
			
			reader.close();
			
			return data;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Read visiting data
	 * @param inputFile
	 * @return Map of : Location id -> (user1, visitCount1, user2, visitCount1, ...)
	 */
	public static Map<Integer, ArrayList<Integer>> readDataFull(String inputFile, int maxNumOfLocationsPerUser) {
		try {
			Map<Integer, Integer> userNumLocCount = new HashMap<>();
			Map<Integer, ArrayList<Integer>> data = new HashMap<>();
			BufferedReader reader = new BufferedReader(new FileReader(inputFile));
			
			String line = null;
			while ((line = reader.readLine()) != null) {
				//get data of a location: locationId,userid1,visits1,userid3,visits2,.....
				StringTokenizer tokenizer = new StringTokenizer(line, ",");
				int locationId = Integer.parseInt(tokenizer.nextToken());
				
				ArrayList<Integer> counts = new ArrayList<>();
				while (tokenizer.hasMoreTokens()) {
					Integer userid = Integer.parseInt(tokenizer.nextToken());
					Integer count = Integer.parseInt(tokenizer.nextToken());
					
					//check if this user visited enough locations
					Integer numLocs = userNumLocCount.get(userid);
					if (numLocs == null) {
						numLocs = 0;
					}
					
					if (numLocs < maxNumOfLocationsPerUser) {
						//not enough -> can visit
						counts.add(userid);
						counts.add(count);
						numLocs++;
					}
					userNumLocCount.put(userid, numLocs);
				}
				
				data.put(locationId, counts);
			}
			
			reader.close();
			
			return data;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
