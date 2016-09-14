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
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.util.URL_ENUM;
import com.util.User;
import com.util.UserDetail;

@Component
public class LoginService {
    private static HttpURLConnection httpConnection = ServerClient.httpConnection;
    private static List<User> users = new ArrayList<User>();

     @PostConstruct
     public void init(){
     try{
     getUsers();
     System.out.println("Inside Login service init");
     }catch(Exception e){
     System.out.println("Exception calling user service "+e.getMessage());
     }
     }
    public List<User> getUsers() {
	String userURL = URL_ENUM.LOGIN_USERS.getUrl();
	try {
	    URL url = new URL(userURL);
	    httpConnection = (HttpURLConnection) url.openConnection();
	    int responseCode = httpConnection.getResponseCode();
	    if (responseCode == HttpURLConnection.HTTP_OK) {
		InputStream inputStream = httpConnection.getInputStream();
		JSONArray array = new JSONArray(
			getStringFromInputStream(inputStream));
		for (int i = 0; i < array.length(); i++) {
		    String json = array.getString(i);
		    User user = new ObjectMapper().readValue(json, User.class);
		    users.add(user);
		}
	    } else {
		System.out
			.println("Error calling user service " + responseCode);
	    }
	} catch (JSONException e) {
	    e.printStackTrace();
	} catch (MalformedURLException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	httpConnection.disconnect();
	return users;
    }

    public Boolean validateUser(User user) {
	return users.contains(user);
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

    public void storeUserDetailToServer(UserDetail userDetail) throws Exception {
	String userURL = URL_ENUM.STORE_USER_DETAILS.getUrl();
	System.out.println(userURL);
	StringBuilder errors = new StringBuilder();
	;
	try {
	    URL url = new URL(userURL);
	    httpConnection = (HttpURLConnection) url.openConnection();
	    httpConnection.setDoInput(true);
	        httpConnection.setDoOutput(true);    // indicates POST method
	        httpConnection.setRequestProperty("Content-Type",MediaType.APPLICATION_JSON_VALUE);
	        OutputStream outputStream = httpConnection.getOutputStream();
	        outputStream.write(new ObjectMapper().writeValueAsString(
	        	userDetail).getBytes());
	    int responseCode = httpConnection.getResponseCode();
	    if (responseCode != HttpURLConnection.HTTP_OK){
		errors.append("Error storing user details\n");
		System.out.println("Error storing user details" + responseCode);
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

    public List<String> getUserNameList() {
	List<String> list = new ArrayList<String>();
	for (User user : getUsers()) {
	    list.add(user.getUserName());
	}
	return list;
    }
}
