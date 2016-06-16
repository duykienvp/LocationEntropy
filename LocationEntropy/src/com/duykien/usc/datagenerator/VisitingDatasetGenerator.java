package com.duykien.usc.datagenerator;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.math3.distribution.ZipfDistribution;

import com.duykien.usc.locationentropy.calculator.EntropyCalculator;

public class VisitingDatasetGenerator {
	
	/**
	 * Generate locatin visiting dataset:
	 * Steps:
	 *  - zipf exponent ze
	 * 	- For each user u:
	 * 		+ m = number of locations u visits = Zipf(M, ze) (M: number of elements, ze: exponent)
	 * 		+ Run m times:
	 * 			++ l = a location that u has not visited = Zipf(L, ze)  and not visited
	 * 			++ c = number of visits of u to l = Zipf(maxC, ze) (M: number of elements, ze: exponent)
	 *  - calculate and write entropy for limiting factors C1, C2
	 * @param L number of locations
	 * @param N number of users
	 * @param M maximum number of locations that a user can visit
	 * @param C maximum number of visits that a user can contribute to a location
	 * @param ze zipf exponent
	 * @param outputFile output file
	 */
	public static void generate(int L, int N, int M, int maxC, int C1, int C2, double ze, String outputFile1, String outputFile2) {
		//Map: location => [visit1, visit2, ...]
//		Random rand = new Random();
		ZipfDistribution zipfM = new ZipfDistribution(M, ze);
		ZipfDistribution zipfC = new ZipfDistribution(maxC, ze);
		ZipfDistribution zipfL = new ZipfDistribution(L, ze);
		Map<Integer, ArrayList<Integer>> dataset = new HashMap<>();
		for (int u = 0; u < N; u++) {
			//a user u
			
			Set<Integer> visited = new HashSet<>();
			//number of locations that u visits = Zipf(M, 1)
//			int m = rand.nextInt(M) + 1;
			int m = zipfM.sample();
			for (int i = 0; i < m; i++) {
				//generate a location and not visited
				if (L <= visited.size())
					break;
				while (true) {
					 //location if from 1 -> L
//					int l = rand.nextInt(L) + 1;
					int l = zipfL.sample();
					if (visited.contains(l) == false) {
						visited.add(l); //visited
						//number of visits of u to l
//						int c = rand.nextInt(C) + 1; 
						int c = zipfC.sample();
						addData(dataset, l, c);
						break;
					}
				}
			}
		}
		
		calculateLocationEntropyAndWriteData(dataset, L, C2, outputFile2);
		calculateLocationEntropyAndWriteData(dataset, L, C1, outputFile1);
	}
	
	/**
	 * Calculate and write entropy for limiting factors C
	 * @param dataset Map: location => [visit1, visit2, ...]
	 * @param L number of locations
	 * @param outputFile output file
	 */
	public static void calculateLocationEntropyAndWriteData(Map<Integer, ArrayList<Integer>> dataset, int L, int C, String outputFile) {
		try {
			PrintWriter writer = new PrintWriter(new FileWriter(outputFile));
			
			for (int l = 1; l <= L; l++) {
				if (dataset.containsKey(l) == false)
					continue;
				ArrayList<Integer> counts = new ArrayList<>(dataset.get(l));
				int numUser = 0;
				double entropy = 0.0;
				if (counts != null) {
					for (int i = 0; i < counts.size(); i++) {
						counts.set(i, Math.max(C, counts.get(i)));
					}
					numUser = counts.size();
					entropy = EntropyCalculator.calShannonEntropy(EntropyCalculator.toDoubleArray(counts.toArray()));
				}
				writer.println(l + "," + numUser + "," + entropy);
			}
			
			writer.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Add to the dataset: a user visits location l in total c times
	 * @param dataset
	 * @param l
	 * @param u
	 * @param c
	 */
	public static void addData(Map<Integer, ArrayList<Integer>> dataset, int l, int c) {
		ArrayList<Integer> lData = dataset.get(l);
		if (lData == null) {
			lData = new ArrayList<>();
		}
//		lData.add(u);
		lData.add(c);
		
		dataset.put(l, lData);
	}

	public static void main(String[] args) {
		int L = 10000;
		int N = 1000000;
		int M = 10;
		int maxC = 100;
		int C1 = 10;
		int C2 = 20;
		double ze = 1;
		DecimalFormat df = new DecimalFormat("0.0"); 
		String outputDir = "/Users/kiennd/Downloads/location_entropy_data/";
		String outputFile1 = outputDir + "synthetic_data_L" + L + "_N" + N + "_M" + M + "_maxC" + maxC + "_C" + C1 + "_ze" + df.format(ze) + ".csv";
		String outputFile2 = outputDir + "synthetic_data_L" + L + "_N" + N + "_M" + M + "_maxC" + maxC + "_C" + C2 + "_ze" + df.format(ze) + ".csv";
		System.out.println("Start generating");
		generate(L, N, M, maxC, C1, C2, ze, outputFile1, outputFile2);
		System.out.println("Finished generating");
	}

}
