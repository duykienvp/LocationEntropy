package com.duykien.usc.GIS;

import java.text.DecimalFormat;

import com.duykien.usc.GIS.DP.PertubationMethodFactory.NoisePertubationMethod;


public class Constants {
	public static final String DATA_GENERATOR_OUTPUT_DIR = "/Users/kiennd/Downloads/location_entropy_data/" 
			+ "synthetic_data/"
			+ "synthetic_data_L10000_N100000_M100_maxC1000_ze1.0/"
			+ "synthetic_data_L10000_N100000_M5_maxC1000_ze1.0/";
	public static final String DATASET_PREFIX = "synthetic_data";
	public static final int L = 10000;		//number of locations
	public static final int N = 100000;	//number of users
	public static final int MAX_M = 100; 	// //maximum of maximum number of locations of a user (used for generate data)
	public static final int M = 5;			//maximum of maximum number of locations of a user (used for limiting)
	public static final int MAX_C = 1000;	//maximum number of visits of a user to a location (used for generate data)
	public static final double ZIPF_EXPONENT = 1;		// Zipf exponent
	public static final DecimalFormat DOUBLE_FORMAT = new DecimalFormat("0.0");	//format for double output to filename
	public static final int C = 5; 		//maximum number of locations of a user (used for limiting)
	public static final double DP_EPSILON = 1;	//epsilon in Differential privacy
	public static final double DP_DELTA = 1e-8;		//delta in Differential privacy
	public static final double MIN_SENSITIVITY = 1e-3;	//minimum sensitivity (to stop calculating)
//	public static final String DP_EPSILON_STR = "2.5";
	public static final int K_CROWD = 50;
	public static final String DP_ALL_EPSILON_STR = "ALL";
	public static final String DP_DELTA_STR = "1e-8";
	public static final String DP_MIN_SENSITIVITY_STR = "1e-3";
	public static final boolean USE_M = false;		//whether or not we use M in adding noise
	public static final double BUCKET_SIZE = 0.1;
	public static final int START_C = 1;
	public static final int END_C = 51;
	public static final NoisePertubationMethod DP_NOISE_PERTURBATION_METHOD = NoisePertubationMethod.LIMIT_SS;
}
