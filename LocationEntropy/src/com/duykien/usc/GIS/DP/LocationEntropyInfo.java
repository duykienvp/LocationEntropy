package com.duykien.usc.GIS.DP;

public class LocationEntropyInfo {
	private int locationId = -1;
	private int numUser = -1;
	private double entropy = -1;
	private double privateEntropy = -1;
	private double noise = -1;

	public LocationEntropyInfo() {
		super();
	}

	public LocationEntropyInfo(int locationId, int numUser, double entropy, double privateEntropy, double noise) {
		super();
		this.locationId = locationId;
		this.numUser = numUser;
		this.entropy = entropy;
		this.privateEntropy = privateEntropy;
		this.noise = noise;
	}

	public LocationEntropyInfo(LocationEntropyInfo rh) {
		super();
		this.locationId = rh.getLocationId();
		this.numUser = rh.getNumUser();
		this.entropy = rh.getEntropy();
		this.privateEntropy = rh.getPrivateEntropy();
		this.noise = rh.getNoise();
	}
	
	public int getLocationId() {
		return locationId;
	}

	public void setLocationId(int locationId) {
		this.locationId = locationId;
	}

	public int getNumUser() {
		return numUser;
	}

	public void setNumUser(int numUser) {
		this.numUser = numUser;
	}

	public double getEntropy() {
		return entropy;
	}

	public void setEntropy(double entropy) {
		this.entropy = entropy;
	}

	public double getPrivateEntropy() {
		return privateEntropy;
	}

	public void setPrivateEntropy(double privateEntropy) {
		this.privateEntropy = privateEntropy;
	}

	public double getNoise() {
		return noise;
	}

	public void setNoise(double noise) {
		this.noise = noise;
	}
}
