package com.duykien.usc.locationentropy.datatypes;

import java.util.ArrayList;

public class LocationInfo {
	private int locationId;
	private ArrayList<UserInfo> userInfos;
	
	public LocationInfo() {
		super();
	}
	
	public LocationInfo(int locationId, ArrayList<UserInfo> userInfos) {
		super();
		this.locationId = locationId;
		this.userInfos = userInfos;
	}

	public int getLocationId() {
		return locationId;
	}

	public void setLocationId(int locationId) {
		this.locationId = locationId;
	}

	public ArrayList<UserInfo> getUserInfos() {
		return userInfos;
	}

	public void setUserInfos(ArrayList<UserInfo> userInfos) {
		this.userInfos = userInfos;
	}

	@Override
	public String toString() {
		return "LocationInfo [locationId=" + locationId + ", userInfos=" + userInfos + "]";
	}
	
	
}
