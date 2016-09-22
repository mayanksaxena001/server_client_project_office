package com.api.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.UserServiceApi;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.servletlistener.FileLocationContextListener;
import com.util.User;
import com.util.UserDetail;

@Service
public class UserService implements UserServiceApi {
    private static final Set<User> userSet=new HashSet<User>();
    private static final Set<UserDetail> userDetailSet=new HashSet<UserDetail>();
    private static Map<String ,File> directories=new HashMap<String, File>();
    private static UserDetail currentuserDetail=new UserDetail();
    @Autowired
    private ObjectMapper objectMapper;
    @PostConstruct
    private void init(){
	System.out.println("Inside user service init");
	User user1=new User("Admin","#Admin1#");
	userSet.add(user1);
	UserDetail userDetail=new UserDetail();
	userDetail.setUser(user1);
	addFolderDirectory(userDetail);
	User user2=new User("Mayank","#Mayank1#");
	userSet.add(user2);
	userDetail=new UserDetail();
	userDetail.setUser(user2);
	addFolderDirectory(userDetail);
    }
    public Set<User> getUsers() {
	return userSet;
    }

    public Boolean findUserByName(HttpServletRequest request) {
	User user=null;
	try {
	    user=objectMapper.readValue(getStringFromInputStream(request.getInputStream()), User.class);
	}catch (Exception e) {
	    e.printStackTrace();
	    user=new User(null, null);
	}
	return userSet.contains(user);
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
		addFolderDirectory(userDetail);
		userSet.add(userDetail.getUser());
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
		if(userSet.contains(userDetail.getUser())){
		    UserDetail updateUserDetail=userDetailSet.stream().filter(p->p.getUser().equals(userDetail.getUser())).findFirst().get();
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
	UserDetail userDetail=objectMapper.readValue(json.isEmpty()?"":json, UserDetail.class);
	System.out.println("getUserDetailFromJSon User Detail : "+userDetail);
	return userDetail;
    }
    
    @Override
    public Response setCurrentUserDetailsOnServer(HttpServletRequest request) {
	try {
	    currentuserDetail=getUserDetailFromJSon(request);
	    System.out.println("Current user ".toUpperCase()+ currentuserDetail.toString());
	} catch (IOException e) {
	    e.printStackTrace();
	    return Response.status(500).type(MediaType.APPLICATION_JSON)
		    .entity(e.getMessage()).build();
	}
	return Response.ok().type(MediaType.APPLICATION_JSON)
		    .entity("Successfully Set user").build();
    }
    
	public UserDetail getUserDetails(String userName) {
		UserDetail userDetail = userDetailSet.stream().filter((p) -> p.getUser().getUserName().equals(userName))
				.findFirst().orElse(null);
		System.out.println("getUserDetails User Detail : " + userDetail);
		displayUsers("getUserDetails".toUpperCase());
		return userDetail;
	}
    
    private void displayUsers(String string){
    	System.out.println(string+"----------------users at servers-----------------");
    	userDetailSet.stream().forEach(u->System.out.println(u) );
    }
    
    private void addFolderDirectory(UserDetail userDetail){
	 if(userDetailSet.add(userDetail)){
	     File file=new File(FileLocationContextListener.SERVER_FILE_PATH+File.separator+userDetail.getUniqueName());
	     if(!file.exists())file.mkdirs();
	     directories.put(userDetail.getUser().getUserName(), file);
	     System.out.println("User directory created".toUpperCase()+file.getAbsolutePath());
	 }
    }

    public static Map<String, File> getDirectories() {
	return directories;
    }
    
    public static UserDetail getCurrentuserDetail() {
	return currentuserDetail;
    }
}
