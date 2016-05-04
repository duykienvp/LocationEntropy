package com.duykien.usc.EBM.dataIO;

import java.io.PrintWriter;
import java.util.Map;

import org.apache.log4j.Logger;

public class CooccurenceIO {
	public static final String SEPARATOR = " ";
	
	private static final Logger LOG = Logger.getLogger(CooccurenceIO.class);
	
	public static void writeCooccurence(Map<Integer, Map<Integer, Integer>> data, String outputFile, int minCooccurences) {
		try {
			PrintWriter writer = new PrintWriter(outputFile);
			int countUser = 0;
			for (Integer u : data.keySet()) {
				writer.write("" + u);
				Map<Integer, Integer> cooccurences = data.get(u);
				
				for (Integer v : cooccurences.keySet()) {
					int c = cooccurences.get(v);
					if (minCooccurences <= c) {
						writer.write(SEPARATOR + v + SEPARATOR + c);
					}
				}
				
				writer.println();
				writer.flush();
				
				countUser++;
				if (countUser % 100 == 0) {
					LOG.info("Writen " + countUser);
				}
			}
			
			writer.close();
		} catch (Exception e) {
			LOG.error("Error writing cooccurences to " + outputFile, e);
		}
	}
}
