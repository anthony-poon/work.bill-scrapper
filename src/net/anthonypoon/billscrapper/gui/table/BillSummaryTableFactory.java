/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.anthonypoon.billscrapper.gui.table;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import net.anthonypoon.billscrapper.BillSummaryData;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import net.anthonypoon.billscrapper.gui.controller.BrowsePDFController;
/**
 *
 * @author anthony.poon
 */
public class BillSummaryTableFactory {
    
    public static void processTableView(TableView<BillRow> table, List<BillSummaryData> dataArray) {
        table.setEditable(true);
        ObservableList<BillRow> rows = FXCollections.observableArrayList();
        for (BillSummaryData data : dataArray) {
            rows.add(new BillRow(data));
        }
        table.setItems(rows);
        TableColumn<BillRow, String> billDate = new TableColumn("Bill Date");
        billDate.setCellValueFactory(new PropertyValueFactory<>("billDate"));
        billDate.setComparator(new Comparator<String>() {            
            @Override            
            public int compare(String t, String t1) {
                try {
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy");
                    Date d1 = format.parse(t);                    
                    Date d2 = format.parse(t1);
                    return Long.compare(d1.getTime(), d2.getTime());
                } catch (ParseException p) {
                    p.printStackTrace();
                }
                return -1;                
            }            
        });        
        
        TableColumn prevBalance = new TableColumn("Previous Balance");
        prevBalance.setCellValueFactory(new PropertyValueFactory<>("prevBalance"));
        
        TableColumn currBalance = new TableColumn("Current Balance");
        currBalance.setCellValueFactory(new PropertyValueFactory<>("currBalance"));
        
        TableColumn serviceFee = new TableColumn("Service Fee");
        serviceFee.setCellValueFactory(new PropertyValueFactory<>("serviceFee"));
        
        TableColumn localCallFee = new TableColumn("Local Call");
        localCallFee.setCellValueFactory(new PropertyValueFactory<>("localCallFee"));
        
        TableColumn iddFee = new TableColumn("IDD");
        iddFee.setCellValueFactory(new PropertyValueFactory<>("iddFee"));
        
        TableColumn roamingVoiceFee = new TableColumn("Roaming Voice");
        roamingVoiceFee.setCellValueFactory(new PropertyValueFactory<>("roamingVoiceFee"));
        
        TableColumn roamingDataFee = new TableColumn("Roaming Data");
        roamingDataFee.setCellValueFactory(new PropertyValueFactory<>("roamingDataFee"));
        
        TableColumn volumnDiscount = new TableColumn("Volumn Discount");
        volumnDiscount.setCellValueFactory(new PropertyValueFactory<>("volumnDiscount"));
        
        TableColumn vas = new TableColumn("VAS");
        vas.setCellValueFactory(new PropertyValueFactory<>("vas"));
        
        TableColumn currentAmount = new TableColumn("Current Amount");
        currentAmount.setCellValueFactory(new PropertyValueFactory<>("currentAmount"));
        
        table.getColumns().addAll(billDate, prevBalance, currBalance,
                serviceFee, localCallFee, iddFee, roamingVoiceFee, roamingDataFee,
                volumnDiscount, vas, currentAmount);
    }
    
    public static class BillRow {

        private SimpleStringProperty billDate;
        private SimpleFloatProperty prevBalance;
        private SimpleFloatProperty currBalance;
        private SimpleFloatProperty serviceFee;
        private SimpleFloatProperty localCallFee;
        private SimpleFloatProperty iddFee;
        private SimpleFloatProperty roamingVoiceFee;
        private SimpleFloatProperty roamingDataFee;
        private SimpleFloatProperty volumnDiscount;
        private SimpleFloatProperty vas;
        private SimpleFloatProperty currentAmount;
        
        public BillRow(BillSummaryData data) {
            billDate = new SimpleStringProperty(data.getBillingDate());
            prevBalance = new SimpleFloatProperty(data.getPreviousBalance());
            currBalance = new SimpleFloatProperty(data.getCurrentAmount());
            serviceFee = new SimpleFloatProperty(data.getServiceFee());
            localCallFee = new SimpleFloatProperty(data.getLocalCallFee());
            iddFee = new SimpleFloatProperty(data.getIddFee());
            roamingDataFee = new SimpleFloatProperty(data.getRoamingDataFee());
            roamingVoiceFee = new SimpleFloatProperty(data.getRoamingVoiceFee());
            volumnDiscount = new SimpleFloatProperty(data.getVolumnDiscount());
            vas = new SimpleFloatProperty(data.getVas());
            currentAmount = new SimpleFloatProperty(data.getCurrentAmount());
        }
        
        public String getBillDate() {
            return billDate.get();
        }
        
        public float getPrevBalance() {
            return prevBalance.get();
        }
        
        public float getCurrBalance() {
            return currBalance.get();
        }
        
        public float getServiceFee() {
            return serviceFee.get();
        }

        public float getLocalCallFee() {
            return localCallFee.get();
        }
        
        public float getIddFee() {
            return iddFee.get();
        }
        
        public float getRoamingVoiceFee() {
            return roamingVoiceFee.get();
        }
        
        public float getRoamingDataFee() {
            return roamingDataFee.get();
        }
        
        public float getVolumnDiscount() {
            return volumnDiscount.get();
        }
        
        public float getVas() {
            return vas.get();
        }
        
        public float getCurrentAmount() {
            return currentAmount.get();
        }        
    }
}
