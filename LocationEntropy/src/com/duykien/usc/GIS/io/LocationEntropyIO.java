package com.duykien.usc.GIS.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;

import com.duykien.usc.GIS.DP.LocationEntropyInfo;

public class LocationEntropyIO {
	/**
	 * Read a location entropy file
	 * @param inputFile
	 * @return
	 */
	public static ArrayList<LocationEntropyInfo> readLocationEntropy(String inputFile) {
		try {
			ArrayList<LocationEntropyInfo> infos = new ArrayList<>();
			BufferedReader reader = new BufferedReader(new FileReader(inputFile));
			
			String line = null;
			while ((line = reader.readLine()) != null) {
				StringTokenizer tokenizer = new StringTokenizer(line, ",");
				Integer locationId = Integer.parseInt(tokenizer.nextToken());
				Integer n = Integer.parseInt(tokenizer.nextToken());
				Double entropy = Double.parseDouble(tokenizer.nextToken());
				Double privateEntropy = entropy;
				if (tokenizer.hasMoreTokens())
					privateEntropy = Double.parseDouble(tokenizer.nextToken());
				double noise = privateEntropy - entropy;
				if (tokenizer.hasMoreTokens())
					noise = Double.parseDouble(tokenizer.nextToken());
				
				LocationEntropyInfo info = new LocationEntropyInfo(locationId, n, entropy, privateEntropy, noise);
				
				infos.add(info);
			}
			reader.close();
			return infos;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Write a location entropy file
	 * @param inputFile
	 * @return
	 */
	public static void writeLocationEntropy(String outputFile, ArrayList<LocationEntropyInfo> data) {
		try {
			PrintWriter writer = new PrintWriter(outputFile);
			
			for (int i = 0; i < data.size(); i++) {
				LocationEntropyInfo info = data.get(i);
				writer.println(info.getLocationId() 
						+ "," + info.getNumUser() 
						+ "," + info.getEntropy()
						+ "," + info.getPrivateEntropy()
						+ "," + info.getNoise());
			}
			
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
