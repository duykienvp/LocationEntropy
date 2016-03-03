package com.duykien.usc.locationentropy.grid;

public class GridUtilityFactory {
	public static final double LA_MAX_LATITUDE = 34.342324;
	public static final double LA_MIN_LATITUDE = 33.699675;
	public static final double LA_MAX_LONGITUDE = -118.144458;
	public static final double LA_MIN_LONGITUDE = -118.684687;

	public static final double LA_LATITUDE_50_METERS = 0.00045;
	public static final double LA_LONGIITUDE_50_METERS = 0.00055;
	
	public static final double GLOBAL_MAX_LATITUDE = 90;
	public static final double GLOBAL_MIN_LATITUDE = -90;
	public static final double GLOBAL_MAX_LONGITUDE = 180;
	public static final double GLOBAL_MIN_LONGITUDE = -180;

	public static final double GLOBAL_LATITUDE_50_METERS = 0.00045;
	public static final double GLOBAL_LONGIITUDE_50_METERS = 0.00055;
	
	public enum Area {
		GLOBAL, LOS_ANLEGES
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
			
		case LOS_ANLEGES:
			return new GridUtility(LA_MAX_LATITUDE, LA_MIN_LATITUDE, LA_MAX_LONGITUDE, LA_MIN_LONGITUDE, LA_LATITUDE_50_METERS, LA_LONGIITUDE_50_METERS);

		default:
			return null;
		}
	}
}
