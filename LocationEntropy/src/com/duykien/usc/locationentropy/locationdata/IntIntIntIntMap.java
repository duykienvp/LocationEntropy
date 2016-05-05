package com.duykien.usc.locationentropy.locationdata;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 
 * An implementation of multi-dimensional Map for <Integer, Integer, Integer, Integer>
 *  which is the number of co-occurrence of users u and v at location l
 * 
 * @author kiennd
 *
 */
public class IntIntIntIntMap {
	private Map<Integer, IntIntIntMap> data;

	public IntIntIntIntMap() {
		data = new HashMap<>();
	}

	/**
	 * Add c to current number of co-occurrences of u and v at location l. The
	 * current number starts with 0
	 * 
	 * @param u
	 * @param v
	 * @param l
	 * @param c
	 */
	public void addEntry(int u, int v, int l, int c) {
		// dU : all co-oc of u
		IntIntIntMap dU = data.get(u);
		if (dU == null) {
			dU = new IntIntIntMap();
		}

		dU.add(v, l, c);
		
		data.put(u, dU);
	}

	/**
	 * Get current number of co-occurrences of u and v at location l
	 * 
	 * @param u
	 * @param v
	 * @param l
	 */
	public int get(int u, int v, int l) {
		// dU : all co-oc of u
		IntIntIntMap dU = data.get(u);
		if (dU == null) {
			return 0;
		}

		return dU.get(v, l);
	}
	
	/**
	 * Get set of all second entries of the first key
	 * @param a
	 * @return
	 */
	public Set<Integer> getSecondEntries(int a) {
		Set<Integer> res = new HashSet<>();
		
		IntIntIntMap dA = data.get(a);
		if (dA != null) {
			res = new HashSet<>(dA.getKeySet());
		}
		
		return res;
	}
	
	/**
	 * Get set of all 3rd entries of the first and 2nd keys
	 * @param a
	 * @return
	 */
	public Set<Integer> getThirdEntries(int a, int b) {
		Set<Integer> res = new HashSet<>();
		
		IntIntIntMap dA = data.get(a);
		if (dA != null) {
			res = new HashSet<>(dA.getSecondEntries(b));
		}
		
		return res;
	}
	
	public Set<Integer> getKeySet() {
		return new HashSet<>(data.keySet());
	}
	
	/**
	 * Sum the last entrie in the chain.
	 * Eg. IntIntIntInt will sum the 4th Ints
	 * @return
	 */
	public Integer sumLastEntries(int first, int second) {
		IntIntIntMap d = data.get(first);
		if (d == null) {
			d = new IntIntIntMap();
		}
		
		return d.sumLastEntries(second);
	}
	
	public void clear() {
		data.clear();
	}
}
