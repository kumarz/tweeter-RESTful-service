package com.tweeter.app.service.impl;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.tweeter.app.dao.TweeterDao;
import com.tweeter.app.model.GetConnectionsResponse;
import com.tweeter.app.model.Message;
import com.tweeter.app.model.MostPopularUser;
import com.tweeter.app.model.User;
import com.tweeter.app.model.UsersPairedWithPopularFollowerResponse;
import com.tweeter.app.service.TweeterService;

public class TweeterServiceImpl implements TweeterService {
	
	private static final Logger LOG = LoggerFactory.getLogger(TweeterServiceImpl.class);
	
	@Autowired
	TweeterDao tweeterDao;

	public User getUser(String userName) throws Exception {
		return tweeterDao.getUser(userName);
	}

	public void createUser(User request) throws Exception {
		tweeterDao.createUser(request);
	}

	public void postMessage(Message request) throws Exception {
		tweeterDao.postMessage(request);
	}

	public ArrayList<Message> getMessages(String userName, String textToBeSearched) throws Exception {
		return tweeterDao.getMessages(userName, textToBeSearched);
	}

	public void followOrUnfollowUser(String userName, String followUserName, Boolean toFollow) throws Exception {
		tweeterDao.followOrUnfollowUser(userName, followUserName, toFollow);
	}

	public ArrayList<User> getFollowers(String userName) throws Exception {
		return tweeterDao.followers(userName);
	}

	public ArrayList<User> getFollowingUser(String userName) throws Exception {
		return tweeterDao.following(userName);
	}


	public MostPopularUser getMostPouplarUser() throws Exception {
		return tweeterDao.getMostPouplarUser();
	}

	public GetConnectionsResponse getConnections(String userName) throws Exception {
		return tweeterDao.getConnections(userName);
	}

	public ArrayList<UsersPairedWithPopularFollowerResponse> getUserPairedWithMostPopularUser() throws Exception {
		return tweeterDao.getUserPairedWithMostPopularUser();
	}

	public int getDistance(String userName1, String userName2) throws Exception {
		return tweeterDao.getDistance(userName1, userName2);
	}

}
