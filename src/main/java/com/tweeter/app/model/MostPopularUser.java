package com.tweeter.app.model;

public class MostPopularUser {

	private String userName;
	private String firstName;
	private String lastName;
	private int age;
	private String email;
	private String gender;
	private int followersCount;

	public MostPopularUser(){
		
	}
	
	public MostPopularUser(MostPopularUser request){
		this.userName = request.getUserName();
		this.firstName = request.getFirstName();
		this.lastName = request.getLastName();
		this.age = request.getAge();
		this.email = request.getEmail();
	}

	public String getUserName() {
		return userName.toLowerCase();
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public MostPopularUser(String username){
		this.userName = username;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public int getFollowersCount() {
		return followersCount;
	}
	public void setFollowersCount(int followersCount) {
		this.followersCount = followersCount;
	}

}