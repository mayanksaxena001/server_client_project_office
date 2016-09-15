package com.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.clientfileupload.ClientUIMain;
import com.clientfileupload.Loader;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

@Component
@Qualifier("baseUIController")
public class BaseUIController implements Initializable {

    @FXML
    TabPane tabPane;
    
    @FXML
    Tab logIn;
    
    @FXML
    Tab signUp;
    
    @FXML
    VBox centerVBox;
    
    @FXML
    HBox bottomHBox;
    
    @FXML
	BorderPane borderPane;
    
    @Autowired
    LoginUIController loginUIController;
    
    @Autowired
    SignUpUIController signUpUIController;
    
    ObservableList<Node> list=FXCollections.observableArrayList();
    
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
    	try {
			logIn.setContent(Loader.getLoginScreen());
			signUp.setContent(Loader.getSignUpScreen());
		} catch (IOException e) {
			e.printStackTrace();
		}
    	initComponents();
	list.addAll(centerVBox.getChildren());
    }

	private void initComponents() {
		// new Image(url)
		Image image=new Image(ClientUIMain.class.getResourceAsStream("/fxml/pic_1.jpg"));
		// new BackgroundSize(width, height, widthAsPercentage, heightAsPercentage, contain, cover)
		BackgroundSize backgroundSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, true, false);
		// new BackgroundImage(image, repeatX, repeatY, position, size)
		BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.ROUND, BackgroundRepeat.ROUND, BackgroundPosition.CENTER, backgroundSize);
		// new Background(images...)
		Background background = new Background(backgroundImage);
		borderPane.setBackground(background);
    	DoubleProperty doubleProperty = new SimpleDoubleProperty(0);
        centerVBox.styleProperty().bind(Bindings
                .concat("-fx-background-color: rgba(56, 176, 209, ")
                .concat(doubleProperty)
                .concat(");"));
//        centerVBox.setEffect(new DropShadow(10, Color.AZURE));

        Slider slider = new Slider(0, 1, .3);
        doubleProperty.bind(slider.valueProperty());
        bottomHBox.getChildren().addAll( slider);
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
