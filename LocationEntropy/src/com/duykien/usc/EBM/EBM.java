package com.duykien.usc.EBM;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.duykien.usc.EBM.calculator.CooccurenceCalculator;
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
	public static final String GOWALLA_COOCCURENCES_FILE = GOWALLA_DATA_DIR + "loc-gowalla_cooccurences";
	
	private static final Logger LOG = Logger.getLogger(LocationEntropyMain.class);
	

	public static void main(String[] args) {
		PropertyConfigurator.configure(LOG4J_PROPERTIES_FILE);

//		DataPreparator.convertToIndex(GOWALLA_DATA_FILE, GOWALLA_DATA_FILE_CONVERTED_TO_INDEX);
//		DataPreparator.divideData(GOWALLA_DATA_FILE_CONVERTED_TO_INDEX, "");
		CooccurenceCalculator.calculateCooccurrence(GOWALLA_DATA_FILE_CONVERTED_TO_INDEX, GOWALLA_COOCCURENCES_FILE);
	}

}
