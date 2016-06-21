package com.duykien.usc.GIS.measure;

import java.util.ArrayList;

public class LEHistogramInfo {
	private ArrayList<Double> bucketIndices = new ArrayList<>();
	private ArrayList<Integer> orgCount = new ArrayList<>();
	private ArrayList<Integer> noisyCount = new ArrayList<>();
	private ArrayList<Double> origCDF = new ArrayList<>();
	private ArrayList<Double> noisyCDF = new ArrayList<>();

	public ArrayList<Double> getBucketIndices() {
		return bucketIndices;
	}

	public void setBucketIndices(ArrayList<Double> bucketIndices) {
		this.bucketIndices = bucketIndices;
	}

	public ArrayList<Integer> getOrgCount() {
		return orgCount;
	}

	public void setOrgCount(ArrayList<Integer> orgCount) {
		this.orgCount = orgCount;
	}

	public ArrayList<Integer> getNoisyCount() {
		return noisyCount;
	}

	public void setNoisyCount(ArrayList<Integer> noisyCount) {
		this.noisyCount = noisyCount;
	}

	public ArrayList<Double> getOrigCDF() {
		return origCDF;
	}

	public void setOrigCDF(ArrayList<Double> origCDF) {
		this.origCDF = origCDF;
	}

	public ArrayList<Double> getNoisyCDF() {
		return noisyCDF;
	}

	public void setNoisyCDF(ArrayList<Double> noisyCDF) {
		this.noisyCDF = noisyCDF;
	}

}
