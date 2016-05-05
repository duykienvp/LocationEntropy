package com.duykien.usc.locationentropy.locationdata;

public class IntIntPair {
	private int num1;
	private int num2;
	
	public IntIntPair() {
		num1 = 0;
		num2 = 0;
	}

	public IntIntPair(int userId, int locId) {
		super();
		this.num1 = userId;
		this.num2 = locId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + num2;
		result = prime * result + num1;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IntIntPair other = (IntIntPair) obj;
		if (num2 != other.num2)
			return false;
		if (num1 != other.num1)
			return false;
		return true;
	}
	
	
}
