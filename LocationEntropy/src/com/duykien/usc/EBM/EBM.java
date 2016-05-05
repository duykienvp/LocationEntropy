package com.duykien.usc.EBM;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.duykien.usc.EBM.calculator.CooccurenceCalculator;
import com.duykien.usc.EBM.calculator.DiversityCalculator;
import com.duykien.usc.EBM.calculator.FrequencyCalculator;
import com.duykien.usc.EBM.calculator.LocationEntropyCalculator;
import com.duykien.usc.EBM.calculator.PotentialPairsCalculator;
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
	public static final String GOWALLA_DATA_CHECKIN_CONVERTED_TO_INDEX_FILE = GOWALLA_DATA_DIR + "loc-gowalla_totalCheckins_converted_to_index.txt";
	public static final String GOWALLA_COOCCURENCES_FILE = GOWALLA_DATA_DIR + "loc-gowalla_EBM";
	
	private static final Logger LOG = Logger.getLogger(LocationEntropyMain.class);
	

	public static void main(String[] args) {
		PropertyConfigurator.configure(LOG4J_PROPERTIES_FILE);
		System.out.println("Start EBM");

//		DataPreparator.convertToIndex(GOWALLA_DATA_FILE, GOWALLA_DATA_FILE_CONVERTED_TO_INDEX);
//		DataPreparator.divideData(GOWALLA_DATA_FILE_CONVERTED_TO_INDEX, "");
//		System.out.println("Start calculate cooccurrences");
//		CooccurenceCalculator.calculateCooccurrence(GOWALLA_DATA_FILE_CONVERTED_TO_INDEX, GOWALLA_COOCCURENCES_FILE);
		
		String checkinsWest = GOWALLA_DATA_DIR + "loc-gowalla_EBM_west.txt";
		String checkinsEast = GOWALLA_DATA_DIR + "loc-gowalla_EBM_east.txt";
		
		String coocWest = GOWALLA_DATA_DIR + "loc-gowalla_EBM_west_cooccurences.txt";
		String freqWest = GOWALLA_DATA_DIR + "loc-gowalla_EBM_west_frequency.txt";
		
//		System.out.println("Start calculate freq west");
//		FrequencyCalculator.calculateFrequency(coocWest, freqWest);
		
		String coocEast = GOWALLA_DATA_DIR + "loc-gowalla_EBM_east_cooccurences.txt";
		String freqEast = GOWALLA_DATA_DIR + "loc-gowalla_EBM_east_frequency.txt";
		
//		System.out.println("Start calculate freq east");
//		FrequencyCalculator.calculateFrequency(coocEast, freqEast);
		
		
		String diversityWest = GOWALLA_DATA_DIR + "loc-gowalla_EBM_west_diversity.txt";
		
//		System.out.println("Start calculate diversity west");
//		DiversityCalculator.calculateDiversity(coocWest, freqWest, diversityWest);
		
		String diversityEast = GOWALLA_DATA_DIR + "loc-gowalla_EBM_east_diversity.txt";
		
//		System.out.println("Start calculate diversity east");
//		DiversityCalculator.calculateDiversity(coocEast, freqEast, diversityEast);
		
//		System.out.println("Start dividing data");
//		DataPreparator.divideData(GOWALLA_DATA_FILE_CONVERTED_TO_INDEX, checkinsEast, checkinsWest);
		
		String leWest = GOWALLA_DATA_DIR + "loc-gowalla_EBM_west_locationentropy.txt";
		String leEast = GOWALLA_DATA_DIR + "loc-gowalla_EBM_east_locationentropy.txt";
		
//		System.out.println("Start calculate location entropy west");
//		LocationEntropyCalculator.calculateLocationEntropy(checkinsWest, leWest);
		
//		System.out.println("Start calculate location entropy east");
//		LocationEntropyCalculator.calculateLocationEntropy(checkinsEast, leEast);
		
		String potentialsWest = GOWALLA_DATA_DIR + "loc-gowalla_EBM_west_potentials.txt";
		String potentialsEast = GOWALLA_DATA_DIR + "loc-gowalla_EBM_east_potentials.txt";
		
//		System.out.println("Start calculate potentials west");
//		PotentialPairsCalculator.calculatePotentialPairs(freqWest, potentialsWest);
		
//		System.out.println("Start calculate potentials east");
//		PotentialPairsCalculator.calculatePotentialPairs(freqEast, potentialsEast);
		
		String wfWest = GOWALLA_DATA_DIR + "loc-gowalla_EBM_west_weightedfreq.txt";
		String wfEast = GOWALLA_DATA_DIR + "loc-gowalla_EBM_east_weightedfreq.txt";
		
//		System.out.println("Start calculate weighted frequence west");
//		WeightedFrequencyCalculator.calculateWeightedFrequency(coocWest, leWest, potentialsWest, wfWest);
		
//		System.out.println("Start calculate weighted frequence east");
//		WeightedFrequencyCalculator.calculateWeightedFrequency(coocEast, leEast, potentialsEast, wfEast);
		
		String userIdMapFile = GOWALLA_DATA_DIR + "loc-gowalla_EBM_userIdMap.txt";
		String locationIdMapFile = GOWALLA_DATA_DIR + "loc-gowalla_EBM_locationIdMap.txt";
//		DataPreparator.createIndexMap(GOWALLA_DATA_RAW_CHECKIN_FILE, userIdMapFile, locationIdMapFile);
		
		String userListFileWest = GOWALLA_DATA_DIR + "loc-gowalla_EBM_west_users.txt";
		String locationListFileWest = GOWALLA_DATA_DIR + "loc-gowalla_EBM_west_locations.txt";
		
//		DataPreparator.calculateUserAndLocationSet(checkinsWest, userListFileWest, locationListFileWest);
		
		String userListFileEast = GOWALLA_DATA_DIR + "loc-gowalla_EBM_east_users.txt";
		String locationListFileEast = GOWALLA_DATA_DIR + "loc-gowalla_EBM_east_locations.txt";
		
//		DataPreparator.calculateUserAndLocationSet(checkinsEast, userListFileEast, locationListFileEast);
	}

}
