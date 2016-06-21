package com.duykien.usc.GIS.measure;

public class MeasurementResults {
	public double klDivergencePrivateVsLimited = -1;
	public double ksTestValuePrivateVsLimited = -1;
	
	public double klDivergencePrivateVsActual = -1;
	public double ksTestValuePrivateVsActual = -1;
	
	public double klDivergenceLimitedVsActual = -1;
	public double ksTestValueLimitedVsActual = -1;
	@Override
	public String toString() {
		return "MeasurementResults [klDivergencePrivateVsLimited=" + klDivergencePrivateVsLimited
				+ ", ksTestValuePrivateVsLimited=" + ksTestValuePrivateVsLimited + ", klDivergencePrivateVsActual="
				+ klDivergencePrivateVsActual + ", ksTestValuePrivateVsActual=" + ksTestValuePrivateVsActual
				+ ", klDivergenceLimitedVsActual=" + klDivergenceLimitedVsActual + ", ksTestValueLimitedVsActual="
				+ ksTestValueLimitedVsActual + "]";
	}
}
