package com.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.layout.GridPane;

import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.util.KeyValuePair;
import com.util.URL_ENUM;

public class ServerClient {

    private static final int BUFFER_SIZE = 4096;
    public  static HttpURLConnection httpConnection=null;
    /**
     * Downloads a file from a URL
     * @param name file name
     * @param saveDir   path of the directory to save the file
     * @throws IOException
     */
    public static void downloadFile(String name, String saveDir)
	    throws IOException {
	String fileURL = URL_ENUM.DOWNLOAD.getUrl() + "?fileName=" + name;
	System.out.println(fileURL);
	URL url = new URL(fileURL);
	httpConnection = (HttpURLConnection) url.openConnection();
	int responseCode = httpConnection.getResponseCode();

	// always check HTTP response code first
	if (responseCode == HttpURLConnection.HTTP_OK) {
	    String fileName = "";
	    String disposition = httpConnection.getHeaderField("Content-Disposition");
	    String contentType = httpConnection.getContentType();
	    int contentLength = httpConnection.getContentLength();

	    if (disposition != null) {
		// extracts file name from header field
		int index = disposition.indexOf("filename=");
		if (index > 0) {
		    fileName = disposition.substring(index + 10,
			    disposition.length() - 1);
		}
	    } else {
		// extracts file name from URL
		fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1,
			fileURL.length());
	    }

	    System.out.println("Content-Type = " + contentType);
	    System.out.println("Content-Disposition = " + disposition);
	    System.out.println("Content-Length = " + contentLength);
	    System.out.println("fileName = " + fileName);

	    // opens input stream from the HTTP connection
	    InputStream inputStream = httpConnection.getInputStream();
	    String saveFilePath = saveDir + File.separator + fileName;

	    // opens an output stream to save into file
	    FileOutputStream outputStream = new FileOutputStream(saveFilePath);

	    int bytesRead = -1;
	    byte[] buffer = new byte[BUFFER_SIZE];
	    while ((bytesRead = inputStream.read(buffer)) != -1) {
		outputStream.write(buffer, 0, bytesRead);
	    }

	    outputStream.close();
	    inputStream.close();

	    System.out.println("File downloaded at " + saveFilePath);
	} else {
	    throw new RuntimeException("No file to download. Server replied HTTP code: "
			    + responseCode);
	}
	httpConnection.disconnect();
    }

    public static void uploadFile(File file) throws IOException {
	String charset = "UTF-8";
	MultipartUtility multipart = new MultipartUtility(httpConnection,URL_ENUM.UPLOAD.getUrl(), charset);
	multipart.addFilePart("textFile", file);
	List<String> response = multipart.finish();
	System.out.println("SERVER REPLIED:");
	for (String line : response) {
	    System.out.println("Upload Files Response:::\n" + line);
	}
    }
    
    public static List<String> getCurrentUserDirectories() throws IOException, JSONException{
	List<String> list=new ArrayList<String>();
	String userURL = URL_ENUM.GET_USER_DIRECTORIES.getUrl();
	URL url = new URL(userURL);
	System.out.println(userURL);
	httpConnection = (HttpURLConnection) url.openConnection();
	int responseCode = httpConnection.getResponseCode();
	if (responseCode == HttpURLConnection.HTTP_OK) {
	    InputStream inputStream = httpConnection.getInputStream();
	    JSONArray array = new JSONArray(
		    Service.getStringFromInputStream(inputStream));
	    for (int i = 0; i < array.length(); i++) {
		String json = array.getString(i);
		list.add(json);
	    }
	} else {
	    System.out.println("Error calling File service " + responseCode);
	    throw new RuntimeException("Error calling File service " + responseCode);
	}
	httpConnection.disconnect();
	return list;
    }
    
    public static boolean updateCurrentUserDirectoriesOnServer(String string) throws Exception {
   	String userURL = URL_ENUM.UPDATE_USER_DIRECTORIES.getUrl();
   	System.out.println(string);
   	URL url;
	try {
	    url = new URL(userURL);
   	System.out.println(userURL);
   	httpConnection = (HttpURLConnection) url.openConnection();
   	httpConnection.setDoInput(true);
	httpConnection.setDoOutput(true); 
	httpConnection.setRequestProperty("Content-Type",
		    MediaType.APPLICATION_JSON_VALUE);
	KeyValuePair keyValuePair=new KeyValuePair();
	keyValuePair.setKey("Key");
	keyValuePair.setValue(string);
   	OutputStream outputStream=httpConnection.getOutputStream();
   	outputStream.write(new ObjectMapper().writeValueAsString(keyValuePair).getBytes());
   	int responseCode = httpConnection.getResponseCode();
   	httpConnection.disconnect();
   	if (responseCode != HttpURLConnection.HTTP_OK) {
   	    throw new Exception("Error updating directory " + responseCode);
   	}
   	} catch (Exception e) {
	    e.printStackTrace();
	    throw new Exception(e.getMessage());
	}
   	return true;
    }
