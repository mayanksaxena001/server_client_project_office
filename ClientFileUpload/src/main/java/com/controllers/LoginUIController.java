package com.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.clientfileupload.Loader;
import com.service.LoginService;
import com.util.User;
import com.util.Utility;

@Component
@Qualifier("loginUIController")
public class LoginUIController implements Initializable {

    @FXML
    TextField userNameTextfFeld;

    @FXML
    PasswordField passwordTextField;

    @FXML
    Button loginButton;

    private User currentUser = null;
    
    @Autowired
    FileUploadClientUIController fileUploadClientUIController;
    
    @Autowired
    LoginService loginService;
    
    @Autowired
    BaseUIController baseUIController;
    
    public void initialize(URL arg0, ResourceBundle arg1) {
	loginButton.disableProperty().bind(
		passwordTextField.textProperty().isEmpty().or(userNameTextfFeld.textProperty().isEmpty()));
	reset();
    }

    @FXML
    public void handleLoginButtonAction(Event event){
	try {
	    if(Validate()){
		baseUIController.loadDashBoardScreen();
		fileUploadClientUIController.setCurrentUserLabel(currentUser);
	    }else{
		passwordTextField.clear();
		Alert alert=new Alert(AlertType.ERROR);
		    alert.setContentText("No user registered");
		    alert.showAndWait();
	    }
	} catch (IOException e) {
	 Utility.reportError(e.getMessage());
	}
    }

    @FXML
    public void handlePasswordFieldAction(Event event) {
	if (KeyEvent.KEY_PRESSED.equals(event.getEventType())
		&& !KeyCode.ENTER.equals(((KeyEvent) event).getCode())) {
	    event.consume();
	    return;
	}
	handleLoginButtonAction(event);
    }

    private Boolean Validate() {
	User user = new User(userNameTextfFeld.getText().trim(),
		passwordTextField.getText());
	Boolean value = loginService.validateUser(user);
	if(value){
	    currentUser=user;
	}
	return value;
    }

   public void reset(){
       userNameTextfFeld.clear();
       passwordTextField.clear();
       currentUser=null;
   }

}
