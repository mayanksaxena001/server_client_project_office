package com.fileview;

import java.net.URISyntaxException;

import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FilesViewMain extends Application {

    private TreeView<String> treeView;

    public static void main(String[] args) {
	launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws URISyntaxException {
//	// create tree pane
//	VBox treeBox = new VBox();
//	treeBox.setPadding(new Insets(10, 10, 10, 10));
//	treeBox.setSpacing(10);
//	String hostName = "computer";
//	try {
//	    hostName = InetAddress.getLocalHost().getHostName();
//	} catch (UnknownHostException x) {
//	}
//	TreeItem<String> rootNode = new TreeItem<>(
//		hostName,
//		new ImageView(
//			new Image(this.getClass().getResourceAsStream("/com/fileview/computer.png"))));
//	Iterator<File> itr = Arrays.asList(new File("D:\\AIG\\calibration_workspace\\server_client_project_office").listFiles()).iterator();
//	while(itr.hasNext()){
//	FilePathTreeItem treeNode = new FilePathTreeItem(itr.next());
//	    rootNode.getChildren().add(treeNode);
//	}
//	rootNode.setExpanded(true);
//	treeView = new TreeView<>(rootNode);
//	// add everything to the tree pane
//	treeBox.getChildren().addAll(new Label("File browser"), treeView);
//	VBox.setVgrow(treeView, Priority.ALWAYS);
//
//	// setup and show the window
//	primaryStage.setTitle("JavaFX File Browse Demo");
//	StackPane root = new StackPane();
//	root.getChildren().addAll(treeBox);
//	primaryStage.setScene(new Scene(root, 400, 300));
	show(primaryStage);
	primaryStage.setTitle("Folder View");
	primaryStage.show();
    }

    private void show(Stage primaryStage) {
	HBox hbox1=null ;
	HBox hbox2=null; 
	ScrollPane scrollPane=new ScrollPane();
	scrollPane.setFitToHeight(true);
	scrollPane.setFitToWidth(true);
	GridPane gridPane=new GridPane();
	gridPane.setStyle("-fx-background-color: white;");
	gridPane.setPadding(new Insets(10, 10, 10, 10));
	gridPane.setHgap(10);
	gridPane.setVgap(10);
	Label label=null;
	Image image=FilePathTreeItem.folderCollapseImage;
	for(int i=0;i<6;i++){
	    for(int j=0;j<4;j++){
		hbox1 = new HBox();
		hbox2 = new HBox();
		VBox vbox=new VBox();
		vbox.setPadding(new Insets(10,10,10,10));
		vbox.setStyle("-fx-border-color: lightgrey;");
		label=new Label("Label "+ i+" "+j);
		label.setMinWidth(Label.USE_PREF_SIZE);
		hbox1.getChildren().add(new ImageView(image));
		hbox1.setAlignment(Pos.CENTER);
		hbox2.getChildren().add(label);
		hbox2.setAlignment(Pos.CENTER);
		vbox.getChildren().addAll(hbox1,hbox2);
		gridPane.add(vbox, i, j);
		
		vbox.addEventHandler(MouseEvent.MOUSE_CLICKED, event->{
			VBox vbox1=(VBox) event.getSource();
			if(event.getEventType().equals(MouseEvent.MOUSE_CLICKED)){
			    if(event.getClickCount()==1){
				vbox1.setStyle("-fx-background-color: #B2EBF2;-fx-border-color: #B2EBF2;");
				vbox.requestFocus();
			    }
//			    else if(event.getClickCount()>1){
//				vbox1.setStyle("-fx-background-color: white;-fx-border-color: lightGrey;");
//			    }
			}
		    
		});
		vbox.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
		    if(!newValue){
			vbox.setStyle("-fx-background-color: white;-fx-border-color: lightGrey;");
		   }
		});
		vbox.setFocusTraversable(true);
	    }
	    ColumnConstraints columnConstraints = new ColumnConstraints();
	    columnConstraints.setFillWidth(true);
	    columnConstraints.setHgrow(Priority.ALWAYS);
	    gridPane.getColumnConstraints().add(columnConstraints);
	}
	scrollPane.setContent(gridPane);
	Scene scene=new Scene(scrollPane);
	primaryStage.setScene(scene);
    }

}
