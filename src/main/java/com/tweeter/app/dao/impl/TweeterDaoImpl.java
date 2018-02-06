package com.tweeter.app.dao.impl;

import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tweeter.app.dao.TweeterDao;
import com.tweeter.app.model.GetConnectionsResponse;
import com.tweeter.app.model.Message;
import com.tweeter.app.model.MostPopularUser;
import com.tweeter.app.model.User;
import com.tweeter.app.model.UsersPairedWithPopularFollowerResponse;

public class TweeterDaoImpl implements TweeterDao {
	
	private static final Logger LOG = LoggerFactory.getLogger(TweeterDaoImpl.class);
	private static final String GET_USER_QUERY = "SELECT * FROM USERS WHERE USERNAME = ?";
	private static final String GET_FOLLOWERS_QUERY = "SELECT * FROM USERS WHERE USERNAME IN (SELECT FOLLOWER_USERNAME FROM FOLLOWERS WHERE USERNAME = ?)";
	private static final String GET_FOLLOWING_USERS_QUERY = "SELECT * FROM USERS WHERE USERNAME IN (SELECT USERNAME FROM FOLLOWERS WHERE FOLLOWER_USERNAME = ?)";
	private static final String INSERT_USER_QUERY = "INSERT OR REPLACE INTO USERS (USERNAME,FIRSTNAME,LASTNAME,AGE,EMAIL,GENDER) VALUES (?, ?, ?, ?, ?, ? );";
	private static final String POST_MESSAGE_QUERY = "INSERT INTO MESSAGES (CONTENT,USERNAME,CREATE_TIMESTAMP) VALUES (?, ?, ?);";
	private static final String GET_MESSAGES_QUERY = "SELECT * FROM MESSAGES WHERE USERNAME IN (SELECT USERNAME FROM FOLLOWERS WHERE follower_username = ?)"+
													 "UNION	SELECT * FROM MESSAGES WHERE USERNAME = ? ORDER BY ID DESC;";
	private static final String SEARCH_MESSAGES_QUERY = "SELECT * FROM MESSAGES WHERE USERNAME IN (?,(SELECT FOLLOWER_USERNAME FROM FOLLOWERS WHERE USERNAME = ?)) AND CONTENT like ? ORDER BY ID";
	private static final String FOLLOW_USER_QUERY = "INSERT OR REPLACE INTO FOLLOWERS (USERNAME, FOLLOWER_USERNAME) VALUES (?, ?);";
	private static final String UNOLLOW_USER_QUERY = "DELETE FROM FOLLOWERS WHERE USERNAME = ? AND FOLLOWER_USERNAME = ?;";
	private static final String GET_POPULAR_USER_QUERY = "select max(COUNT) as COUNT,USERS.USERNAME, USERS.FIRSTNAME, USERS.LASTNAME, USERS.EMAIL, USERS.AGE, USERS.GENDER from" +
														 "(select count(*) as COUNT, USERNAME, id from FOLLOWERS group by USERNAME) a INNER JOIN USERS ON USERS.USERNAME = a.USERNAME order by a.id ASC;";
	private static final String GET_USERS_PAIRED_WITH_MOST_POPULAR_USER = 
			"select t1.follower_username as username, t2.username as popularFollower, t2.follower_count as count from "+
			"(select f.follower_username, f.username from followers f order by follower_username) t1 "+
			"left join (select username,count(username) as follower_count from followers where username in (select follower_username from followers) group by username) t2 "+
			"on t1.username = t2.username group by t1.follower_username;";
	private static final String GET_FOLLOWING_USER_NAME_QUERY = "SELECT USERNAME FROM FOLLOWERS WHERE FOLLOWER_USERNAME = ?";
	
	private Connection dbConnection;
	
	public Connection getDbConnection() throws SQLException, ClassNotFoundException {
		if(this.dbConnection == null){
			Class.forName("org.sqlite.JDBC");
			//this.dbConnection = DriverManager.getConnection("jdbc:sqlite:C:/sqlite/tweeter.db");
			this.dbConnection = DriverManager.getConnection("jdbc:sqlite::memory:");
			this.dbConnection.setAutoCommit(false);
		}
		return this.dbConnection;
	}

	public TweeterDaoImpl() throws UnknownHostException {
		loadDbConfigurations();
	}
	
