package com.duykien.usc.locationentropy.grid;

import org.apache.log4j.Logger;

/**
 * Utility class for Los Angeles (LA) county.
 * 
 * All max, min, latitude, longitude are corresponding to LA.
 * 
 * @author kiennd
 *
 */
public class GridUtility {
	public static final double precision = 1e-12;

	private double maxLatitude = 34.342324;
	private double minLatitude = 33.699675;
	private double maxLongitude = -118.144458;
	private double minLongitude = -118.684687;

	private double latitudeCellLength = 0.00045;
	private double longitudeCellLength = 0.00055;

	private int numRows = 1;
	private int numColumns = 1;

	private static final Logger LOG = Logger.getLogger(GridUtility.class);

	public GridUtility() {
		recalculateCellNum();
	}

	public GridUtility(double maxLatitude, double minLatitude, double maxLongitude, double minLongitude,
			double latitudeCellLength, double longitudeCellLength) {
		super();
		this.maxLatitude = maxLatitude;
		this.minLatitude = minLatitude;
		this.maxLongitude = maxLongitude;
		this.minLongitude = minLongitude;
		this.latitudeCellLength = latitudeCellLength;
		this.longitudeCellLength = longitudeCellLength;

		recalculateCellNum();
	}

	public double getMaxLatitude() {
		return maxLatitude;
	}

	public void setMaxLatitude(double maxLatitude) {
		this.maxLatitude = maxLatitude;
		recalculateCellNum();
	}

	public double getMinLatitude() {
		return minLatitude;
	}

	public void setMinLatitude(double minLatitude) {
		this.minLatitude = minLatitude;
		recalculateCellNum();
	}

	public double getMinLongitude() {
		return minLongitude;
	}

	public void setMinLongitude(double minLongitude) {
		this.minLongitude = minLongitude;
		recalculateCellNum();
	}

	public double getMaxLongitude() {
		return maxLongitude;
	}

	public void setMaxLongitude(double maxLongitude) {
		this.maxLongitude = maxLongitude;
		recalculateCellNum();
	}

	public double getLatitudeCellLength() {
		return latitudeCellLength;
	}

	public void setLatitudeCellLength(double latitudeCellLength) {
		this.latitudeCellLength = latitudeCellLength;
		recalculateCellNum();
	}

	public double getLongitudeCellLength() {
		return longitudeCellLength;
	}

	public void setLongitudeCellLength(double longitudeCellLength) {
		this.longitudeCellLength = longitudeCellLength;
		recalculateCellNum();
	}

	public int getNumRows() {
		return numRows;
	}

	public int getNumColumns() {
		return numColumns;
	}

	/**
	 * Recalculate numbers of cells based on current values of latitudes and
	 * longitudes. Set to number of cells to 1 if error occurred
	 */
	public void recalculateCellNum() {
		numColumns = 1;
		numRows = 1;
		try {
			if ((precision < latitudeCellLength || latitudeCellLength < precision)
					&& (precision < longitudeCellLength || longitudeCellLength < precision)) {
				numRows = getLatitudeCellIndex(maxLatitude);
				numColumns = getLongitudeCellIndex(maxLongitude);
			}
		} catch (Exception e) {
			LOG.error("Error recalculating cell numbers based on current values of latitudes and longitudes.", e);
			numColumns = 1;
			numRows = 1;
		}
	}

	/**
	 * Whether or not a location's latitude and longitude is within this area
	 * (within min, max latitude, longitude of this area)
	 * 
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	public boolean isWithin(double latitude, double longitude) {
		return minLatitude <= latitude && latitude <= maxLatitude && minLongitude <= longitude
				&& longitude <= maxLongitude;
	}

	/**
	 * Get a latitude cell (or row) index of a location.
	 * 
	 * Divide the area between [{@link #minLatitude}, {@link #maxLatitude}] and
	 * [{@link #minLongitude}, {@link #maxLongitude}] into cells with the length
	 * of each cell along latitude is {@link #latitudeCellLength}, along
	 * longitude is {@link #longitudeCellLength}. <br>
	 * 
	 * The number of cells along latitude is {@link #numRows}, along longitude
	 * is {@link #numColumns}.
	 * 
	 * The cells are indexed from SOUTH to NORTH, and WEST to EAST. So the (0,
	 * 0) cell is the bottom-left cell.
	 * 
	 * @param latitude
	 * @return
	 */
	public int getLatitudeCellIndex(double latitude) {
		return (int) Math.floor(((latitude - minLatitude) / latitudeCellLength)) + 1;
	}

	/**
	 * Get a longitude cell (or column) index of a location.
	 * 
	 * Divide the area between [{@link #minLatitude}, {@link #maxLatitude}] and
	 * [{@link #minLongitude}, {@link #maxLongitude}] into cells with the length
	 * of each cell along latitude is {@link #latitudeCellLength}, along
	 * longitude is {@link #longitudeCellLength}. <br>
	 * 
	 * The number of cells along latitude is {@link #numRows}, along longitude
	 * is {@link #numColumns}.
	 * 
	 * The cells are indexed from SOUTH to NORTH, and WEST to EAST. So the (0,
	 * 0) cell is the bottom-left cell.
	 * 
	 * @param latitude
	 * @return
	 */
	public int getLongitudeCellIndex(double longitude) {
		return (int) Math.floor(((longitude - minLongitude) / longitudeCellLength)) + 1;
	}

	/**
	 * Get a cell index of a location.
	 * 
	 * Cell index = (row_index * num_row + column_index)
	 * 
	 * Divide the area between [{@link #minLatitude}, {@link #maxLatitude}] and
	 * [{@link #minLongitude}, {@link #maxLongitude}] into cells with the length
	 * of each cell along latitude is {@link #latitudeCellLength}, along
	 * longitude is {@link #longitudeCellLength}. <br>
	 * 
	 * The number of cells along latitude is {@link #numRows}, along longitude
	 * is {@link #numColumns}.
	 * 
	 * The cells are indexed from SOUTH to NORTH, and WEST to EAST. So the (0,
	 * 0) cell is the bottom-left cell.
	 * 
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	public int getCellIndex(double latitude, double longitude) {
		int rowIndex = getLatitudeCellIndex(latitude);
		int colIndex = getLongitudeCellIndex(longitude);
		return rowIndex * numColumns + colIndex;
	}
}
