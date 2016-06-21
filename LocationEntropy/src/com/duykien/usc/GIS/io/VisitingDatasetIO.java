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
	public static void writeData(Map<Integer, Map<Integer, Integer>> dataset, int L, String outputFile) {
		try {
			PrintWriter writer = new PrintWriter(new FileWriter(outputFile));
			
			for (int l = 1; l <= L; l++) {
				if (dataset.containsKey(l) == false)
					continue;
				
				Map<Integer, Integer> visitMap = new HashMap<>(dataset.get(l));
				
				if (visitMap != null) {
					ArrayList<Integer> users = new ArrayList<>(visitMap.keySet());
					Collections.sort(users);
					
					writer.print("" + l);
					for (int i = 0; i < users.size(); i++) {
						int userId = users.get(i);
						writer.print("," + userId + "," + visitMap.get(userId));
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
					int userid = Integer.parseInt(tokenizer.nextToken());
					
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
}
