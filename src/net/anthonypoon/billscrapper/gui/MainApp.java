/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.anthonypoon.billscrapper.gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.sound.midi.ShortMessage;
import net.anthonypoon.billscrapper.gui.controller.BrowsePDFController;
import net.anthonypoon.billscrapper.gui.controller.MainController;
import net.anthonypoon.billscrapper.gui.controller.Controller;
import net.anthonypoon.billscrapper.gui.controller.ControllerListener;
import net.anthonypoon.billscrapper.gui.controller.ViewRecordController;

/**
 *
 * @author anthony.poon
 */
public class MainApp extends Application{
    private Stage primaryStage;
    private FXMLLoader loader;
    
    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;
        loader = new FXMLLoader(getClass().getResource("/net/anthonypoon/billscrapper/gui/Main.fxml"));      
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
        primaryStage.show();
        registerLoadPDFCallback();
        registerViewRecordCallback();
    }
    
    private void registerLoadPDFCallback() {
        MainController rootController = (MainController) loader.getController();
        rootController.addImportListener(new ControllerListener() {            
            @Override
            public void callback(Controller controller) {
                try {
                    MainController mainController = (MainController) controller;
                    loader = new FXMLLoader(getClass().getResource("/net/anthonypoon/billscrapper/gui/BrowsePDF.fxml"));
                    Scene scene = new Scene(loader.load());
                    BrowsePDFController browseController = loader.getController();
                    browseController.addPDF(mainController.getFile());
                    primaryStage.setScene(scene);
                    primaryStage.show();
                } catch (IOException ex) {
                    Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
    
    private void registerViewRecordCallback() {
        MainController rootController = (MainController) loader.getController();
        rootController.addViewListener(new ControllerListener() {            
            @Override
            public void callback(Controller controller) {
                try {
                    loader = new FXMLLoader(getClass().getResource("/net/anthonypoon/billscrapper/gui/ViewRecord.fxml"));
                    Scene scene = new Scene(loader.load());
                    primaryStage.setScene(scene);
                    primaryStage.show();
                } catch (IOException ex) {
                    Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
    public static void main(String[] args) {
        launch(args);
    }
}
