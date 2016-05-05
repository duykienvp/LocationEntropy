package com.duykien.usc.EBM.dataIO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.duykien.usc.locationentropy.locationdata.IntIntDoubleMap;
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
	
	public static IntIntDoubleMap readIntIntDoubleFile(String inputFile) {
		IntIntDoubleMap data = new IntIntDoubleMap();
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(inputFile));
			String line = null;
			while ((line = reader.readLine()) != null) {
				StringTokenizer tokenizer = new StringTokenizer(line, USER_SEPARATOR);
				int u = Integer.parseInt(tokenizer.nextToken());
				int v = Integer.parseInt(tokenizer.nextToken());
				double c = Double.parseDouble(tokenizer.nextToken());
				
				data.add(u, v, c);
			}
			reader.close();
			
			return data;
		} catch (Exception e) {
			LOG.error("Error readIntIntDoubleFile: " + inputFile, e);
			return new IntIntDoubleMap();
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
	
	public static IntIntIntMap parseIntIntIntLine(String line) {
		IntIntIntMap data = new IntIntIntMap();
		
		try {
			StringTokenizer tokenizer = new StringTokenizer(line, USER_SEPARATOR);
			int u = Integer.parseInt(tokenizer.nextToken());
			while (tokenizer.hasMoreTokens()) {
				StringTokenizer freqTokenizer = new StringTokenizer(tokenizer.nextToken(), COUNT_SEPARATOR);
				int v = Integer.parseInt(freqTokenizer.nextToken());
				int c = Integer.parseInt(freqTokenizer.nextToken());
				data.add(u, v, c);
			}
			
			return data;
		} catch (Exception e) {
			LOG.error("Error readIntIntIntFile", e);
			return new IntIntIntMap();
		}
	} 
	
	public static void writeLocationEntropy(Map<Integer, Double> le, String outputFile) {
		try {	
			PrintWriter writer = new PrintWriter(outputFile);
			ArrayList<Integer> locs = new ArrayList<>(le.keySet());
			Collections.sort(locs);
			for (Integer l: locs) {
				writer.println(l + USER_SEPARATOR + le.get(l));
			}
			
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Map<Integer, Double> readLocationEntropy(String inputFile) {
		try {	
			Map<Integer, Double> le = new HashMap<>();
			
			BufferedReader reader = new BufferedReader(new FileReader(inputFile));
			
			String line = null;
			while ((line = reader.readLine()) != null) {
				StringTokenizer tokenizer = new StringTokenizer(line, USER_SEPARATOR);
				Integer l = Integer.parseInt(tokenizer.nextToken());
				Double hl = Double.parseDouble(tokenizer.nextToken());
				le.put(l, hl);
			}
			
			reader.close();
			return le;
		} catch (Exception e) {
			e.printStackTrace();
			return new HashMap<>();
		}
	}
	
	public static Map<Integer, Set<Integer>> readPotentials(String inputFile) {
		try {	
			Map<Integer, Set<Integer>> data = new HashMap<>();
			
			BufferedReader reader = new BufferedReader(new FileReader(inputFile));
			
			String line = null;
			while ((line = reader.readLine()) != null) {
				StringTokenizer tokenizer = new StringTokenizer(line, USER_SEPARATOR);
				Integer u = Integer.parseInt(tokenizer.nextToken());
				Set<Integer> vs = new HashSet<>();
				while (tokenizer.hasMoreTokens()) {
					vs.add(Integer.parseInt(tokenizer.nextToken()));
				}
				
				data.put(u, vs);
			}
			
			reader.close();
			return data;
		} catch (Exception e) {
			e.printStackTrace();
			return new HashMap<>();
		}
	}
	
	public static void writeIdToIndex(ArrayList<Integer> list, String outputFile) {
		try {	
			PrintWriter writer = new PrintWriter(outputFile);
			for (int i = 0; i < list.size(); i++) {
				writer.println(list.get(i) + USER_SEPARATOR + i);
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Map<Integer, Integer> readIdToIndexMap(String inputFile) {
		try {	
			Map<Integer, Integer> data = new HashMap<>();
			
			BufferedReader reader = new BufferedReader(new FileReader(inputFile));
			
			String line = null;
			while ((line = reader.readLine()) != null) {
				StringTokenizer tokenizer = new StringTokenizer(line, USER_SEPARATOR);
				Integer id = Integer.parseInt(tokenizer.nextToken());
				Integer index = Integer.parseInt(tokenizer.nextToken());
				
				data.put(id, index);
			}
			
			reader.close();
			return data;
		} catch (Exception e) {
			e.printStackTrace();
			return new HashMap<>();
		}
	}
	
	public static void writeList(ArrayList<Integer> list, String outputFile) {
		try {	
			PrintWriter writer = new PrintWriter(outputFile);
			for (int i = 0; i < list.size(); i++) {
				writer.println(list.get(i));
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static ArrayList<Integer> readList(String inputFile) {
		try {	
			ArrayList<Integer> res = new ArrayList<>();
			Scanner scanner = new Scanner(new File(inputFile));
			while (scanner.hasNextInt()) {
				res.add(scanner.nextInt());
			}
			scanner.close();
			return res;
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	
	
	
	public static Map<Integer, Set<Integer>> readRelationships(String inputFile) {
		try {	
			Map<Integer, Set<Integer>> data = new HashMap<>();
			
			BufferedReader reader = new BufferedReader(new FileReader(inputFile));
			
			String line = null;
			while ((line = reader.readLine()) != null) {
				StringTokenizer tokenizer = new StringTokenizer(line, USER_SEPARATOR);
				Integer u = Integer.parseInt(tokenizer.nextToken());
				Integer v = Integer.parseInt(tokenizer.nextToken());
				Set<Integer> vs = data.get(u);
				if (vs == null) {
					vs = new HashSet<>();
				}
				vs.add(v);
				
				data.put(u, vs);
			}
			
			reader.close();
			return data;
		} catch (Exception e) {
			e.printStackTrace();
			return new HashMap<>();
		}
	}
}
