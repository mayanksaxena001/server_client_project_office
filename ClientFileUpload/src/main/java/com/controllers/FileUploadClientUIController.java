package com.controllers;

import java.io.File;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.clientfileupload.Loader;
import com.service.ServerClient;
import com.util.URL_ENUM;
import com.util.User;
import com.util.Utility;

@Component
@Qualifier("fileUploadClientUIController")
public class FileUploadClientUIController implements Initializable{

    @FXML
    TextField urlTextField;
    
    @FXML
    Button uploadButton;
    
    @FXML
    Button downloadButton;
    
    @FXML
    Label userNameLabel;
    
    @FXML
    Button logoutButton;
    
    File currentSelectedFile=null;
    
    @Autowired
    BaseUIController baseUIController;
    
    public void initialize(URL arg0, ResourceBundle arg1) {
	reset();
    }
    
    @FXML
    public void handleUpload(ActionEvent event){
	 FileChooser fileChooser = new FileChooser();
	        fileChooser.setTitle("Choose file to upload");
	        currentSelectedFile = fileChooser.showOpenDialog(null);
	        if (currentSelectedFile != null) {
	           try {
		    ServerClient.uploadFile(currentSelectedFile);
		    downloadButton.setText("Download "+currentSelectedFile.getName());
		} catch (IOException e) {
		    Alert alert = new Alert(AlertType.NONE,"Error uploading File!!", ButtonType.OK);
                    alert.setTitle("Error");
                    alert.showAndWait();
                    e.printStackTrace();
		}
	        }
    }
    
    @FXML
    public void handleDownload(ActionEvent event){
	DirectoryChooser chooser = new DirectoryChooser();
	chooser.setTitle("JavaFX Projects");
//	File defaultDirectory = new File("c:/dev/javafx");
//	chooser.setInitialDirectory(defaultDirectory);
	File selectedDirectory = chooser.showDialog(null);
	try {
	    if(selectedDirectory!=null){
		ServerClient.downloadFile(currentSelectedFile.getName(), selectedDirectory.getAbsolutePath());
	    }
	    reset();
	} catch (IOException e) {
	    Alert alert = new Alert(AlertType.NONE,"Error downloading File!!", ButtonType.OK);
            alert.setTitle("Error");
            alert.showAndWait();
	    e.printStackTrace();
	}
    }
    
    @FXML
    public void handleLogoutAction(Event event){
	baseUIController.loadStartScreen();
    }

    private void reset() {
	urlTextField.setText(URL_ENUM.url);
	downloadButton.setText("Download");
    }
    
    void setCurrentUserLabel(User user){
	userNameLabel.setText(user.getUserName());
    }

}
