package com.api;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/file")
@Consumes(MediaType.MULTIPART_FORM_DATA)
public interface FileUploadApi {

    @Path("/upload")
    @POST
    public Response upload(@Context HttpServletRequest request) throws ServletException, IOException ;
    
    @Path("/download")
    @GET
    public Response download(@Context HttpServletRequest request) throws IOException, ServletException;
}