	private void loadDbConfigurations() {
		LOG.info("[TweeterDaoImpl - loading database...]");
	      try {
	         Class.forName("org.sqlite.JDBC");
	         Statement stmt = getDbConnection().createStatement();
	         String usersTableSql = "CREATE TABLE IF NOT EXISTS USERS " +
	                     "(USERNAME  TEXT PRIMARY KEY   NOT NULL, " + 
	                     " AGE            INT      NULL, " + 
	                     " FIRSTNAME      TEXT     NULL, " + 
	                     " LASTNAME       TEXT     NULL, " + 
	                     " GENDER         TEXT     NULL, " + 
	                     " EMAIL          TEXT     NULL);";
	         
	         String messagesTableSql = "CREATE TABLE IF NOT EXISTS MESSAGES ("+
										"ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"+
										"CONTENT TEXT NOT NULL,"+
										"USERNAME TEXT NOT NULL,"+
										"CREATE_TIMESTAMP TEXT NULL,"+
										"FOREIGN KEY (USERNAME) REFERENCES USERS(USERNAME));"; 
	         
	         String followersTableSql = "CREATE TABLE IF NOT EXISTS FOLLOWERS " +
                     "(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"+
                     "USERNAME  TEXT   NOT NULL, " + 
                     "FOLLOWER_USERNAME   TEXT     NOT NULL," +
                     "FOREIGN KEY (FOLLOWER_USERNAME) REFERENCES USERS(USERNAME));"; 
	         
	         String enforceForiegnKey = "PRAGMA foreign_keys = ON;";
	         stmt.executeUpdate(usersTableSql);
		     stmt.executeUpdate(messagesTableSql);
		     stmt.executeUpdate(followersTableSql);
		     stmt.executeUpdate(enforceForiegnKey);
		     dbConnection.commit();
		     stmt.close();
		      
	      } catch ( Exception e ) {
	         LOG.error( e.getClass().getName() + ": " + e.getMessage() );
	      }
	      LOG.info("[TweeterDaoImpl - Opened database successfully");
	}

	
	public User getUser(String usrName) throws Exception {

		User response = new User();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try{
			stmt = getDbConnection().prepareStatement(GET_USER_QUERY);
			stmt.setString(1, usrName);
			rs = stmt.executeQuery();
			int resultSetCount = 0;
			while (rs.next()) {
		         int age  = rs.getInt("AGE");
		         response.setUserName(StringUtils.trim(rs.getString("USERNAME")));
		         response.setFirstName(StringUtils.trim(rs.getString("FIRSTNAME")));
		         response.setLastName(StringUtils.trim(rs.getString("LASTNAME")));
		         response.setEmail(StringUtils.trim(rs.getString("EMAIL")));
		         response.setGender(StringUtils.trim(rs.getString("GENDER")));
		         response.setAge(age);
		         resultSetCount++;
			}
			if(resultSetCount == 0){
				response = null;
			}
			
		}catch (Exception e){
			LOG.info(e.getMessage());
			e.printStackTrace();
			throw new Exception();
		}finally{
			rs.close();
		    stmt.close();
		}
		
		return response;
	}
	
	
	public void createUser(User request) throws Exception {
		PreparedStatement stmt = null;
		
		try{
			stmt = getDbConnection().prepareStatement(INSERT_USER_QUERY);
	        stmt.setString(1, request.getUserName());
	        stmt.setString(2, request.getFirstName());
	        stmt.setString(3, request.getLastName());
	        stmt.setInt(4, request.getAge());
	        stmt.setString(5, request.getEmail());
	        stmt.setString(6, request.getGender());
		    stmt.executeUpdate();
		    dbConnection.commit();
		    
		}catch (Exception e){
			LOG.info(e.getMessage());
			e.printStackTrace();
			rollbackDb();
			throw new Exception();
		}finally{
			stmt.close();
		}
	}
	
	public void postMessage(Message request) throws Exception {
		PreparedStatement stmt = null;
		try{
			stmt = getDbConnection().prepareStatement(POST_MESSAGE_QUERY);
	        stmt.setString(1, request.getContent());
	        stmt.setString(2, request.getUserName());
	        stmt.setString(3, request.getCreateTimeStamp());
		    stmt.executeUpdate();
		    dbConnection.commit();
		    
		}catch (Exception e){
			LOG.info(e.getMessage());
			e.printStackTrace();
			rollbackDb();
			throw new Exception();
		}finally{
			stmt.close();
		}
	}
	
