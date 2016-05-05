package com.duykien.usc.EBM;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.duykien.usc.EBM.calculator.CooccurenceCalculator;
import com.duykien.usc.EBM.calculator.DiversityCalculator;
import com.duykien.usc.EBM.calculator.EBMParamsCalculator;
import com.duykien.usc.EBM.calculator.FrequencyCalculator;
import com.duykien.usc.EBM.calculator.LocationEntropyCalculator;
import com.duykien.usc.EBM.calculator.PotentialPairsCalculator;
import com.duykien.usc.EBM.calculator.SocialStrengthCalculator;
import com.duykien.usc.EBM.calculator.WeightedFrequencyCalculator;
import com.duykien.usc.EBM.dataprepare.DataPreparator;
import com.duykien.usc.locationentropy.LocationEntropyMain;

/**
 * Reproduce EBM for Gowalla dataset
 * @author kiennd
 *
 */
public class EBM {
	public static final String LOG4J_PROPERTIES_FILE = "log4j.properties";
	
	
	public static final String GOWALLA_DATA_DIR = "/Users/kiennd/Downloads/location_entropy_data/";
	public static final String GOWALLA_DATA_RAW_CHECKIN_FILE = GOWALLA_DATA_DIR + "loc-gowalla_totalCheckins.txt";
	
	private static final Logger LOG = Logger.getLogger(LocationEntropyMain.class);
	

