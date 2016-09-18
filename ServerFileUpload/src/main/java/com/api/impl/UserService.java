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
import javax.ws.rs.core.Response.Status;

import org.springframework.stereotype.Service;

import com.api.UserServiceApi;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.util.User;
import com.util.UserDetail;

@Service
public class UserService implements UserServiceApi {
    private static final List<User> userlist=new ArrayList<User>();
    private static final List<UserDetail> userDetailList=new ArrayList<UserDetail>();
    @PostConstruct
    private void init(){
	System.out.println("Inside user service init");
	User user1=new User("Admin","#Admin1#");
	userlist.add(user1);
	UserDetail userDetail=new UserDetail();
	userDetail.setUser(user1);
	userDetailList.add(userDetail);
	User user2=new User("Mayank","#Mayank1#");
	userlist.add(user2);
	userDetail=new UserDetail();
	userDetail.setUser(user2);
	userDetailList.add(userDetail);
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

    public Response storeUserDetailsOnServer(HttpServletRequest request) {
	try {
	    UserDetail userDetail = getUserDetailFromJSon(request);
	    if(userDetail!=null){
		userDetailList.add(userDetail);
		userlist.add(userDetail.getUser());
		displayUsers("storeUserDetailsOnServer".toUpperCase());
	    }else{
		 return Response.status(Status.NO_CONTENT).type(MediaType.APPLICATION_JSON)
			    .entity("Invalid Json Format").build();
	    }
	}catch (Exception e) {
	    e.printStackTrace();
	    return Response.status(500).type(MediaType.APPLICATION_JSON)
		    .entity("Invalid Json Format").build();
	}
	   return Response.ok().type(MediaType.APPLICATION_JSON)
		    .entity("Successfully stored").build();
    }
    
    public Response updateUserDetailsOnServer(HttpServletRequest request) {
	try {
	    UserDetail userDetail = getUserDetailFromJSon(request);
	    if(userDetail!=null){
		if(userlist.contains(userDetail.getUser())){
		    UserDetail updateUserDetail=userDetailList.stream().filter(p->p.getUser().equals(userDetail.getUser())).findFirst().get();
		    updateUserDetail.setFirstName(userDetail.getFirstName());
		    updateUserDetail.setLastName(userDetail.getLastName());
		    updateUserDetail.seteMailId(userDetail.geteMailId());
		    updateUserDetail.setDateOfBirth(userDetail.getDateOfBirth());
		    updateUserDetail.setGender(userDetail.getGender());
		}else{
		    Response.status(Status.NOT_FOUND).type(MediaType.APPLICATION_JSON)
		    .entity("User Not Found").build();
		}
		displayUsers("updateUserDetailsOnServer".toUpperCase());
	    }
	}catch (Exception e) {
	    e.printStackTrace();
	    return Response.status(Status.NOT_ACCEPTABLE).type(MediaType.APPLICATION_JSON)
		    .entity("Invalid Json Format").build();
	}
	   return Response.ok().type(MediaType.APPLICATION_JSON)
		    .entity("Successfully stored").build();
    }
    
    private UserDetail getUserDetailFromJSon(HttpServletRequest request)
	    throws IOException, JsonParseException, JsonMappingException {
	String json=getStringFromInputStream(request.getInputStream());
	UserDetail userDetail=new ObjectMapper().readValue(json.isEmpty()?"":json, UserDetail.class);
	System.out.println("getUserDetailFromJSon User Detail : "+userDetail);
	return userDetail;
    }
    
	public UserDetail getUserDetails(String userName) {
		UserDetail userDetail = userDetailList.stream().filter((p) -> p.getUser().getUserName().equals(userName))
				.findFirst().orElse(null);
		System.out.println("getUserDetails User Detail : " + userDetail);
		displayUsers("getUserDetails".toUpperCase());
		return userDetail;
	}
    
    private void displayUsers(String string){
    	System.out.println(string+"----------------users at servers-----------------");
    	userDetailList.stream().forEach(u->System.out.println(u) );
    }

}
