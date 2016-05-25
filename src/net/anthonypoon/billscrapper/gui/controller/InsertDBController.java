/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.anthonypoon.billscrapper.gui.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSpinner;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.anthonypoon.billscrapper.Bill;
import net.anthonypoon.billscrapper.database.DatabaseConnector;
import net.anthonypoon.billscrapper.database.DbWriter;
import net.anthonypoon.billscrapper.gui.ThemeFactory;

/**
 * FXML Controller class
 *
 * @author anthony.poon
 */
public class InsertDBController implements Initializable, SimpleThreadListener {

    @FXML private VBox mainContainer;
    @FXML private Label msgLabel;
    @FXML private Button cancelButton;
    @FXML private JFXSpinner spinner;
    double x = 0;
    double y = 0;
    double orgX = 0;  
    double orgY = 0;
    private List<Bill> billObjs = new ArrayList<>();
    private InsertThread thread;
    private int billsCount = 0;
    private int currentCount = 0;
    class InsertThread extends Thread{
        private List<Bill> billObjs;
        private List<SimpleThreadListener> listeners = new ArrayList<>();
        public InsertThread(List<Bill> objs) {
            billObjs = objs;
        }
        
        
        @Override
        public void run() {
            insertBills();
        }
        
        private void insertBills(){
            try {
                DatabaseConnector db = new DatabaseConnector();
                DbWriter writer = new DbWriter(db.getConnection());
                for (Bill bill : billObjs) {
                    boolean isWritten = writer.insertDetail(bill.getBillSummary(), bill.getPhoneSummaryData(), bill.getPhoneDetail());
                    writer.commit();
                    for (SimpleThreadListener listener : listeners) {
                        listener.handle();
                    }
                }
                for (SimpleThreadListener listener : listeners) {
                    listener.finish();
                }
            } catch (IOException ex) {
                Logger.getLogger(InsertDBController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(InsertDBController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ParseException ex) {
                Logger.getLogger(InsertDBController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public void addListener(SimpleThreadListener handler){
            listeners.add(handler);
        }

    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {        
        cancelButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stage stage = (Stage) cancelButton.getScene().getWindow();
                stage.close();
            }
        });
        spinner.setVisible(true);
        
    }    
    
    public void setBillObjs(List<Bill> billObjs) {
        billsCount = billObjs.size();
        thread = new InsertThread(billObjs);
        thread.addListener(this);
    }
    
    public void startThread() {
        thread.start();
    }
    
    @Override
    public synchronized void handle() {
        currentCount += 1;
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                msgLabel.setText("Writing to database " + currentCount + "/" + billsCount);
            }            
        });
        
    }
    
    @Override
    public void finish() {
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                msgLabel.setText("Finish writing to database");
                cancelButton.setText("Close");
                spinner.setVisible(false);
            }            
        });        
    }
}
