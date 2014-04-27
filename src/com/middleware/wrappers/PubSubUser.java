package com.middleware.wrappers;

public class PubSubUser {
private String userToken;
public String getUserToken() {
	return userToken;
}
public PubSubUser(String userToken, String value, String lookup) {
	super();
	this.userToken = userToken;
	this.value = value;
	this.lookup = lookup;
}
public PubSubUser() {
   super();
	//constructor Code
}
public void setUserToken(String userToken) {
	this.userToken = userToken;
}
public String getValue() {
	return value;
}
public void setValue(String value) {
	this.value = value;
}
public String getLookup() {
	return lookup;
}
public void setLookup(String lookup) {
	this.lookup = lookup;
}
private String value;
private String lookup;

}
