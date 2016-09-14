package com.util;

import java.io.File;

public enum URL_ENUM {

    UPLOAD("file/upload"),DOWNLOAD("file/download"),LOGIN_USERS("service/users"),LOGIN_CHECK("service/users/check"),GET_USER_DETAILS("service/users/details"),STORE_USER_DETAILS("service/users/store"),UPDATE_USER_DETAILS("service/users/update");
    public static final String url="http://localhost:8080/ServerFileUpload/api/";
    String code="";
    private URL_ENUM(String code) {
	this.code=code;
    }
    
    public String getUrl() {
	return url+code+"/";
    }
    
}
