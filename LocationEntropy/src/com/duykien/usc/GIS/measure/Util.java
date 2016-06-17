package com.duykien.usc.GIS.measure;

import java.util.ArrayList;

public class Util {
	public static final double PRECISION = 1e-15;
	
	public static double[] convertDoubleListToDoubleArray(ArrayList<Double> a) {
		double[] tmp = new double[a.size()];
		for (int i = 0; i < a.size(); i++)
			tmp[i] = a.get(i);
		return tmp;
	}
	
	public static double[] convertIntListToDoubleArray(ArrayList<Integer> a) {
		double[] tmp = new double[a.size()];
		for (int i = 0; i < a.size(); i++)
			tmp[i] = a.get(i);
		return tmp;
	}

	public static double[] convertIntArrayToDoubleArray(int[] ns) {
		double[] tmp = new double[ns.length];
		for (int i = 0; i < ns.length; i++)
			tmp[i] = ns[i];
		return tmp;
	}
	
	public static double[] convertToProbabilityArray(double[] c) {
		double s = 0;
		for (int i = 0; i < c.length; i++)
			s += c[i];
		
		double[] p = new double[c.length];
		for (int i = 0; i < c.length; i++)
			p[i] = (double) c[i] / s;
		
		return p;
	}
}
