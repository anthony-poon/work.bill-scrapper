/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.anthonypoon.billscrapper.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import net.anthonypoon.billscrapper.BillSummaryData;

/**
 *
 * @author anthony.poon
 */
public class DbReader {
    private Connection db;
    public DbReader(Connection db) {
        this.db = db;
    }
    public List<BillSummaryData> getBillSummary() throws SQLException{
        List<BillSummaryData> dataArray = new ArrayList<>();
        PreparedStatement statement = db.prepareStatement("SELECT billing_date, "
                + "account_number, previous_balance, service_fee, local_call_fee, "
                + "idd_fee, roaming_voice_fee, roaming_data_fee, volume_discount, vas_fee, current_amount "
                + "FROM monthly_bill");
        statement.executeQuery();
        ResultSet results = statement.getResultSet();
        while (results.next()) {
            BillSummaryData data = new BillSummaryData();
            data.setBillingDate(results.getString("billing_date"));
            data.setAccNumber(results.getString("account_number"));
            data.setPreviousBalance(results.getString("previous_balance"));
            data.setServiceFee(results.getString("service_fee"));
            data.setLocalCallFee(results.getString("local_call_fee"));
            data.setIddFee(results.getString("idd_fee"));
            data.setRoamingVoiceFee(results.getString("roaming_voice_fee"));
            data.setRoamingDataFee(results.getString("roaming_data_fee"));
            data.setVolumnDiscount(results.getString("volume_discount"));
            data.setVas(results.getString("vas_fee"));
            data.setCurrentAmount(results.getString("current_amount"));
            dataArray.add(data);
        }
        return dataArray;
    }
}
