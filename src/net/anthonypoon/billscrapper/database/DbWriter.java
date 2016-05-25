/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.anthonypoon.billscrapper.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.anthonypoon.billscrapper.BillSummaryData;
import net.anthonypoon.billscrapper.IddDetailData;
import net.anthonypoon.billscrapper.PhoneDetailData;
import net.anthonypoon.billscrapper.PhoneSummaryData;
import net.anthonypoon.billscrapper.RoamingDetailData;

/**
 *
 * @author anthony.poon
 */
public class DbWriter {
    private Connection db;
    private int uid;
    private Map<String, List<IddDetailData>> iddDataList = new HashMap();
    private Map<String, List<RoamingDetailData>> roamingDataList = new HashMap();
    public DbWriter(Connection db) throws SQLException {
        this.db = db;
        
    }
    
    public boolean insertDetail(BillSummaryData billSummary, PhoneSummaryData phoneSummary, Map<String, PhoneDetailData> phoneDetail) throws SQLException, ParseException {
        PreparedStatement statement = db.prepareStatement("SELECT billing_date "
                + "FROM monthly_bill "
                + "WHERE (billing_date = ?)");
        statement.setDate(1, new Date(billSummary.getBillingDateAsObj().getTime()));
        ResultSet result = statement.executeQuery();
        if (!result.next()) {
            this.db.setAutoCommit(false);
            insertBillSummary(billSummary);
            insertPhoneSummary(phoneSummary);
            insertPhoneDetail(phoneDetail);
            return true;
        } else {
            return false;
        }
    }
    
