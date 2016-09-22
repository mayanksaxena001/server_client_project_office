package com.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.clientfileupload.Loader;
import com.service.LoginService;
import com.service.ServerClient;
import com.util.URL_ENUM;
import com.util.UserDetail;
import com.util.Utility;

@Component
@Qualifier("fileUploadClientUIController")
public class FileUploadClientUIController implements Initializable{

    private static final Image FOLDER_IMAGE=new Image(FileUploadClientUIController.class.getResourceAsStream("/image/folder.png"));
    private static final Image TEXT_FILE_IMAGE=new Image(FileUploadClientUIController.class.getResourceAsStream("/image/text.png"));
    private static final Image DOCUMENT_FILE_IMAGE=new Image(FileUploadClientUIController.class.getResourceAsStream("/image/document.png"));
    private static final ImageView NEXT_BUTTON_IMAGE= new ImageView(new Image(FileUploadClientUIController.class.getResourceAsStream("/image/go-next.png")));
    private static final ImageView PREVIOUS_BUTTON_IMAGE= new ImageView(new Image(FileUploadClientUIController.class.getResourceAsStream("/image/go-previous.png")));
    private static final DecimalFormat FORMATTER = new DecimalFormat("#.##");
    
    @FXML
    private TextField urlTextField;
    
    @FXML
    private TextField fileDirectoryTextField;
    
    @FXML
    private Button uploadButton;
    
    @FXML
    private Button editButton;
    
    @FXML
    private Button downloadButton;
    
    @FXML
    private Label userNameLabel;
    
    @FXML
    private Button logoutButton;
    
    @FXML
    private Button previousButton;
    
    @FXML
    private Button nextButton;
    
    @FXML
    private ScrollPane scrollPane;
    
    private File currentSelectedFile=null;
    
    @Autowired
    private BaseUIController baseUIController;
    
    @Autowired
    private SignUpUIController signUpUIController;
    
    @Autowired
    private LoginService loginService;
    
    private UserDetail currentUserDetail;
    
    public void initialize(URL arg0, ResourceBundle arg1) {
	reset();
	setCurrentUserLabel();
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
	chooser.setTitle("Choose");
//	chooser.setInitialDirectory(LoginService.getDirectories().get(currentUserDetail.getUser().getUserName()));
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
	try {
	    baseUIController.loadStartScreen();
	} catch (IOException e) {
	    e.printStackTrace();
	    Utility.reportError(e.getMessage());
	}
    }
    
    @FXML
    public void handleEditButtonAction(Event event){
	Stage stage=new Stage();
	try {
	    VBox vbox=(VBox) Loader.getSignUpScreen();
	    vbox.setBackground(baseUIController.getBackground("/image/pic.jpg"));
	    Scene scene=new Scene(vbox);
	    scene.getStylesheets().clear();
	    scene.getStylesheets().add("/stylesheet/app.css");
	    stage.setScene(scene);
	    stage.setTitle("Edit Details");
	    stage.show();
	    signUpUIController.setEditProperties();
	    signUpUIController.init(currentUserDetail);
	} catch (Exception e) {
	    e.printStackTrace();
	    Utility.reportError(e.getMessage());
	}
    }

    private void reset() {
	urlTextField.setText(URL_ENUM.url);
	downloadButton.setText("Download");
	previousButton.setGraphic(PREVIOUS_BUTTON_IMAGE);
	previousButton.setText("");
	previousButton.setDisable(true);
	nextButton.setDisable(true);
	nextButton.setGraphic(NEXT_BUTTON_IMAGE);
	nextButton.setText("");
	fileDirectoryTextField.clear();
    }
    
    private void setCurrentUserLabel(){
	if(currentUserDetail!=null){
	    if(currentUserDetail.getFirstName()==null || currentUserDetail.getLastName()==null){
		Utility.reportError("Please update profile details . Click edit button on top right");
	    }
	    userNameLabel.setText(currentUserDetail.getFirstName()+" "+currentUserDetail.getLastName());
	}
    }

    public UserDetail getCurrentUserDetail() {
        return currentUserDetail;
    }

    public void updateUserDetail(UserDetail currentUserDetail) {
        this.currentUserDetail = currentUserDetail;
        if(currentUserDetail!=null){
            try {
		loginService.setCurrentUserDetailAtServer(currentUserDetail);
	    } catch (Exception e) {
		e.printStackTrace();
		Utility.reportError(e.getMessage());
	    }
        }
	showFileView(LoginService.getDirectories().get(currentUserDetail.getUser().getUserName()).getAbsolutePath());
        setCurrentUserLabel();
    }

