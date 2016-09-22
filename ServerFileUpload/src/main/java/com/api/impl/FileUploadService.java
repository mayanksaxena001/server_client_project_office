package com.api.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
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
import org.springframework.stereotype.Service;

import com.api.FileUploadApi;

@Service("fileUploadService")
public class FileUploadService implements FileUploadApi {
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
	out.append("<html><head></head><body>");
	try {
	    List<FileItem> fileItemsList = uploader.parseRequest(request);
	    Iterator<FileItem> fileItemsIterator = fileItemsList.iterator();
	    while (fileItemsIterator.hasNext()) {
		FileItem fileItem = fileItemsIterator.next();
		System.out.println("FieldName=" + fileItem.getFieldName());
		System.out.println("FileName=" + fileItem.getName());
		System.out.println("ContentType=" + fileItem.getContentType());
		System.out.println("Size in bytes=" + fileItem.getSize());

		File file = new File(UserService.getDirectories().get(UserService.getCurrentuserDetail().getUser().getUserName()).getAbsolutePath()
			+ File.separator + fileItem.getName());
		System.out.println("Absolute Path at server="
			+ file.getAbsolutePath());
		fileItem.write(file);
		out.append("File " + fileItem.getName()
			+ " uploaded successfully.");
		out.append("<br>");
		out.append("<a href=\"UploadDownloadFileServlet?fileName="
			+ fileItem.getName() + "\">Download "
			+ new File(fileItem.getName()).getName() + "</a>");
	    }
	} catch (Exception e) {
	    out.append(e.getMessage());
	    out.append("Exception in uploading file.");
	    out.append("</body></html>");
	    return Response.status(500).type(MediaType.APPLICATION_JSON)
		    .entity(out).build();
	}
	out.append("</body></html>");
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
	    File file = new File(UserService.getDirectories().get(UserService.getCurrentuserDetail().getUser().getUserName()).getAbsolutePath()+ File.separator + fileName);
	    if (!file.exists()) {
		throw new ServletException("File doesn't exists on server.");
	    }
	    System.out.println("File location on server::"
		    + file.getAbsolutePath());
	    fis = new FileInputStream(file);
	    type = MediaType.APPLICATION_OCTET_STREAM_TYPE;
	    len = (int) file.length();
	    header = "Content-Disposition";
	    headerValue = "attachment; filename=\"" + fileName + "\"";
	    // ServletContext ctx = request.getServletContext();
	    // String mimeType = ctx.getMimeType(file.getAbsolutePath());
	    // response.setContentType(mimeType != null ? mimeType
	    // : "application/octet-stream");
	    // response.setContentLength((int) file.length());
	    // response.setHeader("Content-Disposition",
	    // "attachment; filename=\""
	    // + fileName + "\"");
	    // OutputStream os = response.;
	    // byte[] bufferData = new byte[1024];
	    // int read = 0;
	    // while ((read = fis.read(bufferData)) != -1) {
	    // os.write(bufferData, 0, read);
	    // }
	    // os.flush();
	    // os.close();
	    // fis.close();
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
}
