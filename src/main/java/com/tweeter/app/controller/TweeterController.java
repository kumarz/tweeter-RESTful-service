package com.tweeter.app.controller;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tweeter.app.model.GetConnectionsResponse;
import com.tweeter.app.model.Message;
import com.tweeter.app.model.MostPopularUser;
import com.tweeter.app.model.StatusBean;
import com.tweeter.app.model.User;
import com.tweeter.app.model.UsersPairedWithPopularFollowerResponse;
import com.tweeter.app.service.TweeterService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(tags="Tweeter")
@RequestMapping("/")
public class TweeterController {
	
	private static final Logger LOG = LoggerFactory.getLogger(TweeterController.class);
	
	@Autowired
	TweeterService service;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value="user/{userName}" , method = RequestMethod.GET)
	@ApiOperation(value = "user", notes = "gets the details of a specified user.")
	public ResponseEntity getUser(@PathVariable("userName") String userName ){
		User response = null ;
		LOG.info("[TweeterController - getUser] received request...");
		try{
			response = service.getUser(trim(userName));
			if(response == null){
				return new ResponseEntity(response, HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity(response, HttpStatus.OK);
		}catch(Exception e){
			return internalServerErrorResponse(e);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value="usersPairedWithMostPopularUser" , method = RequestMethod.GET)
	@ApiOperation(value = "usersPairedWithMostPopularUser", notes = "gets the details of all the users paired with most popular user with number of followers.")
	public ResponseEntity getUserPairedWithMostPopularUser(){
		ArrayList<UsersPairedWithPopularFollowerResponse> response = null ;
		LOG.info("[TweeterController - getUserPairedWithMostPopularUser] received request...");
		try{
			response = service.getUserPairedWithMostPopularUser();
			return new ResponseEntity(response, HttpStatus.OK);
		}catch(Exception e){
			return internalServerErrorResponse(e);
		}
	}
	
	@SuppressWarnings({ "rawtypes" })
	@RequestMapping(value="user" , method = RequestMethod.POST)
	@ApiOperation(value = "user", notes = "creartes a new user with the given information.")
	public ResponseEntity createUser(@RequestBody User request ){
		
		LOG.info("[TweeterController - createUser] received request...");
		try{
			service.createUser(request);
			return new ResponseEntity(HttpStatus.OK);
		}catch(Exception e){
			return internalServerErrorResponse(e);
		}
		
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value="messages/{userName}" , method = RequestMethod.GET)
	@ApiOperation(value = "messages/{userName}", notes = "gets all the messages posted by the specified user & the users that they are currently following.")
	public ResponseEntity getMessages(@PathVariable String userName, @RequestParam (value = "textToBeSearched", required= false) String textToBeSearched ){
		
		LOG.info("[TweeterController - getMessages] received request...");
		ArrayList<Message> response = new ArrayList<Message>();
		try{
			response = service.getMessages(trim(userName), trim(textToBeSearched));
			return new ResponseEntity(response, HttpStatus.OK);
		}catch(Exception e){
			return internalServerErrorResponse(e);
		}
		
	}
	
	@SuppressWarnings({ "rawtypes" })
	@RequestMapping(value="message" , method = RequestMethod.POST)
	@ApiOperation(value = "message", notes = "posts message for the specified user.")
	public ResponseEntity postMessage(@RequestBody Message request ){
		
		LOG.info("[TweeterController - postMessage] received request...");
		try{
			service.postMessage(request);
			return new ResponseEntity(HttpStatus.OK);
		}catch(Exception e){
			return internalServerErrorResponse(e);
		}
		
	}
	
	@SuppressWarnings({ "rawtypes" })
	@RequestMapping(value="{userName}/follow/{followUserName}" , method = RequestMethod.POST)
	@ApiOperation(value = "{userName}/follow/{followUserName}", notes = "specified user follows another user.")
	public ResponseEntity followUser(@PathVariable String userName, @PathVariable String followUserName ){
		
		LOG.info("[TweeterController - followUser] received request...");
		try{
			service.followOrUnfollowUser(trim(userName.toLowerCase()), trim(followUserName.toLowerCase()), true);
			return new ResponseEntity(HttpStatus.OK);
		}catch(Exception e){
			return internalServerErrorResponse(e);
		}
		
	}
	
	@SuppressWarnings({ "rawtypes" })
	@RequestMapping(value="{userName}/unfollow/{followUserName}" , method = RequestMethod.POST)
	@ApiOperation(value = "{userName}/unfollow/{followUserName}", notes = "specified user unfollows another user.")
	public ResponseEntity unfollowUser(@PathVariable String userName, @PathVariable String followUserName ){
		
		LOG.info("[TweeterController - unfollowUser] received request...");
		try{
			service.followOrUnfollowUser(trim(userName.toLowerCase()), trim(followUserName.toLowerCase()), false);
			return new ResponseEntity(HttpStatus.OK);
		}catch(Exception e){
			return internalServerErrorResponse(e);
		}
		
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value="followers/{userName}" , method = RequestMethod.GET)
	@ApiOperation(value = "followers/{userName}", notes = "gets the list of followers for the specified user")
	public ResponseEntity getFollowers(@PathVariable String userName){
		ArrayList<User> response = new ArrayList<User>();
		LOG.info("[TweeterController - getFollowers] received request...");
		try{
			response = service.getFollowers(trim(userName.toLowerCase()));
			return new ResponseEntity(response,HttpStatus.OK);
		}catch(Exception e){
			return internalServerErrorResponse(e);
		}
		
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value="following/{userName}" , method = RequestMethod.GET)
	@ApiOperation(value = "following/{userName}", notes = "gets the list of following users for the specified user")
	public ResponseEntity getFollowingUser(@PathVariable String userName){
		ArrayList<User> response = new ArrayList<User>();
		LOG.info("[TweeterController - getFollowingUser] received request...");
		try{
			response = service.getFollowingUser(trim(userName.toLowerCase()));
			return new ResponseEntity(response,HttpStatus.OK);
		}catch(Exception e){
			return internalServerErrorResponse(e);
		}
		
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value="user/mostPopular" , method = RequestMethod.GET)
	@ApiOperation(value = "user/mostPopular", notes = "gets details of the user who is the most popular")
	public ResponseEntity getMostPouplarUser(){
		LOG.info("[TweeterController - getMostPouplarUser] received request...");
		try{
			MostPopularUser response = service.getMostPouplarUser();
			if(response == null){
				return new ResponseEntity(response, HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity(response,HttpStatus.OK);
		}catch(Exception e){
			return internalServerErrorResponse(e);
		}
		
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value="connections/{userName}" , method = RequestMethod.GET)
	@ApiOperation(value = "connections/{userName}", notes = "gets details of the user who are following the user and the users who being followed by specified username")
	public ResponseEntity getConnections(@PathVariable String userName){
		LOG.info("[TweeterController - getConnections] received request...");
		try{
			GetConnectionsResponse response = service.getConnections(trim(userName.toLowerCase()));
			return new ResponseEntity(response,HttpStatus.OK);
		}catch(Exception e){
			return internalServerErrorResponse(e);
		}
		
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value="distance/{userName1}/{userName2}" , method = RequestMethod.GET)
	@ApiOperation(value = "distance/{userName1}/{userName2}", notes = "gets shortest distance between two users via their followers.")
	public ResponseEntity getDistance(@PathVariable String userName1, @PathVariable String userName2){
		LOG.info("[TweeterController - getDistance] received request...");
		try{
			int response = service.getDistance(trim(userName1.toLowerCase()),trim(userName2.toLowerCase()));
			return new ResponseEntity(response,HttpStatus.OK);
		}catch(Exception e){
			return internalServerErrorResponse(e);
		}
		
	}

	/**
	 * This method is to trim the strings and its null safe as well. 
	 * we would like to catch them as soon as the request comes in.
	 * So that we dont have to worry about down the way to db while processing them
	 * @param input
	 * @return trimmed input string
	 */
	private String trim(String input){
		return StringUtils.trim(input);
		
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private ResponseEntity internalServerErrorResponse(Exception ex) {
		LOG.error("Error occured processing Request", ex);
		StatusBean errorBean = new StatusBean();
		errorBean.setStatusDesc(ex.getMessage());
		return new ResponseEntity(errorBean, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}