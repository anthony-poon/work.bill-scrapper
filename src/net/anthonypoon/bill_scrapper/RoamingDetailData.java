/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.anthonypoon.bill_scrapper;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 *
 * @author anthony.poon
 */
public class RoamingDetailData {
    private String dateTimeString;
    private String location;
    private String remark;
    private Float minute;
    private Float amount;

    public RoamingDetailData(String location) {
        this.location = location;
    }
    
    public void setEntry(String dateTimeString, String remark) {
        this.dateTimeString = dateTimeString;
        this.remark = remark;
    }
    
    public void setAmount(String minute, String amount) {
        this.minute = Float.parseFloat(minute);
        this.amount = Float.parseFloat(amount);
    }
    
    public String getDateTimeString() {
        return dateTimeString;
    }
    
    public String getDateTimeAsSqlString() throws ParseException {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yy HH:mm");
        DateFormat outputformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return outputformat.format(formatter.parse(dateTimeString));
    }

    public String getLocation() {
        return location;
    }

    public String getRemark() {
        return remark;
    }

    public Float getMinute() {
        return minute;
    }

    public Float getAmount() {
        return amount;
    }
    
    public void dump(){
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                System.out.println(field.getName() + " -> " + field.get(this));
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }
    }
}
