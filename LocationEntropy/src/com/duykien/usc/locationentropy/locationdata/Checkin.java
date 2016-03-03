package com.duykien.usc.locationentropy.locationdata;

public class Checkin {
	private int userId;
	private long timestamp;
	private double latitude;
	private double longitude;
	private int locationId;

	public Checkin() {
		super();
	}

	public Checkin(int userId, long timestamp, double latitude, double longitude, int locationId) {
		super();
		this.userId = userId;
		this.timestamp = timestamp;
		this.latitude = latitude;
		this.longitude = longitude;
		this.locationId = locationId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public int getLocationId() {
		return locationId;
	}

	public void setLocationId(int locationId) {
		this.locationId = locationId;
	}

	@Override
	public String toString() {
		return "GowallaCheckin [userId=" + userId + ", timestamp=" + timestamp + ", latitude=" + latitude
				+ ", longitude=" + longitude + ", locationId=" + locationId + "]";
	}
}
