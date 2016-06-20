package com.duykien.usc.GIS.gowalla;

import java.text.DecimalFormat;

import com.duykien.usc.GIS.Constants;

public class GowallaContants {
	
	public static final int GOWALLA_L = 1280955 + 1;
	public static final int GOWALLA_N = 107091 + 1;
	public static final String GOWALLA_DATA_GENERATOR_OUTPUT_DIR = Constants.DATA_GENERATOR_OUTPUT_DIR + "gowalla/";
	public static final String GOWALLA_DATASET_PREFIX = "gowalla";
	public static final String GOWALLA_DATASET_LA_PREFIX = "gowalla_LA";
	public static final String GOWALLA_DATASET_NY_PREFIX = "gowalla_NY";
	public static final int GOWALLA_M = Constants.M;			//maximum of maximum number of locations of a user
	public static final int GOWALLA_MAX_C = 100000;	//maximum number of visits of a user to a location
	public static final double GOWALLA_ZIPF_EXPONENT = 0;		// Zipf exponent
	public static final DecimalFormat DOUBLE_FORMAT = Constants.DOUBLE_FORMAT;	//format for double output to filename

	
	public static final String GOWALLA_CHECKINS_FILE = "loc-gowalla_EBM_totalCheckins_converted_to_index.txt";
}
