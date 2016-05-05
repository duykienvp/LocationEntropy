package com.duykien.usc.EBM.dataIO;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.duykien.usc.locationentropy.locationdata.IntIntIntIntMap;
import com.duykien.usc.locationentropy.locationdata.IntIntIntMap;

public class EBMDataIO {
	public static final String DEFAULT_TXT_FILE_SUFFIX = ".txt";
	public static final String USER_SEPARATOR = "\t";
	public static final String COUNT_SEPARATOR = ":";
	
	private static final Logger LOG = Logger.getLogger(EBMDataIO.class);
	
	public static IntIntIntIntMap readCooccurrences(String inputFile) {
		IntIntIntIntMap data = new IntIntIntIntMap();
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(inputFile));
			String line = null;
			while ((line = reader.readLine()) != null) {
				StringTokenizer userTokenizer = new StringTokenizer(line, USER_SEPARATOR);
				if (userTokenizer.hasMoreTokens() == false)
					continue;
				
				int u = Integer.parseInt(userTokenizer.nextToken());
				while (userTokenizer.hasMoreElements()) {
					String countStr = userTokenizer.nextToken();
					if (countStr.length() < 1)
						continue;
					StringTokenizer countTokenizer = new StringTokenizer(countStr, COUNT_SEPARATOR);
					int v = Integer.parseInt(countTokenizer.nextToken());
					int l = Integer.parseInt(countTokenizer.nextToken());
					int c = Integer.parseInt(countTokenizer.nextToken());
					
					data.addEntry(u, v, l, c);
				}
			}
			reader.close();
		} catch (Exception e) {
			LOG.error("Error reading cooccurrences from " + inputFile, e);
			data = new IntIntIntIntMap();
		}
		
		return data;
	}
	
	/**
	 * Parse a co-occurrence line: u v:l:c ....
	 * 
	 * @param line
	 * @return Map contains data of the line or empty map if error occurred
	 */
	public static IntIntIntIntMap parseCooccurenceLine(String line) {
		IntIntIntIntMap data = new IntIntIntIntMap();
		try {
			StringTokenizer userTokenizer = new StringTokenizer(line, USER_SEPARATOR);
			if (userTokenizer.hasMoreTokens() == false)
				return data;
			
			int u = Integer.parseInt(userTokenizer.nextToken());
			while (userTokenizer.hasMoreElements()) {
				String countStr = userTokenizer.nextToken();
				if (countStr.length() < 1)
					continue;
				StringTokenizer countTokenizer = new StringTokenizer(countStr, COUNT_SEPARATOR);
				int v = Integer.parseInt(countTokenizer.nextToken());
				int l = Integer.parseInt(countTokenizer.nextToken());
				int c = Integer.parseInt(countTokenizer.nextToken());
				
				data.addEntry(u, v, l, c);
			} 
			
			return data;
		} catch (Exception e) {
			return new IntIntIntIntMap();
		}
	}
	
	public static IntIntIntMap readIntIntIntFile(String inputFile) {
		IntIntIntMap data = new IntIntIntMap();
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(inputFile));
			String line = null;
			while ((line = reader.readLine()) != null) {
				StringTokenizer tokenizer = new StringTokenizer(line, USER_SEPARATOR);
				int u = Integer.parseInt(tokenizer.nextToken());
				int v = Integer.parseInt(tokenizer.nextToken());
				int c = Integer.parseInt(tokenizer.nextToken());
				
				data.add(u, v, c);
			}
			reader.close();
			
			return data;
		} catch (Exception e) {
			LOG.error("Error readIntIntIntFile", e);
			return new IntIntIntMap();
		}
	}
}
