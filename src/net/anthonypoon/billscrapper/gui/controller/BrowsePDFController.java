/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.anthonypoon.billscrapper.gui.controller;

import com.jfoenix.controls.JFXButton;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.anthonypoon.billscrapper.Bill;
import net.anthonypoon.billscrapper.BillSummaryData;
import net.anthonypoon.billscrapper.JavaBillScrapper;
import net.anthonypoon.billscrapper.gui.ThemeFactory;
import net.anthonypoon.billscrapper.gui.table.BillSummaryTableFactory;

/**
 *
 * @author anthony.poon
 */
public class BrowsePDFController implements Initializable, Controller {
    
    @FXML private TableView billSummaryTable;
    @FXML private JFXButton insertDBButton;
    private List<Bill> billObjs = new ArrayList<>();
    private List<BillSummaryData> billSummary = new ArrayList<>();

    public void addPDF(List<File> pdfFiles) throws IOException {
        for (File pdfFile : pdfFiles) {
            JavaBillScrapper scrapper = new JavaBillScrapper(pdfFile);
            billObjs.add(scrapper.getBill());
            billSummary.add(scrapper.getBill().getBillSummary());
        }
        BillSummaryTableFactory.processTableView(billSummaryTable, billSummary);
    }
    
    

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        insertDBButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/net/anthonypoon/billscrapper/gui/InsertDB.fxml"));            
                    Scene scene = new Scene(loader.load());
                    InsertDBController controller = loader.getController();
                    controller.setBillObjs(billObjs);
                    controller.startThread();
                    Stage stage = new Stage();                    
                    stage.setScene(scene);
                    stage.show();
                    
                } catch (IOException ex) {
                    Logger.getLogger(BrowsePDFController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

}
