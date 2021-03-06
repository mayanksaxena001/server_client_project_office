package com.api;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.util.User;
import com.util.UserDetail;

@Path("/service")
public interface UserServiceApi {

    @GET
    @Path("/users")
    @Produces(MediaType.APPLICATION_JSON)
    Set<User> getUsers();
    
    @GET
    @Path("/users/details/{userName}")
    @Produces(MediaType.APPLICATION_JSON)
    UserDetail getUserDetails(@PathParam("userName") String userName);
    
    @POST
    @Path("/users/check")
    @Consumes(MediaType.APPLICATION_JSON)
    Boolean findUserByName(@Context HttpServletRequest request);
    
    @POST
    @Path("/users/store")
    @Consumes(MediaType.APPLICATION_JSON)
    Response storeUserDetailsOnServer(@Context HttpServletRequest request);
    
    @POST
    @Path("/users/update")
    @Consumes(MediaType.APPLICATION_JSON)
    Response updateUserDetailsOnServer(@Context HttpServletRequest request);
    
    @POST
    @Path("/users/current")
    @Consumes(MediaType.APPLICATION_JSON)
    Response setCurrentUserDetailsOnServer(@Context HttpServletRequest request);
}