    private void showFileView(String path) {
	File file =new File(path);
	fileDirectoryTextField.setText(path);
   	GridPane gridPane = getGridPane(file);
   	scrollPane.setContent(gridPane);
       }

    private GridPane getGridPane(File file) {
	File[] filelist = file.listFiles();
	GridPane gridPane = new GridPane();
	gridPane.setPadding(new Insets(10, 10, 10, 10));
	gridPane.setHgap(10);
	gridPane.setVgap(10);
	int k = 0;
	boolean value = false;
	for (int i = 0; i < 10; i++) {
	    for (int j = 0; j <8 ; j++) {
		if (filelist.length != k) {
		    File tempFile = filelist[k++];
		    VBox vbox = getFolder(i, j, tempFile);
		    gridPane.add(vbox, j, i);
		} else {
		    value = true;
		    break;
		}
	    }
	    if (value) {
		break;
	    }
	}
	return gridPane;
    }

    private VBox getFolder(int i, int j, File tempFile) {
	HBox topHbox = new HBox();
	HBox bottomHbox = new HBox();
	VBox vbox=new VBox();
	vbox.setPrefWidth(100);
	vbox.setMaxWidth(100);
	vbox.setMinWidth(100);
	vbox.setPadding(new Insets(10,10,10,10));
	vbox.getStyleClass().clear();
	vbox.getStyleClass().add("pane");
	Label folderNameLabel=new Label(tempFile.getName());
	folderNameLabel.setTooltip(new Tooltip(tempFile.getName()));
	if(tempFile.isDirectory()){
	    topHbox.getChildren().add(new ImageView(FOLDER_IMAGE));
	}else{
	    topHbox.getChildren().add(new ImageView(DOCUMENT_FILE_IMAGE));
	}
	topHbox.setAlignment(Pos.CENTER);
	bottomHbox.getChildren().add(folderNameLabel);
	bottomHbox.setAlignment(Pos.CENTER);
	
	vbox.setUserData(tempFile);
	vbox.addEventHandler(MouseEvent.MOUSE_CLICKED, event->{
		VBox vbox1=(VBox) event.getSource();
		if(event.getEventType().equals(MouseEvent.MOUSE_CLICKED)){
		    if(event.getClickCount()==1){
			vbox1.getStyleClass().clear();
			vbox1.getStyleClass().add("pane-selected");
			vbox.requestFocus();
		    }
		    else if(event.getClickCount()>1){
			File file =(File) vbox.getUserData();
			if(file.isDirectory()){
			    showFileView(file.getAbsolutePath());
			    previousButton.setDisable(false);
			    previousButton.setUserData(file);
			    nextButton.setUserData(file);
			}else{
			    
		      StringBuilder prop=new StringBuilder("File Properties : ");
		      prop.append("\n File Size "+FORMATTER.format((file.length()/1024.00))+" KB");
		      prop.append("\n File Path "+file.getAbsolutePath());
		      Alert alert=new Alert(AlertType.INFORMATION);
		      alert.setContentText(prop.toString());
		      alert.setHeaderText(file.getName());
		      alert.showAndWait();
		  }
		    }
		}
	    
	});
	vbox.focusedProperty().addListener((observable,  oldValue, newValue) -> {
	    if(!newValue){
		vbox.getStyleClass().clear();
		vbox.getStyleClass().add("pane");
	   }
	});
	vbox.setFocusTraversable(true);
	vbox.getChildren().addAll(topHbox,bottomHbox);
	return vbox;
    }
    
    @FXML
    public void handleNextButtonClick(Event event){
	Button button=(Button) event.getSource();
	File file=(File) button.getUserData();
	showFileView(file.getAbsolutePath());
	nextButton.setDisable(true);
	previousButton.setDisable(false);
	previousButton.setUserData(file);
    }
    
    @FXML
    public void handlePreviousButtonClick(Event event){
	Button button=(Button) event.getSource();
	File file=(File) button.getUserData();
	showFileView(file.getParentFile().getAbsolutePath());
	nextButton.setUserData(file);
	if(file.getParentFile().getParentFile()!=null){
	    nextButton.setDisable(false);
	    previousButton.setDisable(false);
	    previousButton.setUserData(file.getParentFile());
	}else{
	    previousButton.setDisable(true);
	    nextButton.setDisable(true);
	}
    }
}
