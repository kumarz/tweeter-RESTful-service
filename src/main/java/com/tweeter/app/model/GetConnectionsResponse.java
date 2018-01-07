package com.tweeter.app.model;

import java.util.ArrayList;

public class GetConnectionsResponse {
	private ArrayList<User>followers;
	private ArrayList<User>following;
	public ArrayList<User> getFollowers() {
		return followers;
	}
	public void setFollowers(ArrayList<User> followers) {
		this.followers = followers;
	}
	public ArrayList<User> getFollowing() {
		return following;
	}
	public void setFollowing(ArrayList<User> following) {
		this.following = following;
	}
}
