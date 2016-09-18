package com.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.util.URL_ENUM;
import com.util.User;
import com.util.UserDetail;

@Component
public class LoginService {
    private static HttpURLConnection httpConnection = ServerClient.httpConnection;
    public static List<User> users = new ArrayList<User>();
    public static List<UserDetail> userDetailList = new ArrayList<UserDetail>();

    @Autowired
	ObjectMapper objectMapper;
    
     @PostConstruct
     public void init(){
//     getUsers();
     System.out.println("Inside Login service init");
     }
    public List<User> getUsers() throws JsonParseException, JsonMappingException, IOException, JSONException {
	String userURL = URL_ENUM.LOGIN_USERS.getUrl();
	    URL url = new URL(userURL);
	    httpConnection = (HttpURLConnection) url.openConnection();
	    int responseCode = httpConnection.getResponseCode();
	    if (responseCode == HttpURLConnection.HTTP_OK) {
		InputStream inputStream = httpConnection.getInputStream();
		JSONArray array = new JSONArray(
			getStringFromInputStream(inputStream));
		 UserDetail userDetail=null;
		for (int i = 0; i < array.length(); i++) {
		    String json = array.getString(i);
		    User user =objectMapper.readValue(json, User.class);
		    users.add(user);
		    userDetail=new UserDetail();
		    userDetail.setUser(user);
		    userDetailList.add(userDetail);
		}
	    } else {
		System.out
			.println("Error calling user service " + responseCode);
	    }
	httpConnection.disconnect();
	return users;
    }

    public Boolean validateUser(User user) throws JsonParseException, JsonMappingException, IOException, JSONException {
	return getUsers().contains(user);
    }

    private static String getStringFromInputStream(InputStream is) {
	BufferedReader br = null;
	StringBuilder sb = new StringBuilder();

	String line;
	try {
	    br = new BufferedReader(new InputStreamReader(is));
	    while ((line = br.readLine()) != null) {
		sb.append(line + "\n");
	    }

	} catch (IOException e) {
	    e.printStackTrace();
	} finally {
	    if (br != null) {
		try {
		    br.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	}
	return sb.toString().trim();
    }

    public void storeUserDetailToServer(UserDetail userDetail,String path) throws Exception {
	String userURL = path;
	System.out.println(userURL);
	StringBuilder errors = new StringBuilder();
	try {
	    URL url = new URL(userURL);
	    httpConnection = (HttpURLConnection) url.openConnection();
	    httpConnection.setDoInput(true);
	        httpConnection.setDoOutput(true);    // indicates POST method
	        httpConnection.setRequestProperty("Content-Type",MediaType.APPLICATION_JSON_VALUE);
	        OutputStream outputStream = httpConnection.getOutputStream();
	        outputStream.write(objectMapper.writeValueAsString(
	        	userDetail).getBytes());
	    int responseCode = httpConnection.getResponseCode();
	    if (responseCode != HttpURLConnection.HTTP_OK){
		errors.append("Error storing user details\n");
		System.out.println("Error storing user details" + responseCode);
	    }else{
		userDetailList.add(userDetail);
	    }
	} catch (MalformedURLException e) {
	    errors.append(e.getMessage() + "\n");
	    e.printStackTrace();
	} catch (IOException e) {
	    errors.append(e.getMessage() + "\n");
	    e.printStackTrace();
	}
	httpConnection.disconnect();
	if (!errors.toString().isEmpty()) {
	    throw new RuntimeException(errors.toString());
	}
    }

    public List<String> getUserNameList() throws JsonParseException, JsonMappingException, IOException, JSONException {
	List<String> list = new ArrayList<String>();
	for (User user : getUsers()) {
	    list.add(user.getUserName());
	}
	return list;
    }
    public void updateUserDetails(UserDetail userDetail) throws Exception {
	storeUserDetailToServer(userDetail,URL_ENUM.UPDATE_USER_DETAILS.getUrl());
    }
    
    public UserDetail getUserDetail(String fileName) throws JsonParseException, JsonMappingException, IOException{
	    String userURL=URL_ENUM.GET_USER_DETAILS.getUrl()+fileName;
	    UserDetail userDetail=null;
		URL url = new URL(userURL);
	    httpConnection = (HttpURLConnection) url.openConnection();
	    int responseCode = httpConnection.getResponseCode();
	    if (responseCode == HttpURLConnection.HTTP_OK) {
		InputStream inputStream = httpConnection.getInputStream();
		userDetail=objectMapper.readValue(getStringFromInputStream(inputStream),UserDetail.class);
	    }
	    httpConnection.disconnect();
	    return userDetail;
}
}