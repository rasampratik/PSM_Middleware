package com.middleware.wrappers;
import com.middleware.wrappers.PubSubUser;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

public class PubSubMongoDBWrapper {
	
	
	static void writeToMongo() throws UnknownHostException{
		List<Map<String,String>> subscriberList = new ArrayList<Map<String,String>>();
		
		Map<String,String> userMap = new HashMap<String,String>();
		userMap.put("userToken","arn:aws:sns:us-east-1:380190426285:endpoint/GCM/PubSubRegion2/2f131135-8b93-3c2a-8c4a-33259288c4b8");
		userMap.put("value","1214.00");
		userMap.put("lookup","LOW");
		
		subscriberList.add(userMap);
		
		DB db = new MongoClient("localhost",27017).getDB("pubSubDB");
		BasicDBObject basic = new BasicDBObject();
		basic.put("name","GOOG");
		basic.put("data",subscriberList);
		//userMap.clear();
		

		DBCollection dBCollection = db.getCollection("Channel");
		dBCollection.insert(basic);
		
		}
		static void readFromMongo() throws UnknownHostException{
			DB db = new MongoClient("localhost",27017).getDB("pubSubDB");
			BasicDBObject basic = new BasicDBObject();
			basic.put("name","GOOG");
			//basic.put("surname", "rasam");
			DBCollection dBCollection = db.getCollection("Channel");
			
			DBCursor dbCursor = dBCollection.find(basic);
			
			while(dbCursor.hasNext()){
				
				System.out.println(dbCursor.next());
			}
			}
		static void update() throws UnknownHostException{
			DB db = new MongoClient("localhost",27017).getDB("pubSubTest");
			BasicDBObject original = new BasicDBObject();
			//basic.put("name","pratik");
			original.put("surname", "rasam");
			DBCollection dBCollection = db.getCollection("Channel");
			BasicDBObject modified = new BasicDBObject();
			//basic.put("name","pratik");
			modified.put("surname", "rasam");
			modified.put("Address", "Mumbai");
			dBCollection.update(original, modified);
			
			}
		public static void subscribe(PubSubUser user,String topicName) throws JsonParseException, JsonMappingException, IOException{
			List<Map<String, String>> userList = getSubscriberList(topicName);
			Map<String,String> userMap = new HashMap<String,String>();
			userMap.put("userToken",user.getUserToken());
			userMap.put("value",user.getValue());
			userMap.put("lookup",user.getLookup());
			userList.add(userMap);
			DB db = new MongoClient("localhost",27017).getDB("pubSubDB");
			BasicDBObject original = new BasicDBObject();
		//Fetch original db Object
			original.put("name",topicName);
			DBCollection dBCollection = db.getCollection("Channel");
			
			BasicDBObject modified = new BasicDBObject();
		//Create modified object and replace with original object
			modified.put("name", topicName);
			modified.put("data", userList);
			dBCollection.update(original, modified);
			}
		public static void unsubscribe(PubSubUser user,String topicName) throws JsonParseException, JsonMappingException, IOException{
			List<Map<String, String>> userList = getSubscriberList(topicName);
			Iterator<Map<String,String>> listIterator = userList.iterator();
			while(listIterator.hasNext()){
				Map<String,String> current = listIterator.next();
				if(current.get("userToken").equals(user.getUserToken())){
					listIterator.remove();
				}
			}
			DB db = new MongoClient("localhost",27017).getDB("pubSubDB");
			BasicDBObject original = new BasicDBObject();
		//Fetch original db Object
			original.put("name",topicName);
			DBCollection dBCollection = db.getCollection("Channel");
			
			BasicDBObject modified = new BasicDBObject();
		//Create modified object and replace with original object
			modified.put("name", topicName);
			modified.put("data", userList);
			dBCollection.update(original, modified);
		}
		public static List<Map<String, String>> getSubscriberList(
				String topicName) throws JsonParseException, JsonMappingException, IOException {
			DB db = new MongoClient("localhost",27017).getDB("pubSubDB");
			BasicDBObject topicObject = new BasicDBObject();
			topicObject.put("name",topicName);
			DBCollection dBCollection = db.getCollection("Channel");
			DBCursor dbCursor = dBCollection.find(topicObject);
			if(dbCursor.count()==0){
				System.out.println("Creating new entry for"+topicName);
				List<Map<String,String>> subscriberList = new ArrayList<Map<String,String>>();
				topicObject.put("data",subscriberList);
				dBCollection.insert(topicObject);
				insertTopic(topicName);
				return subscriberList;
				
			}
			@SuppressWarnings("unchecked")
			List<Map<String, String>> userList = (List<Map<String, String>>) dbCursor.toArray().get(0).get("data");
			return userList;
		}
		public static List <String> getTopicList() throws JsonParseException, JsonMappingException, IOException {
			DB db = new MongoClient("localhost",27017).getDB("pubSubDB");
			BasicDBObject topicObject = new BasicDBObject();
			topicObject.put("name","topicList");
			DBCollection dBCollection = db.getCollection("Channel");
			DBCursor dbCursor = dBCollection.find(topicObject);
			@SuppressWarnings("unchecked")
			List<String> topicList = (List<String>) dbCursor.toArray().get(0).get("listElements");
		//	topicList.add("AAPL");
			
			return topicList;
		}
		public static void insertTopic(String topicName) throws UnknownHostException{
			DB db = new MongoClient("localhost",27017).getDB("pubSubDB");
			BasicDBObject topicObject = new BasicDBObject();
			topicObject.put("name","topicList");
			DBCollection dBCollection = db.getCollection("Channel");
			DBCursor dbCursor = dBCollection.find(topicObject);
			@SuppressWarnings("unchecked")
			List<String> userList = (List<String>) dbCursor.toArray().get(0).get("listElements");
			userList.add(topicName);
			BasicDBObject updatedTopicObject = new BasicDBObject();
			updatedTopicObject.put("name", "topicList");
			updatedTopicObject.put("listElements",userList);
			dBCollection.update(topicObject, updatedTopicObject);
		}
		public static void removeTopic(String topicName) throws UnknownHostException{
			DB db = new MongoClient("localhost",27017).getDB("pubSubDB");
			BasicDBObject topicObject = new BasicDBObject();
			topicObject.put("name","topicList");
			DBCollection dBCollection = db.getCollection("Channel");
			DBCursor dbCursor = dBCollection.find(topicObject);
			@SuppressWarnings("unchecked")
			List<String> userList = (List<String>) dbCursor.toArray().get(0).get("listElements");
			userList.remove(topicName);
			BasicDBObject updatedTopicObject = new BasicDBObject();
			updatedTopicObject.put("name", "topicList");
			updatedTopicObject.put("listElements",userList);
			dBCollection.update(topicObject, updatedTopicObject);
		
		}
}
