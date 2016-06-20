package com.duykien.usc.locationentropy.grid;

public class GridUtilityFactory {
	public static final double NY_MAX_LATITUDE = 40.7965600333;
	public static final double NY_MIN_LATITUDE = 40.6991117951;
	public static final double NY_MAX_LONGITUDE = -73.9228093667;
	public static final double NY_MIN_LONGITUDE = -74.0270912647;
	
	public static final double LA_MAX_LATITUDE = 34.342324;
	public static final double LA_MIN_LATITUDE = 33.699675;
	public static final double LA_MAX_LONGITUDE = -118.144458;
	public static final double LA_MIN_LONGITUDE = -118.684687;

	public static final double LATITUDE_50_METERS = 0.00045;
	public static final double LONGIITUDE_50_METERS = 0.00055;
	
	public static final double LATITUDE_100_METERS = LATITUDE_50_METERS * 2;
	public static final double LONGIITUDE_100_METERS = LONGIITUDE_50_METERS * 2;
	
	public static final double LATITUDE_500_METERS = LATITUDE_50_METERS * 10;
	public static final double LONGIITUDE_500_METERS = LONGIITUDE_50_METERS * 10;
	
	public static final double LATITUDE_1000_METERS = LATITUDE_100_METERS * 10;
	public static final double LONGIITUDE_1000_METERS = LATITUDE_100_METERS * 10;
	
	public static final double GLOBAL_MAX_LATITUDE = 90;
	public static final double GLOBAL_MIN_LATITUDE = -90;
	public static final double GLOBAL_MAX_LONGITUDE = 180;
	public static final double GLOBAL_MIN_LONGITUDE = -180;

	public static final double GLOBAL_LATITUDE_50_METERS = 0.00045;
	public static final double GLOBAL_LONGIITUDE_50_METERS = 0.00055;
	
	public enum Area {
		GLOBAL, 
		LOS_ANLEGES_50, LOS_ANLEGES_100, LOS_ANLEGES_500, LOS_ANLEGES_1000,
		NEWYORK_100
	}
	
	/**
	 * Create a GridUtility for a specific area
	 * @param area 
	 * @return the GridUtility or null if error occurred.
	 */
	public static GridUtility createGridUtility(Area area) {
		switch (area) {
		case GLOBAL:
			return new GridUtility(GLOBAL_MAX_LATITUDE, GLOBAL_MIN_LATITUDE, GLOBAL_MAX_LONGITUDE, GLOBAL_MIN_LONGITUDE, GLOBAL_LATITUDE_50_METERS, GLOBAL_LONGIITUDE_50_METERS);
			
		case LOS_ANLEGES_50:
			return new GridUtility(LA_MAX_LATITUDE, LA_MIN_LATITUDE, LA_MAX_LONGITUDE, LA_MIN_LONGITUDE, LATITUDE_50_METERS, LONGIITUDE_50_METERS);
			
		case LOS_ANLEGES_100:
			return new GridUtility(LA_MAX_LATITUDE, LA_MIN_LATITUDE, LA_MAX_LONGITUDE, LA_MIN_LONGITUDE, LATITUDE_100_METERS, LONGIITUDE_100_METERS);

		case LOS_ANLEGES_500:
			return new GridUtility(LA_MAX_LATITUDE, LA_MIN_LATITUDE, LA_MAX_LONGITUDE, LA_MIN_LONGITUDE, LATITUDE_500_METERS, LONGIITUDE_500_METERS);
			
		case LOS_ANLEGES_1000:
			return new GridUtility(LA_MAX_LATITUDE, LA_MIN_LATITUDE, LA_MAX_LONGITUDE, LA_MIN_LONGITUDE, LATITUDE_1000_METERS, LONGIITUDE_1000_METERS);

		case NEWYORK_100:
			return new GridUtility(NY_MAX_LATITUDE, NY_MIN_LATITUDE, NY_MAX_LONGITUDE, NY_MIN_LONGITUDE, LATITUDE_100_METERS, LONGIITUDE_100_METERS);

		default:
			return null;
		}
	}
}
