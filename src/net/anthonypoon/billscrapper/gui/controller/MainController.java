package net.anthonypoon.billscrapper.gui.controller;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainController implements Initializable, Controller {
    @FXML private Button importPDFButton;
    @FXML private Button viewRecordButton;
    @FXML private Button exitButton;
    private MainController self = this;
    private List<ControllerListener> importListeners = new ArrayList<>();
    private List<ControllerListener> viewListeners = new ArrayList<>();
    private List<File> fileArray;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        importPDFButton.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                FileChooser browser = new FileChooser();
                browser.setTitle("Select PDF file(s)");
                fileArray = browser.showOpenMultipleDialog(importPDFButton.getScene().getWindow());
                if (fileArray != null) {
                    for(ControllerListener listener : importListeners) {
                        listener.callback(self);
                    }
                }
            }            
        });
        
        viewRecordButton.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                for(ControllerListener listener : viewListeners) {
                    listener.callback(self);
                }
            }            
        });
        
        exitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stage stage = (Stage) exitButton.getScene().getWindow();
                stage.close();
            }
        });
        
    }
    
    public void addImportListener(ControllerListener listener) {
        importListeners.add(listener);
    }
    
    public void addViewListener(ControllerListener listener) {
        viewListeners.add(listener);
    }
    
    public List<File> getFile(){
        return fileArray;
    }
}