	public static void main(String[] args) {
		PropertyConfigurator.configure(LOG4J_PROPERTIES_FILE);
		System.out.println("Start EBM");

		String userIdMapFile = GOWALLA_DATA_DIR + "loc-gowalla_EBM_userIdMap.txt";
		String locationIdMapFile = GOWALLA_DATA_DIR + "loc-gowalla_EBM_locationIdMap.txt";
//		DataPreparator.createIndexMap(GOWALLA_DATA_RAW_CHECKIN_FILE, userIdMapFile, locationIdMapFile);
		
		String reindexedCheckinsFile = GOWALLA_DATA_DIR + "loc-gowalla_EBM_totalCheckins_converted_to_index";
		
//		DataPreparator.convertToIndex(GOWALLA_DATA_RAW_CHECKIN_FILE, userIdMapFile, locationIdMapFile, reindexedCheckinsFile);
		
		String checkinsWest = GOWALLA_DATA_DIR + "loc-gowalla_EBM_west.txt";
		String checkinsEast = GOWALLA_DATA_DIR + "loc-gowalla_EBM_east.txt";
		
//		System.out.println("Start dividing data");
//		DataPreparator.divideCheckinData(reindexedCheckinsFile, checkinsEast, checkinsWest);
		
		String userListFileWest = GOWALLA_DATA_DIR + "loc-gowalla_EBM_west_users.txt";
		String locationListFileWest = GOWALLA_DATA_DIR + "loc-gowalla_EBM_west_locations.txt";
		
		String userListFileEast = GOWALLA_DATA_DIR + "loc-gowalla_EBM_east_users.txt";
		String locationListFileEast = GOWALLA_DATA_DIR + "loc-gowalla_EBM_east_locations.txt";
		
//		DataPreparator.calculateUserAndLocationSet(checkinsWest, userListFileWest, locationListFileWest);		
//		DataPreparator.calculateUserAndLocationSet(checkinsEast, userListFileEast, locationListFileEast);
		
		String coocWest = GOWALLA_DATA_DIR + "loc-gowalla_EBM_west_cooccurences.txt";
		String coocEast = GOWALLA_DATA_DIR + "loc-gowalla_EBM_east_cooccurences.txt";
		
//		System.out.println("Start calculate cooccurrences");
//		CooccurenceCalculator.calculateCooccurrence(checkinsWest, coocWest);
//		CooccurenceCalculator.calculateCooccurrence(checkinsEast, coocEast);
		
		
		String freqWest = GOWALLA_DATA_DIR + "loc-gowalla_EBM_west_frequency.txt";
		String freqEast = GOWALLA_DATA_DIR + "loc-gowalla_EBM_east_frequency.txt";
		
//		System.out.println("Start calculate freq");
//		FrequencyCalculator.calculateFrequency(coocWest, freqWest);
//		FrequencyCalculator.calculateFrequency(coocEast, freqEast);
		
		String potentialsWest = GOWALLA_DATA_DIR + "loc-gowalla_EBM_west_potentials.txt";
		String potentialsEast = GOWALLA_DATA_DIR + "loc-gowalla_EBM_east_potentials.txt";
		
//		System.out.println("Start calculate potentials");
//		PotentialPairsCalculator.calculatePotentialPairs(freqWest, potentialsWest);
//		PotentialPairsCalculator.calculatePotentialPairs(freqEast, potentialsEast);
		
		String diversityWest = GOWALLA_DATA_DIR + "loc-gowalla_EBM_west_diversity.txt";
		String diversityEast = GOWALLA_DATA_DIR + "loc-gowalla_EBM_east_diversity.txt";
		
//		System.out.println("Start calculate diversity");
//		DiversityCalculator.calculateDiversity(coocWest, freqWest, potentialsWest, diversityWest);		
//		DiversityCalculator.calculateDiversity(coocEast, freqEast, potentialsEast, diversityEast);
		
		String leWest = GOWALLA_DATA_DIR + "loc-gowalla_EBM_west_locationentropy.txt";
		String leEast = GOWALLA_DATA_DIR + "loc-gowalla_EBM_east_locationentropy.txt";
		
//		System.out.println("Start calculate location entropy");
//		LocationEntropyCalculator.calculateLocationEntropy(checkinsWest, leWest);
//		LocationEntropyCalculator.calculateLocationEntropy(checkinsEast, leEast);
		
		String wfWest = GOWALLA_DATA_DIR + "loc-gowalla_EBM_west_weightedfreq.txt";
		String wfEast = GOWALLA_DATA_DIR + "loc-gowalla_EBM_east_weightedfreq.txt";
		
//		System.out.println("Start calculate weighted frequency");
//		WeightedFrequencyCalculator.calculateWeightedFrequency(coocWest, leWest, potentialsWest, wfWest);
//		WeightedFrequencyCalculator.calculateWeightedFrequency(coocEast, leEast, potentialsEast, wfEast);
		
		/*
		 * RELATIONSHIP
		 */
		
		String relationshipFile = GOWALLA_DATA_DIR + "loc-gowalla_edges.txt";
		String relationshipWestFile = GOWALLA_DATA_DIR + "loc-gowalla_EBM_west_edges.txt";
		String relationshipEastFile = GOWALLA_DATA_DIR + "loc-gowalla_EBM_east_edges.txt";
//		System.out.println("Start dividing edges");
//		DataPreparator.divideRelationshipData(relationshipFile, userListFileWest, userListFileEast, relationshipWestFile, relationshipEastFile);
		
		String socialStrengthWestFile = GOWALLA_DATA_DIR + "loc-gowalla_EBM_west_socialstrength.txt";
		String socialStrengthEastFile = GOWALLA_DATA_DIR + "loc-gowalla_EBM_east_socialstrength.txt";
		
		System.out.println("Start calculate social strength");
		SocialStrengthCalculator.calculateSocialStrength(relationshipWestFile, userListFileWest, potentialsWest, socialStrengthWestFile);
		SocialStrengthCalculator.calculateSocialStrength(relationshipEastFile, userListFileEast, potentialsEast, socialStrengthEastFile);
		
//		System.out.println("Start calculate EBM model params");
//		EBMParamsCalculator.calculateEBMModelParams(potentialsWest, diversityWest, wfWest, socialStrengthWestFile);
//		EBMParamsCalculator.calculateEBMModelParams(potentialsEast, diversityEast, wfEast, socialStrengthEastFile);
	}

}