	public ArrayList<Message> getMessages(String userName, String textToBeSearched) throws Exception {
		ArrayList<Message> response = new ArrayList<Message>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try{
			
			if(textToBeSearched == null){
				stmt = getDbConnection().prepareStatement(GET_MESSAGES_QUERY);
			}else{
				LOG.info("[TweeterDaoImpl - getMessages] received request to search a text on the messages");
				stmt = getDbConnection().prepareStatement(SEARCH_MESSAGES_QUERY);
		        stmt.setString(3, "%"+textToBeSearched+"%");
			}
	        stmt.setString(1, userName);
	        stmt.setString(2, userName);
	        rs = stmt.executeQuery();
			while (rs.next()) {
				 Message message = new Message();
				 message.setUserName(StringUtils.trim(rs.getString("USERNAME")));
				 message.setContent(StringUtils.trim(rs.getString("CONTENT")));
				 message.setCreateTimeStamp(StringUtils.trim(rs.getString("CREATE_TIMESTAMP")));
				 response.add(message);
			}
			
		}catch (Exception e){
			LOG.info(e.getMessage());
			e.printStackTrace();
			rollbackDb();
			throw new Exception();
		}finally{
			rs.close();
		    stmt.close();
		}
		return response;
	}
	
	public void followOrUnfollowUser(String userName, String followUserName, Boolean toFollow) throws Exception {
		PreparedStatement stmt = null;
		try{
			if(toFollow){
				stmt = getDbConnection().prepareStatement(FOLLOW_USER_QUERY);
				stmt.setString(1, followUserName);
		        stmt.setString(2, userName);
			}else{
				stmt = getDbConnection().prepareStatement(UNOLLOW_USER_QUERY);
				stmt.setString(1, followUserName);
		        stmt.setString(2, userName);
			}
			
	        stmt.executeUpdate();
	        dbConnection.commit();
			
		}catch (Exception e){
			LOG.info(e.getMessage());
			e.printStackTrace();
			rollbackDb();
			throw new Exception();
		}finally{
			stmt.close();
		}
	}
	
	
public ArrayList<User> followers(String userName) throws Exception {
	ArrayList<User> response = new ArrayList<User>();
	PreparedStatement stmt = null;
	ResultSet rs = null;
	try{
		stmt = getDbConnection().prepareStatement(GET_FOLLOWERS_QUERY);
        stmt.setString(1, userName);
        rs = stmt.executeQuery();
		while (rs.next()) {
			 User user = new User();
	         int age  = rs.getInt("AGE");
	         user.setUserName(StringUtils.trim(rs.getString("USERNAME")));
	         user.setFirstName(StringUtils.trim(rs.getString("FIRSTNAME")));
	         user.setLastName(StringUtils.trim(rs.getString("LASTNAME")));
	         user.setEmail(StringUtils.trim(rs.getString("EMAIL")));
	         user.setGender(StringUtils.trim(rs.getString("GENDER")));
	         user.setAge(age);
	         response.add(user);
		}
		
	}catch (Exception e){
		LOG.info(e.getMessage());
		e.printStackTrace();
		rollbackDb();
		throw new Exception();
	}finally{
		rs.close();
	    stmt.close();
	}
	return response;
}

public ArrayList<User> following(String userName) throws Exception {
	ArrayList<User> response = new ArrayList<User>();
	PreparedStatement stmt = null;
	ResultSet rs = null;
	try{
		
		stmt = getDbConnection().prepareStatement(GET_FOLLOWING_USERS_QUERY);
        stmt.setString(1, userName);
        rs = stmt.executeQuery();
		while (rs.next()) {
			 User user = new User();
	         int age  = rs.getInt("AGE");
	         user.setUserName(StringUtils.trim(rs.getString("USERNAME")));
	         user.setFirstName(StringUtils.trim(rs.getString("FIRSTNAME")));
	         user.setLastName(StringUtils.trim(rs.getString("LASTNAME")));
	         user.setEmail(StringUtils.trim(rs.getString("EMAIL")));
	         user.setGender(StringUtils.trim(rs.getString("GENDER")));
	         user.setAge(age);
	         response.add(user);
		}
	   
		
	}catch (Exception e){
		LOG.info(e.getMessage());
		e.printStackTrace();
		rollbackDb();
		throw new Exception();
	}finally{
		 rs.close();
		 stmt.close();
	}
	return response;
}

public MostPopularUser getMostPouplarUser() throws Exception {
	MostPopularUser response = new MostPopularUser();
	PreparedStatement stmt = null;
	ResultSet rs = null;
	try{
		stmt = getDbConnection().prepareStatement(GET_POPULAR_USER_QUERY);
        rs = stmt.executeQuery();
        int resultSetCount = 0;
		while (rs.next()) {
	         int age  = rs.getInt("AGE");
	         response.setUserName(StringUtils.trim(rs.getString("USERNAME")));
	         response.setFirstName(StringUtils.trim(rs.getString("FIRSTNAME")));
	         response.setLastName(StringUtils.trim(rs.getString("LASTNAME")));
	         response.setEmail(StringUtils.trim(rs.getString("EMAIL")));
	         response.setGender(StringUtils.trim(rs.getString("GENDER")));
	         response.setAge(age);
	         response.setFollowersCount(rs.getInt("COUNT"));
	         resultSetCount++;
		}
	    if(resultSetCount == 0){
			response = null;
		}
	  
	}catch (Exception e){
		LOG.info(e.getMessage());
		e.printStackTrace();
		rollbackDb();
		throw new Exception();
	}finally{
		rs.close();
		stmt.close();
	}
	return response;
}

public GetConnectionsResponse getConnections(String userName) throws Exception {
	GetConnectionsResponse response = new GetConnectionsResponse();
	response.setFollowers(followers(userName));
	response.setFollowing(following(userName));
	return response;
}

public int getDistance(String userName1, String userName2) throws Exception {
	int depth = 0;
	try{
		HashMap<String, String> followingUserNames = new HashMap<String, String>();
		followingUserNames = getFollowingUserNames(userName1);
		depth = calculateDepth(userName2, depth, followingUserNames);
		
	}catch (Exception e){
		LOG.info(e.getMessage());
		e.printStackTrace();
		rollbackDb();
		throw new Exception();
	}
	return depth;
}

private int calculateDepth(String userName2, int depth, HashMap<String, String> followingUserNames)
		throws SQLException, ClassNotFoundException {
	HashMap<String, String> followingUserFollowerNames;
	depth++;
	if(!followingUserNames.containsValue(userName2)){
	    for (String key : followingUserNames.keySet()) {
		   followingUserFollowerNames = getFollowingUserNames(followingUserNames.get(key));
		   depth++; // on each level we got to increase the depth value
		   if(followingUserFollowerNames.containsValue(userName2)){
			   return depth; 
		   }
		}
	}
	return 0;
}

private HashMap<String, String> getFollowingUserNames(String value) throws SQLException, ClassNotFoundException {
	PreparedStatement stmt = null;
	ResultSet rs = null;
	HashMap<String, String> followingUserNames = new HashMap<String, String>();
	
	try{
		stmt = getDbConnection().prepareStatement(GET_FOLLOWING_USERS_QUERY);
	    stmt.setString(1, value);
	    rs = stmt.executeQuery();
	    while (rs.next()) {
			String uName = StringUtils.trim(rs.getString("USERNAME"));
	        followingUserNames.put(uName, uName);
		}
	}catch (Exception e){
		
	}finally{
	    rs.close();
	    stmt.close();
	}
    
	return followingUserNames;
}

public ArrayList<UsersPairedWithPopularFollowerResponse> getUserPairedWithMostPopularUser() throws Exception {
	ArrayList<UsersPairedWithPopularFollowerResponse> response = new ArrayList<UsersPairedWithPopularFollowerResponse>();
	PreparedStatement stmt = null;
	ResultSet rs = null;
	
	try{
		
		stmt = getDbConnection().prepareStatement(GET_USERS_PAIRED_WITH_MOST_POPULAR_USER);
        rs = stmt.executeQuery();
		while (rs.next()) {
			UsersPairedWithPopularFollowerResponse obj = new UsersPairedWithPopularFollowerResponse();
			obj.setNumberOfFollowers(rs.getInt("COUNT"));
			obj.setUserName(StringUtils.trim(rs.getString("USERNAME")));
			obj.setMostPopularFollower(StringUtils.trim(rs.getString("POPULARFOLLOWER")));
	        response.add(obj);
		}
		
	}catch (Exception e){
		LOG.info(e.getMessage());
		e.printStackTrace();
		rollbackDb();
		throw new Exception();
	}finally{
		 rs.close();
		 stmt.close();
	}
	return response;
}

protected void disconnectFromDbNoRollback() {
	  try {
		  if(dbConnection != null){
			  dbConnection.close();
		  }
	  } catch (Exception e) {
		 LOG.error("[TweeterDaoImpl - disconnectFromDbNoRollback ]closing the db Connection threw exception: " + e.getMessage());
	  }
   }

/**
* Rollback changes to the DB.
* If it fails, then log an error and continue.
* 
*/
protected void rollbackDb() {
  try {
	  if(dbConnection != null){
		  dbConnection.rollback();
	  }
  } catch (Exception e) {
	  LOG.error("[TweeterDaoImpl - rollbackDb ] closing the db Connection threw exception: " + e.getMessage());
  }
}


}
