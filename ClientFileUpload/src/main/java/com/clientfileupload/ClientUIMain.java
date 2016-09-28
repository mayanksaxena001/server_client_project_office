package com.clientfileupload;

import java.io.File;
import java.util.Optional;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import com.util.SpringApplicationContext;

public class ClientUIMain extends Application {

    public static final String CLIENT_FILE_PATH= "D:/client_file_store";
    @Override
    public void start(Stage primaryStage) throws Exception {
	Scene scene = new Scene(Loader.getBaseUIScreen());
//	primaryStage.initStyle(StageStyle.UTILITY);
	primaryStage.setScene(scene);
	// primaryStage.setResizable(false);
	primaryStage.setTitle("Client File Upload");
	primaryStage.getScene().getStylesheets().add("/stylesheet/app.css");
	// primaryStage.setMaximized(true);
	confirmExit(primaryStage);
//	primaryStage.setMaximized(true);
	primaryStage.show();
	createDirectory();
    }

    private void createDirectory() {
	File file =new File(CLIENT_FILE_PATH);
	if(file.exists()){
	    if(file.delete()){
		System.out.println(CLIENT_FILE_PATH+" Deleted");
	    }else{
		System.out.println("Unable to delete "+CLIENT_FILE_PATH);
	    }
	}else{
	    System.out.println(CLIENT_FILE_PATH+" Created");
	    file.mkdirs();
	}
	System.out.println("client file store created at "+file.getAbsolutePath());
    }

    private void confirmExit(Stage primaryStage) {
	primaryStage.setOnCloseRequest(event -> {
	    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
	    confirm.setResizable(false);
	    confirm.setHeaderText(null);
	    confirm.setContentText("Are you sure ?");
	    Optional<ButtonType> buttonType = confirm.showAndWait();
	    if (!buttonType.get().equals(ButtonType.OK)) {
		event.consume();
	    }
	});
    }

    public static void main(String[] args) {
	SpringApplicationContext springApplicationContext = new SpringApplicationContext();
	launch(args);
    }
}
