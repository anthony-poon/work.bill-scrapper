/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.anthonypoon.billscrapper.gui.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import net.anthonypoon.billscrapper.database.DatabaseConnector;
import net.anthonypoon.billscrapper.database.DbReader;
import net.anthonypoon.billscrapper.gui.table.BillSummaryTableFactory;

/**
 *
 * @author anthony.poon
 */
public class ViewRecordController implements Initializable, Controller{

    @FXML private TableView billSummaryTable;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            DatabaseConnector connector = new DatabaseConnector();
            DbReader reader = new DbReader(connector.getConnection());
            BillSummaryTableFactory.processTableView(billSummaryTable, reader.getBillSummary());
        } catch (IOException ex) {
            Logger.getLogger(ViewRecordController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ViewRecordController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
