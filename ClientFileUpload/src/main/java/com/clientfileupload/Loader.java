package com.clientfileupload;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.util.Callback;

import com.util.SpringApplicationContext;

public class Loader {

    private static final String LOGIN_SCREEN = "/fxml/LoginUI.fxml";
    private static final String DASHBOARD_SCREEN = "/fxml/FileUploadClientUI.fxml";
    private static final String SIGNUP_SCREEN = "/fxml/SignUpUI.fxml";
    private static final String BASEUI_SCREEN = "/fxml/BaseUI.fxml";

    public static Parent getLoginScreen() throws IOException {
	return load(LOGIN_SCREEN);
    }

    public static Parent getDashBoardScreen() throws IOException {
	return load(DASHBOARD_SCREEN);
    }

    public static Parent getSignUpScreen() throws IOException {
	return load(SIGNUP_SCREEN);
    }
    
    public static Parent getBaseUIScreen() throws IOException {
	return load(BASEUI_SCREEN);
    }

    private static Parent load(String path) {
	FXMLLoader loader = new FXMLLoader();
	loader.setControllerFactory(new Callback<Class<?>, Object>() {
	    public Object call(Class<?> clazz) {
		return SpringApplicationContext.getBean(clazz);
	    }
	});

	URL url = Loader.class.getResource(path);
	try {
	    InputStream fxmlStream = url.openStream();
	    Parent parent = loader.load(fxmlStream);
	    return parent;
	} catch (IOException ex) {
	    throw new RuntimeException(ex);
	}
    }
}