//    private static void sendGet(String fileName) throws MalformedURLException,
//	    IOException, ProtocolException {
//
//	URL obj = new URL(url + "?fileName=" + fileName);
//
//	HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//	con.setDoOutput(true);
//	// optional default is GET
//	con.setRequestMethod("GET");
//
//	// add request header
//	con.setRequestProperty("User-Agent", USER_AGENT);
//
//	int responseCode = con.getResponseCode();
//	System.out.println("Sending 'GET' request to URL : " + url);
//	System.out.println("Response Code : " + responseCode);
//
//	BufferedReader in = new BufferedReader(new InputStreamReader(
//		con.getInputStream()));
//	String inputLine;
//	StringBuffer response = new StringBuffer();
//
//	while ((inputLine = in.readLine()) != null) {
//	    response.append(inputLine);
//	}
//	in.close();
//
//	// print result
//	System.out.println(response.toString());
//    }
//
//    private static void sendPost() throws Exception {
//
//	URL obj = new URL(url + File.separator + "UploadDownloadFileServlet");
//	HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//
//	// add reuqest header
//	con.setRequestMethod("POST");
//	con.setRequestProperty("User-Agent", USER_AGENT);
//	con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
//
//	// String urlParameters =
//	// "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";
//
//	// Send post request
//	con.setDoOutput(true);
//	DataOutputStream wr = new DataOutputStream(con.getOutputStream());
//	// wr.writeBytes(urlParameters);
//	wr.flush();
//	wr.close();
//
//	int responseCode = con.getResponseCode();
//	System.out.println("\nSending 'POST' request to URL : " + url);
//	// System.out.println("Post parameters : " + urlParameters);
//	System.out.println("Response Code : " + responseCode);
//
//	BufferedReader in = new BufferedReader(new InputStreamReader(
//		con.getInputStream()));
//	String inputLine;
//	StringBuffer response = new StringBuffer();
//
//	while ((inputLine = in.readLine()) != null) {
//	    response.append(inputLine);
//	}
//	in.close();
//
//	// print result
//	System.out.println(response.toString());
//
//    }

    public static void main(String[] args) throws Exception {
	//
	// String saveDir = "D:/mayank_saxena1/";
	// downloadFile("ThermoScientific1.jpg", saveDir);
	// sendPost();

	// // String url =
	// "http://localhost:8080/ServletFileUploadDownloadExample/";
	// String charset = "UTF-8";
	// String param = "value";
	// File textFile = new File("D:\\test\\ThermoScientific1.jpg");
	// MultipartUtility multipart = new MultipartUtility(url, charset);
	// multipart.addFilePart("textFile", textFile);
	// // multipart.setPostRequest();
	// List<String> response = multipart.finish();
	// System.out.println("SERVER REPLIED:");
	// for (String line : response) {
	// System.out.println("Upload Files Response:::\n" + line);
	// get your server response here.
	// }
	// HttpURLConnection httpConnection = (HttpURLConnection) new
	// URL(url).openConnection();
	// httpConnection.setRequestMethod("POST");
	// httpConnection.setDoOutput(true);
	// // httpConnection.setRequestProperty("Content-Type",
	// "application/x-www-form-urlencoded;charset=" + charset);
	// String boundary = Long.toHexString(System.currentTimeMillis()); //
	// Just generate some unique random value.
	// httpConnection.setRequestProperty("Content-Type",
	// "multipart/form-data; boundary=" + boundary);
	// String CRLF = "\r\n"; // Line separator required by
	// multipart/form-data.
	//
	// OutputStream output = httpConnection.getOutputStream();
	// PrintWriter writer = new PrintWriter(new OutputStreamWriter(output,
	// charset), true);
	// writer.append("--" + boundary).append(CRLF);
	// writer.append("Content-Disposition: form-data; name=\"textFile\"; filename=\""
	// + textFile.getName() + "\"").append(CRLF);
	// writer.append("Content-Type: text/plain; charset=" +
	// charset).append(CRLF); // Text file itself must be saved in this
	// charset!
	// writer.append(CRLF).flush();
	// Files.copy(textFile.toPath(), output);
	// output.flush(); // Important before continuing with writer!
	// writer.append(CRLF).flush(); // CRLF is important! It indicates end
	// of boundary.
	// writer.append("--" + boundary + "--").append(CRLF).flush();
	// int responseCode = ((HttpURLConnection)
	// httpConnection).getResponseCode();
	// System.out.println(responseCode); // Should be 200
	// System.out.println("\nSending 'POST' request to URL : " + url);
	// // System.out.println("Post parameters : " + urlParameters);
	// System.out.println("Response Code : " + responseCode);
	// BufferedReader in = new BufferedReader(
	// new InputStreamReader(httpConnection.getInputStream()));
	// String inputLine;
	// StringBuffer response = new StringBuffer();
	//
	// while ((inputLine = in.readLine()) != null) {
	// response.append(inputLine);
	// }
	// in.close();
	//
	// //print result
	// System.out.println(response.toString());
    }
}
