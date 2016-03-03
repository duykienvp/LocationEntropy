package com.duykien.usc.locationentropy.datatypes;

public class UserInfo {
	private int userId;
	private int numCheckins;
	
	public UserInfo() {
		super();
	}

	public UserInfo(int userId, int numCheckins) {
		super();
		this.userId = userId;
		this.numCheckins = numCheckins;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getNumCheckins() {
		return numCheckins;
	}

	public void setNumCheckins(int numCheckins) {
		this.numCheckins = numCheckins;
	}

	@Override
	public String toString() {
		return "UserInfo [userId=" + userId + ", numCheckins=" + numCheckins + "]";
	}
	
	
}
