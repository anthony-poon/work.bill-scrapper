/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.anthonypoon.bill_scrapper.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.anthonypoon.bill_scrapper.BillSummaryData;
import net.anthonypoon.bill_scrapper.IddDetailData;
import net.anthonypoon.bill_scrapper.PhoneDetailData;
import net.anthonypoon.bill_scrapper.PhoneSummaryData;
import net.anthonypoon.bill_scrapper.RoamingDetailData;

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
    
    public void insertBillSummary(BillSummaryData data) throws SQLException, ParseException {
        PreparedStatement statement = db.prepareStatement("INSERT INTO monthly_bill "
                + "SET billing_date = ?, "
                + "account_number=?, "
                + "previous_balance=?, "
                + "service_fee=?, "
                + "local_call_fee=?, "
                + "idd_fee=?, "
                + "roaming_voice_fee=?, "
                + "roaming_data_fee=?, "
                + "volume_discount=?, "
                + "vas_fee=?, "
                + "current_amount=?", Statement.RETURN_GENERATED_KEYS);
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
    
    public void insertPhoneSummary(PhoneSummaryData dataObj) throws SQLException {
        Map<String, Float> allData = dataObj.getData();
        PreparedStatement statement = db.prepareStatement("INSERT INTO phone_summary "
                + "SET bill_uid =?, "
                + "phone_number=?, "
                + "amount=? ", Statement.RETURN_GENERATED_KEYS);
        for (Map.Entry<String, Float> data : allData.entrySet()) {
            statement.setInt(1, uid);
            statement.setString(2, data.getKey());
            statement.setFloat(3, data.getValue());
            statement.addBatch();
            //System.out.println(statement);
        }
        statement.executeBatch();
    }
    
    public void insertPhoneDetail(Map<String, PhoneDetailData> dataObjMap) throws SQLException, ParseException {
        PreparedStatement statement = db.prepareStatement("INSERT INTO phone_detail "
                + "SET phone_number =?, "
                + "bill_uid=?, "
                + "fixed_monthly_fee=?, "
                + "idd_fee=?, "
                + "roaming_voice_fee=?, "
                + "roaming_data_fee=?, "
                + "daypass_fee=?, "
                + "daypass_count=?, "
                + "idd_roaming_discount=?, "
                + "regex_sum=?, "
                + "idd_regex_sum=?, "
                + "is_check_sum_ok=?, "
                + "is_idd_check_sum_ok=?, "
                + "is_roaming_check_sum_ok=?", Statement.RETURN_GENERATED_KEYS);
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
    
    public void insertIddDetail(Map<String, List<IddDetailData>> dataObjMap) throws SQLException, ParseException {
        for (Map.Entry<String, List<IddDetailData>> dataObj : dataObjMap.entrySet()) {
            PreparedStatement statement = db.prepareStatement("INSERT INTO idd_detail "
                + "SET bill_uid=?, "
                + "phone_number=?, "
                + "timestamp=?, "
                + "location=?, "
                + "phone_number_called=?, "
                + "minute=?, "
                + "amount=?", Statement.RETURN_GENERATED_KEYS);
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
    
    public void insertRoamingDetail(Map<String, List<RoamingDetailData>> dataObjMap) throws SQLException, ParseException {
        for (Map.Entry<String, List<RoamingDetailData>> dataObj : dataObjMap.entrySet()) {
            PreparedStatement statement = db.prepareStatement("INSERT INTO roaming_detail "
                + "SET bill_uid=?, "
                + "phone_number=?, "
                + "timestamp=?, "
                + "location=?, "
                + "remark=?, "
                + "minute=?, "
                + "amount=?", Statement.RETURN_GENERATED_KEYS);
            String phoneNumber = dataObj.getKey();
            List<RoamingDetailData> allIddEntry = dataObj.getValue();
            for (RoamingDetailData iddEntry : allIddEntry) {
                statement.setInt(1, uid);
                statement.setString(2, phoneNumber);
                statement.setString(3, iddEntry.getDateTimeAsSqlString());
                statement.setString(4, iddEntry.getLocation());
                statement.setString(5, iddEntry.getRemark());
                statement.setFloat(6, iddEntry.getMinute());
                statement.setFloat(7, iddEntry.getAmount());
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }
}
