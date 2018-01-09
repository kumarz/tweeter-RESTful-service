package com.tweeter.app.model;

public class UsersPairedWithPopularFollowerResponse {
	private String userName;
	private String mostPopularFollower;
	private int NumberOfFollowers;
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getMostPopularFollower() {
		return mostPopularFollower;
	}
	public void setMostPopularFollower(String mostPopularUserName) {
		this.mostPopularFollower = mostPopularUserName;
	}
	public int getNumberOfFollowers() {
		return NumberOfFollowers;
	}
	public void setNumberOfFollowers(int numberOfFollowers) {
		NumberOfFollowers = numberOfFollowers;
	}
	

}
