package com.duykien.usc.EBM;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.duykien.usc.EBM.calculator.CooccurenceCalculator;
import com.duykien.usc.EBM.calculator.DiversityCalculator;
import com.duykien.usc.EBM.calculator.FrequencyCalculator;
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
	public static final String GOWALLA_DATA_RAW_FILE = GOWALLA_DATA_DIR + "loc-gowalla_totalCheckins.txt";
	public static final String GOWALLA_DATA_FILE_CONVERTED_TO_INDEX = GOWALLA_DATA_DIR + "loc-gowalla_totalCheckins_converted_to_index.txt";
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
	}

}
