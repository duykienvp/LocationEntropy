package com.duykien.usc.GIS.measure;

public class MeasurementResults {
	public double klDivergenceNoisyVsCut = -1;
	public double ksTestValueNoisyVsCut = -1;
	
	public double klDivergenceNoisyVsUncut = -1;
	public double ksTestValueNoisyVsUncut = -1;
	
	public double klDivergenceCutVsUncut = -1;
	public double ksTestValueCutVsUncut = -1;
	@Override
	public String toString() {
		return "MeasurementResults [klDivergenceNoisyVsCut=" + klDivergenceNoisyVsCut + ", ksTestValueNoisyVsCut="
				+ ksTestValueNoisyVsCut + ", klDivergenceNoisyVsUncut=" + klDivergenceNoisyVsUncut
				+ ", ksTestValueNoisyVsUncut=" + ksTestValueNoisyVsUncut + ", klDivergenceCutVsUncut="
				+ klDivergenceCutVsUncut + ", ksTestValueCutVsUncut=" + ksTestValueCutVsUncut + "]";
	}
	
	
}
