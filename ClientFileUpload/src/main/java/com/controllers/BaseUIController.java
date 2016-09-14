package com.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.clientfileupload.Loader;

@Component
@Qualifier("baseUIController")
public class BaseUIController implements Initializable {

    @FXML
    TabPane tabPane;
    
    @FXML
    VBox centerVBox;
    
    @Autowired
    LoginUIController loginUIController;
    
    @Autowired
    SignUpUIController signUpUIController;
    
    ObservableList<Node> list=FXCollections.observableArrayList();
    
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
	list.addAll(centerVBox.getChildren());
    }
    
    protected void loadDashBoardScreen() throws IOException {
	centerVBox.getChildren().clear();
	VBox vbox=(VBox) Loader.getDashBoardScreen();
	centerVBox.getChildren().add(vbox);
	VBox.setVgrow(vbox, Priority.ALWAYS);
    }
    
    protected void loadStartScreen(){
	centerVBox.getChildren().clear();
	loginUIController.reset();
	signUpUIController.reset();
	centerVBox.getChildren().addAll(list);
    }

}
