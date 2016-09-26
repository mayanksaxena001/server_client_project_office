package com.api.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.FileUploadApi;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service("fileUploadService")
public class FileUploadService implements FileUploadApi {
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private ServletFileUpload uploader = null;

    @PostConstruct
    private void init() {
	System.out.println("Inside FileUploadService");
	DiskFileItemFactory fileFactory = new DiskFileItemFactory();
	this.uploader = new ServletFileUpload(fileFactory);
    }

    @Path("/upload/")
    @POST	
    public Response upload(@Context HttpServletRequest request)
	    throws ServletException, IOException {
	if (!ServletFileUpload.isMultipartContent(request)) {
	    throw new ServletException(
		    "Content type is not multipart/form-data");
	}

	// response.setContentType("text/html");
	StringBuilder out = new StringBuilder();
	try {
	    List<FileItem> fileItemsList = uploader.parseRequest(request);
	    Iterator<FileItem> fileItemsIterator = fileItemsList.iterator();
	    while (fileItemsIterator.hasNext()) {
		FileItem fileItem = fileItemsIterator.next();
		System.out.println("FieldName=" + fileItem.getFieldName());
		System.out.println("FileName=" + fileItem.getName());
		System.out.println("ContentType=" + fileItem.getContentType());
		System.out.println("Size in bytes=" + fileItem.getSize());

		String filePath=UserService.getCurrentUserDirectory().getAbsolutePath()+"/"+fileItem.getName();
		File file = new File(filePath);;
		System.out.println("Absolute Path at server="
			+ file.getAbsolutePath());
		fileItem.write(file);
		out.append("File " + fileItem.getName()
			+ " uploaded successfully.");
	    }
	} catch (Exception e) {
	    out.append(e.getMessage());
	    out.append("Exception in uploading file.");
	    return Response.status(500).type(MediaType.APPLICATION_JSON)
		    .entity(out).build();
	}
	return Response.status(200).type(MediaType.APPLICATION_JSON)
		.entity(out).build();
    }

    @Path("/download/")
    @GET
    public Response download(@Context HttpServletRequest request)
	    throws IOException, ServletException {
	String fileName = request.getParameter("fileName");
	FileInputStream fis = null;
	MediaType type = null;
	int len = 0;
	;
	String header = "";
	String headerValue = "";
	try {
	    if (fileName == null || fileName.equals("")) {
		throw new ServletException("File Name can't be null or empty");
	    }
	    File file = searchFileOnUserDirectory(UserService.getCurrentUserDirectory().getAbsolutePath(),fileName);
	    if (file==null) {
		throw new ServletException("File doesn't exists on server.");
	    }
	    System.out.println("File location on server::"
		    + file.getAbsolutePath());
	    fis = new FileInputStream(file);
	    type = MediaType.APPLICATION_OCTET_STREAM_TYPE;
	    len = (int) file.length();
	    header = "Content-Disposition";
	    headerValue = "attachment; filename=\"" + fileName + "\"";
	} catch (Exception e) {
	    StringBuilder out = new StringBuilder();
	    out.append(e.getMessage());
	    out.append("Exception in uploading file.");
	    out.append("</body></html>");
	    return Response.status(500).type(MediaType.APPLICATION_JSON)
		    .entity(out).build();
	}
	System.out.println("File successfully delivered to client");
	return Response.ok().header(header, headerValue)
		.header("Content-Length", len).type(type).entity(fis).build();

    }

    private File searchFileOnUserDirectory(String absolutePath, String fileName) {
	File file=new File(absolutePath);
	return searchFile(file,fileName);
    }

    private File searchFile(File file, String fileName) {
	File files[] = file.listFiles();
	for (File _file : files) {
	    if (_file.isDirectory()) {
		searchFile(_file, fileName);
	    } else {
		if (_file.getName().equals(fileName)) {
		    return _file;
		}
	    }
	}
	return null;
    }

    @Override
    public List<String> fileDirectory() {
	List<String> list=new ArrayList<String>();
	File file=UserService.getCurrentUserDirectory();
	if(file!=null){
	    getFiles(list, file);
	    System.out.println("Current user directory sent to client");
	}
	return list;
    }

    private void getFiles(List<String> list, File file) {
	File files[]=file.listFiles();
	for(File _file:files){
	    if(_file.isDirectory()){
		 list.add(getString(_file.getAbsolutePath()+File.separator));
		getFiles(list, _file);
	    }else{
		list.add(getString(_file.getAbsolutePath()));
	    }
	}
    }
    
    private String getString(String str) {
	String folderName=UserService.getCurrentUserDetail().getUniqueName();
	return str.substring(str.lastIndexOf(folderName+File.separator)+folderName.length(), str.length());
    }

    public static void main(String[] args) throws IOException {
//	FileUploadService fileUploadService=new FileUploadService();
//	for(String str:fileUploadService.fileDirectory()){
//	    System.out.println("Files "+ str);
//	}
	String str="D:\\mayank\\saxena\\21\\name\\age.txt";
	System.out.println(str.substring(0,str.lastIndexOf(File.separator)));
	System.out.println(Files.probeContentType(Paths.get(str)));
    }
}
