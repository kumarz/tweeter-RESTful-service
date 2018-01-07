package com.tweeter.app.model;

public class Message {
	private String userName;
	private String content;
	private String createTimeStamp;

	public String getUserName() {
		return userName.toLowerCase();
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getCreateTimeStamp() {
		return createTimeStamp;
	}
	public void setCreateTimeStamp(String createTimeStamp) {
		this.createTimeStamp = createTimeStamp;
	}

}
