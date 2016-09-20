package com.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import org.springframework.stereotype.Component;

import com.clientfileupload.Loader;
import com.util.Utility;

@Component
public class CenterUIController implements Initializable {

    @FXML
    TabPane tabPane;
    
    @FXML
    Tab logIn;
    
    @FXML
    Tab signUp;
    
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
	try {
	    logIn.setContent(Loader.getLoginScreen());
	    signUp.setContent(Loader.getSignUpScreen());
	} catch (IOException e) {
	    e.printStackTrace();
	    Utility.reportError(e.getMessage());
	}
    }

}
