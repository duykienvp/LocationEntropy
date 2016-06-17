package com.duykien.usc.GIS;

import java.text.DecimalFormat;

public class Constants {
	public static final String DATA_GENERATOR_OUTPUT_DIR = "/Users/kiennd/Downloads/location_entropy_data/";
	public static final int L = 10000;		//number of locations
	public static final int N = 1000000;	//number of users
	public static final int M = 10;			//maximum of maximum number of locations of a user
	public static final int MAX_C = 1000;	//maximum number of visits of a user to a location
	public static final double ZIPF_EXPONENT = 1;		// Zipf exponent
	public static final DecimalFormat DOUBLE_FORMAT = new DecimalFormat("0.0");	//format for double output to filename
	public static final int C = 5; 		//maximum number of locations of a user
	public static final double DP_EPSILON = Math.log(10);	//epsilon in Differential privacy
	public static final double DP_DELTA = 1e-7;		//delta in Differential privacy
	public static final double MIN_SENSITIVITY = 1e-3;	//minimum sensitivity (to stop calculating)
	public static final boolean USE_M = false;		//whether or not we use M in adding noise
	public static final double BUCKET_SIZE = 0.1;
}
