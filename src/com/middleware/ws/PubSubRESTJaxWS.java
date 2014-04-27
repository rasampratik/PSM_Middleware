package com.middleware.ws;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.middleware.wrappers.PubSubUser;
import com.middleware.wrappers.PubSubMongoDBWrapper;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;






@SuppressWarnings("deprecation")
@Path("/main")
public class PubSubRESTJaxWS {

	
/*
 * http://localhost:7001/PSM_Middleware/REST/main/AAPL
 */
	@GET
	@Path("{company1}")
	@Produces(MediaType.TEXT_HTML)
	public String Info(@PathParam("company1") String company1) throws ClientProtocolException, IOException {
		@SuppressWarnings({ "resource" })
		
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet("http://dev.markitondemand.com/Api/v2/Quote/jsonp?symbol="+company1);
	
		HttpResponse response = client.execute(request);
		request.addHeader("Accept", "text/html");
		// Get the response
		BufferedReader rd = new BufferedReader
		  (new InputStreamReader(response.getEntity().getContent()));
		StringBuffer ro = new StringBuffer();
		String line = "";
		System.out.println(company1);
		while ((line = rd.readLine()) != null) {
		  System.out.println(line);
		  ro.append(line);
		} 
		String result = ro.toString();
		result = result.replace("(function () { })(", "").replace(")","");
		// Gson gson=new Gson(); 
			
	//	 Map<String,String> map=new HashMap<String,String>();
		// map=(HashMap<String,String>) gson.fromJson(result, map.getClass());
		HashMap<String,Object> result2=
		        new ObjectMapper().readValue(result, HashMap.class);
		return String.valueOf(result2.get("LastPrice"));
	}


	
	@POST
	@Path("/subscribe/{topicName}")
	@Produces(MediaType.TEXT_PLAIN)
	public String subscribeUser(@PathParam("topicName") String topicName,String jsonData) throws JsonParseException, JsonMappingException, IOException {
	PubSubUser  user =  new ObjectMapper().readValue(jsonData, PubSubUser.class);
	PubSubMongoDBWrapper.subscribe(user, topicName);
	return "Done Successfully!!";
	}
	@POST
	@Path("/unsubscribe/{topicName}")
	@Produces(MediaType.TEXT_PLAIN)
	public String unsubscribeUser(@PathParam("topicName") String topicName,String jsonData) throws JsonParseException, JsonMappingException, IOException {
	PubSubUser  user =  new ObjectMapper().readValue(jsonData, PubSubUser.class);
	PubSubMongoDBWrapper.subscribe(user, topicName);
	return "Done Successfully!!";
	}
}
