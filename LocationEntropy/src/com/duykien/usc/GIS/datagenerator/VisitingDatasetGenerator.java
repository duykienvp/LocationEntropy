package com.duykien.usc.GIS.datagenerator;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.distribution.ZipfDistribution;

import com.duykien.usc.GIS.Constants;
import com.duykien.usc.GIS.FileNameUtil;
import com.duykien.usc.GIS.io.VisitingDatasetIO;

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
	 * Output file format:
	 * for each line: locationId,userid1,visits1,userid3,visits2,.....
	 * @param L number of locations
	 * @param N number of users
	 * @param maxM maximum number of locations that a user can visit
	 * @param C maximum number of visits that a user can contribute to a location
	 * @param ze zipf exponent
	 * @param outputFile output file
	 */
	public static void generate(int L, 
			int N, 
			int maxM, 
			int maxC, 
			double ze, 
			String outputFile) {
		//Map: location => (map: userid -> visits)
		ZipfDistribution zipfM = new ZipfDistribution(maxM, ze);
		ZipfDistribution zipfC = new ZipfDistribution(maxC, ze);
		ZipfDistribution zipfL = new ZipfDistribution(L, ze);
		Map<Integer, ArrayList<Integer>> dataset = new HashMap<>();
		for (int u = 0; u < N; u++) {
			//a user u
			if (u % 10000 == 0)
				System.out.println("Generated " + u + " users");
			
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
						putData(dataset, l, u, c);
						break;
					}
				}
			}
		}
		
		VisitingDatasetIO.writeData(dataset, outputFile);
	}
	
	/**
	 * Put to the dataset: a user u visits location l in c times
	 * @param dataset Map: location => [user1, visitCount1, user2, visitCount2, ...]
	 * @param l location id
	 * @param u user id
	 * @param c number of visits
	 */
	public static void putData(Map<Integer, ArrayList<Integer>> dataset, int l, int u, int c) {
		ArrayList<Integer> lData = dataset.get(l);
		if (lData == null) {
			lData = new ArrayList<>();
		}
		
		lData.add(u);
		lData.add(c);
		
		dataset.put(l, lData);
	}
	
	/**
	 * Add to the dataset: a user u visits location l in c times more
	 * @param dataset Map: location => (map: userid -> visits)
	 * @param l location id
	 * @param u user id
	 * @param c number of visits
	 */
	public static void addData(Map<Integer, Map<Integer, Integer>> dataset, int l, int u, int c) {
		Map<Integer, Integer> lData = dataset.get(l);
		if (lData == null) {
			lData = new HashMap<>();
		}
		Integer count = lData.get(u);
		if (count == null) {
			count = 0;
		}
		count += c;
		
		lData.put(u, count);
		
		dataset.put(l, lData);
	}
	
	public static void limitMaximumNumOfLocationsPerUser(String inputFile, int maxNumOfLocationsPerUser, String outputFile) {
		Map<Integer, ArrayList<Integer>> data = VisitingDatasetIO.readDataFull(inputFile, maxNumOfLocationsPerUser);
		VisitingDatasetIO.writeData(data, outputFile);
	}

	public static void main(String[] args) {
		String prefix = Constants.DATASET_PREFIX;
		int L = Constants.L;
		int N = Constants.N;
		int maxM = Constants.MAX_M;
		int maxC = Constants.MAX_C;
		double ze = Constants.ZIPF_EXPONENT;
		DecimalFormat df = Constants.DOUBLE_FORMAT; 
		
		String dataGenerationOutputDir = Constants.DATA_GENERATOR_OUTPUT_DIR;
		String dataGenerationOutputFile = FileNameUtil.getDataGenerationOutputFile(prefix, L, N, maxM, maxC, ze, df, dataGenerationOutputDir);
		
		System.out.println("Start generating");
//		generate(L, N, maxM, maxC, ze, dataGenerationOutputFile);
		
		int M = 20;
		String limitedFile = FileNameUtil.getDataGenerationOutputFile(prefix, L, N, M, maxC, ze, df, dataGenerationOutputDir);
		limitMaximumNumOfLocationsPerUser(dataGenerationOutputFile, M, limitedFile);
		System.out.println("Finished generating");
	}

}
