package com.api;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/file")
public interface FileUploadApi {

    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response upload(@Context HttpServletRequest request) throws ServletException, IOException ;
    
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response download(@Context HttpServletRequest request) throws IOException, ServletException;
    
    @GET
    @Path("/directories")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> fileDirectory();
    
    @POST
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateDirectory(@Context HttpServletRequest request);
}
