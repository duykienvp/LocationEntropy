package com.duykien.usc.EBM.calculator;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.duykien.usc.EBM.dataIO.EBMDataIO;

public class SocialStrengthCalculator {
	private static final Logger LOG = Logger.getLogger(WeightedFrequencyCalculator.class);
	
	public static void calculateSocialStrength(String relationshipFile, String userFile, String potentialFile, String socialStrengthFile) {
		try {
			PrintWriter writer = new PrintWriter(socialStrengthFile);
			Map<Integer, Set<Integer>> edges = EBMDataIO.readRelationships(relationshipFile);
			Set<Integer> users = new HashSet<>(EBMDataIO.readList(userFile));
			Map<Integer, Set<Integer>> potentials = EBMDataIO.readPotentials(potentialFile);
			
			ArrayList<Integer> us = new ArrayList<>(potentials.keySet());
			Collections.sort(us);
			for (Integer u : us) {
				if (users.contains(u) == false) {
					continue;
				}
				
				Set<Integer> uEdges = edges.get(u);
				if (uEdges == null) {
					continue;
				}
				
				ArrayList<Integer> vs = new ArrayList<>(potentials.get(u));
				Collections.sort(vs);
				for (Integer v : vs) {
					if (users.contains(v) == false) {
						continue;
					}
					
					Set<Integer> vEdges = edges.get(v);
					if (vEdges == null) {
						continue;
					}
					
					double strength = 0.0;
					//AA for u and v
					for (Integer k : uEdges) {
						if (vEdges.contains(k) == false) 
							continue;
						
						//k is a mutual friend of u and v
						Set<Integer> kEdges = edges.get(k);
						if (kEdges == null) {
							continue;
						}
						
						strength += 1.0 / Math.log(kEdges.size());
					}
					
					//write social strength
					writer.println(u + EBMDataIO.USER_SEPARATOR 
							+ v + EBMDataIO.USER_SEPARATOR
							+ strength);
				}
			}
			
			writer.close();
		} catch (Exception e) {
			LOG.error("Error calculation social strength from " + relationshipFile, e);
		}
	}
}
