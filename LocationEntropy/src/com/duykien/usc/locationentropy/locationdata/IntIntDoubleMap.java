package com.duykien.usc.locationentropy.locationdata;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class IntIntDoubleMap {
	Map<Integer, Map<Integer, Double>> data;
	
	public IntIntDoubleMap() {
		data = new HashMap<>();
	}
	
	/**
	 * Add c to current value of a and b tuple
	 * 
	 * @param a
	 * @param b
	 * @param c
	 */
	public void add(int a, int b, double c) {
		Map<Integer, Double> dA = data.get(a);
		if (dA == null) {
			dA = new HashMap<>();
		}
		
		Double count = dA.get(b);
		if (count == null) {
			count = 0.0;
		}
		count += c;
		
		dA.put(b, count);
		data.put(a, dA);
	}
	
	/**
	 * Get current value of a and b tuple
	 * @param a
	 * @param b
	 * @return
	 */
	public double get(int a, int b) {
		Map<Integer, Double> dA = data.get(a);
		if (dA == null) {
			return 0;
		}
		
		Double count = dA.get(b);
		if (count == null) {
			return 0;
		}
		return count;
	}
	
	/**
	 * Get set of all second entries of the first key
	 * @param a
	 * @return
	 */
	public Set<Integer> getSecondEntries(int a) {
		Set<Integer> res = new HashSet<>();
		
		Map<Integer, Double> dA = data.get(a);
		if (dA != null) {
			res = new HashSet<>(dA.keySet());
		}
		
		return res;
	}
	
	public Set<Integer> getKeySet() {
		return new HashSet<>(data.keySet());
	}
	
	/**
	 * Sum the last entrie in the chain.
	 * Eg. IntIntIntInt will sum the 4th Ints; IntIntInt will sum the 3rd Ints
	 * @return
	 */
	public Double sumLastEntries(int first) {
		Map<Integer, Double> d = data.get(first);
		if (d == null) {
			d = new HashMap<Integer, Double>();
		}
		
		Double sum = 0.0;
		for (Double c : d.values()) {
			sum += c;
		}
		return sum;
	}
	
	public void clear() {
		data.clear();
	}
}
