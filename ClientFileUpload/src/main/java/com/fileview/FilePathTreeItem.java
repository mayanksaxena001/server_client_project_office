package com.fileview;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Iterator;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class FilePathTreeItem extends TreeItem<String> {

    public static Image folderCollapseImage = new Image(FilePathTreeItem.class.getResourceAsStream("/image/folder.png"));
    public static Image folderExpandImage = new Image( FilePathTreeItem.class.getResourceAsStream("/com/fileview/folder-open.png"));
    public static Image fileImage = new Image( FilePathTreeItem.class.getResourceAsStream("/com/fileview/text-x-generic.png"));

    // this stores the full path to the file or directory
    private String fullPath;

    public String getFullPath() {
	return (this.fullPath);
    }

    private boolean isDirectory;

    public boolean isDirectory() {
	return (this.isDirectory);
    }

    public FilePathTreeItem(File file) {
	super(file.toString());
	this.fullPath = file.toString();
	// test if this is a directory and set the icon
	if (file.isDirectory()) {
	    this.isDirectory = true;
	    this.setGraphic(new ImageView(folderCollapseImage));
	} else {
	    this.isDirectory = false;
	    this.setGraphic(new ImageView(fileImage));
	    // if you want different icons for different file types this is
	    // where you'd do it
	}

	// set the value
	if (!fullPath.endsWith(File.separator)) {
	    // set the value (which is what is displayed in the tree)
	    String value = file.toString();
	    int indexOf = value.lastIndexOf(File.separator);
	    if (indexOf > 0) {
		this.setValue(value.substring(indexOf + 1));
	    } else {
		this.setValue(value);
	    }
	}

	this.addEventHandler(TreeItem.branchExpandedEvent(),
		new EventHandler() {
		    @Override
		    public void handle(Event e) {
			FilePathTreeItem source = (FilePathTreeItem) e
				.getSource();
			if (source.isDirectory() && source.isExpanded()) {
			    ImageView iv = (ImageView) source.getGraphic();
			    iv.setImage(folderExpandImage);
			}
			try {
			    if (source.getChildren().isEmpty()) {
				Path path = Paths.get(source.getFullPath());
				BasicFileAttributes attribs = Files
					.readAttributes(path,
						BasicFileAttributes.class);
				if (attribs.isDirectory()) {
				    DirectoryStream<Path> dir = Files
					    .newDirectoryStream(path);
				    Iterator<Path> itr=dir.iterator();
				   while(itr.hasNext()){
					FilePathTreeItem treeNode = new FilePathTreeItem(
						itr.next().toFile());
					source.getChildren().add(treeNode);
				    }
				}
			    } else {
				// if you want to implement rescanning a
				// directory for changes this would be the place
				// to do it
			    }
			} catch (IOException x) {
			    x.printStackTrace();
			}
		    }
		});
	this.addEventHandler(TreeItem.branchCollapsedEvent(),
		new EventHandler() {
		    @Override
		    public void handle(Event e) {
			FilePathTreeItem source = (FilePathTreeItem) e
				.getSource();
			if (source.isDirectory() && !source.isExpanded()) {
			    ImageView iv = (ImageView) source.getGraphic();
			    iv.setImage(folderCollapseImage);
			}
		    }
		});
    }
}