    private void insertBillSummary(BillSummaryData data) throws SQLException, ParseException {
        PreparedStatement statement = db.prepareStatement("INSERT INTO monthly_bill "
                + "(billing_date, account_number, previous_balance, service_fee, local_call_fee, "
                + "idd_fee, roaming_voice_fee, roaming_data_fee, volume_discount, vas_fee, current_amount) "
                + "VALUES "
                + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        statement.setDate(1, new Date(data.getBillingDateAsObj().getTime()));
        statement.setString(2, data.getAccNumber());
        statement.setFloat(3, data.getPreviousBalance());
        statement.setFloat(4, data.getServiceFee());
        statement.setFloat(5, data.getLocalCallFee());
        statement.setFloat(6, data.getIddFee());
        statement.setFloat(7, data.getRoamingVoiceFee());
        statement.setFloat(8, data.getRoamingDataFee());
        statement.setFloat(9, data.getVolumnDiscount());
        statement.setFloat(10, data.getVas());
        statement.setFloat(11, data.getCurrentAmount());
        statement.executeUpdate();
        ResultSet result = statement.getGeneratedKeys();
        result.next();
        uid = result.getInt(1);
    }    
    
    private void insertPhoneSummary(PhoneSummaryData dataObj) throws SQLException {
        Map<String, Float> allData = dataObj.getData();
        PreparedStatement statement = db.prepareStatement("INSERT INTO phone_summary "
                + "(bill_uid, phone_number, amount) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        for (Map.Entry<String, Float> data : allData.entrySet()) {
            statement.setInt(1, uid);
            statement.setString(2, data.getKey());
            statement.setFloat(3, data.getValue());
            statement.addBatch();
            //System.out.println(statement);
        }
        statement.executeBatch();
    }
    
    private void insertPhoneDetail(Map<String, PhoneDetailData> dataObjMap) throws SQLException, ParseException {
        PreparedStatement statement = db.prepareStatement("INSERT INTO phone_detail "
                + "(phone_number, bill_uid, fixed_monthly_fee, idd_fee, roaming_voice_fee, "
                + "roaming_data_fee, daypass_fee, daypass_count, idd_roaming_discount, "
                + "regex_sum, idd_regex_sum, is_check_sum_ok, is_idd_check_sum_ok, is_roaming_check_sum_ok) VALUES "
                + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        for (Map.Entry<String, PhoneDetailData> dataObj : dataObjMap.entrySet()) {
            statement.setString(1, dataObj.getKey());
            statement.setInt(2, uid);
            statement.setFloat(3, dataObj.getValue().getFixedMonthly());
            statement.setFloat(4, dataObj.getValue().getIddFee());
            statement.setFloat(5, dataObj.getValue().getRoamingVoiceFee());
            statement.setFloat(6, dataObj.getValue().getRoamingDataFee());
            statement.setFloat(7, dataObj.getValue().getDayPassFee());
            statement.setInt(8, dataObj.getValue().getDayPassCount());
            statement.setFloat(9, dataObj.getValue().getIddRoamingDiscount());
            statement.setFloat(10, dataObj.getValue().getRegexSum());
            statement.setFloat(11, dataObj.getValue().getIddRegexSum());
            statement.setBoolean(12, dataObj.getValue().checkSumCharge());
            statement.setBoolean(13, dataObj.getValue().checkSumIdd());
            statement.setBoolean(14, dataObj.getValue().checkSumRoamingDetail());
            statement.addBatch();
            if (dataObj.getValue().getIddDetail() != null) {
                iddDataList.put(dataObj.getValue().getPhoneNumber(), dataObj.getValue().getIddDetail());
            }
            
            if (dataObj.getValue().getIddDetail() != null) {
                roamingDataList.put(dataObj.getValue().getPhoneNumber(), dataObj.getValue().getRoamingDetail());
            }
        }
        statement.executeBatch();
        
        insertIddDetail(iddDataList);
        insertRoamingDetail(roamingDataList);
    }
    
    private void insertIddDetail(Map<String, List<IddDetailData>> dataObjMap) throws SQLException, ParseException {
        for (Map.Entry<String, List<IddDetailData>> dataObj : dataObjMap.entrySet()) {
            PreparedStatement statement = db.prepareStatement("INSERT INTO idd_detail "
                    + "(bill_uid, phone_number, timestamp, location, phone_number_called, minute, amount) VALUES "
                    + "(?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            String phoneNumber = dataObj.getKey();
            List<IddDetailData> allIddEntry = dataObj.getValue();
            for (IddDetailData iddEntry : allIddEntry) {
                statement.setInt(1, uid);
                statement.setString(2, phoneNumber);
                statement.setString(3, iddEntry.getDateTimeAsSqlString());
                statement.setString(4, iddEntry.getLocation());
                statement.setString(5, iddEntry.getPhoneNumberCalled());
                statement.setFloat(6, iddEntry.getMinute());
                statement.setFloat(7, iddEntry.getAmount());
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }
    
    private void insertRoamingDetail(Map<String, List<RoamingDetailData>> dataObjMap) throws SQLException, ParseException {
        for (Map.Entry<String, List<RoamingDetailData>> dataObj : dataObjMap.entrySet()) {
            PreparedStatement statement = db.prepareStatement("INSERT INTO roaming_detail "
                    + "(bill_uid, phone_number, timestamp, location, remark, roaming_type, minute, amount) VALUES "
                    + "(?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            String phoneNumber = dataObj.getKey();
            List<RoamingDetailData> allIddEntry = dataObj.getValue();
            for (RoamingDetailData iddEntry : allIddEntry) {
                statement.setInt(1, uid);
                statement.setString(2, phoneNumber);
                statement.setString(3, iddEntry.getDateTimeAsSqlString());
                statement.setString(4, iddEntry.getLocation());
                statement.setString(5, iddEntry.getRemark());
                String roamingType = "roaming_inbound_call";
                Matcher roamingDataRegex = Pattern.compile("^\\d+KB$").matcher(iddEntry.getRemark());
                Matcher roamingOutboundRegex = Pattern.compile("^\\d+$").matcher(iddEntry.getRemark());
                if (roamingDataRegex.find()) {
                    roamingType = "roaming_data";
                } else if (roamingOutboundRegex.find()) {
                    roamingType = "roaming_outbound_call";
                }
                statement.setString(6, roamingType);
                statement.setFloat(7, iddEntry.getMinute());
                statement.setFloat(8, iddEntry.getAmount());
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }
    
    public void commit() throws SQLException {
        if (!db.getAutoCommit()) {
            db.commit();
            db.setAutoCommit(true);
        }
    }
}
