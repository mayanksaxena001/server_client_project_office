package com.api.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Service;

import com.api.UserServiceApi;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class UserService implements UserServiceApi {
    private static final List<User> userlist=new ArrayList<User>();
    private static final List<UserDetail> userDetailList=new ArrayList<UserDetail>();
    @PostConstruct
    private void init(){
	System.out.println("Inside user service init");
	userlist.add(new User("Admin","Admin"));
	userlist.add(new User("Mayank","Mayank"));
    }
    public List<User> getUsers() {
	return userlist;
    }

    public Boolean findUserByName(HttpServletRequest request) {
	User user=null;
	try {
	    user=new ObjectMapper().readValue(getStringFromInputStream(request.getInputStream()), User.class);
	}catch (Exception e) {
	    e.printStackTrace();
	    user=new User(null, null);
	}
	return userlist.contains(user);
    }
    
    private  String getStringFromInputStream(InputStream is) {

	BufferedReader br = null;
	StringBuilder sb = new StringBuilder();

	String line;
	try {

		br = new BufferedReader(new InputStreamReader(is));
		while ((line = br.readLine()) != null) {
			sb.append(line+"\n");
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
//    public User findUserByName(final String name) {
//	return list.stream().filter(u-> {
//		return u.getUserName().equals(name);
//	    }).findFirst().get();
//    }
    @Override
    public Response storeUserDetailsOnServer(HttpServletRequest request) {
	UserDetail userDetail=null;
	try {
	    String json=getStringFromInputStream(request.getInputStream());
	    userDetail=new ObjectMapper().readValue(json.isEmpty()?"":json, UserDetail.class);
	    if(userDetail!=null){
		userDetailList.add(userDetail);
		userlist.add(userDetail.getUser());
	    }
	}catch (Exception e) {
	    e.printStackTrace();
	    return Response.status(500).type(MediaType.APPLICATION_JSON)
		    .entity("Invalid Json Format").build();
	}
	   return Response.ok().type(MediaType.APPLICATION_JSON)
		    .entity("Successfully stored").build();
    }
    @Override
    public UserDetail getUsersDetails(String userName) {
	return userDetailList.stream().filter((p)->p.getUser().getUserName().equals(userName)).findFirst().orElse(null);
    }

}
