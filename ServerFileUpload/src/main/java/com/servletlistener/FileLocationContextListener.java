package com.servletlistener;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class FileLocationContextListener implements ServletContextListener {

    public static final String SERVER_FILE_PATH= "D:/server_file_store";
    public void contextInitialized(ServletContextEvent servletContextEvent) {
    	String rootPath = System.getProperty("catalina.home");
    	ServletContext ctx = servletContextEvent.getServletContext();
    	String relativePath = ctx.getInitParameter("tempfile.dir");
    	File file = new File(SERVER_FILE_PATH/*rootPath + File.separator + relativePath*/);
    	if(!file.exists()) file.mkdirs();
    	System.out.println("File Directory created to be used for storing files at : "+SERVER_FILE_PATH);
    	ctx.setAttribute("FILES_DIR_FILE", file);
    	ctx.setAttribute("FILES_DIR",SERVER_FILE_PATH/* rootPath + File.separator + relativePath*/);
    }

	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		//do cleanup if needed
	}
	
}
