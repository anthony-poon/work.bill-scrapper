/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.anthonypoon.bill_scrapper;

import java.lang.reflect.Field;

/**
 *
 * @author anthony.poon
 */
public class IddDetailData {
    private String dateTimeString;
    private String location;
    private String phoneNumber;
    private Float minute;
    private Float amount;
    
    IddDetailData(String dateTimeString, String location, String phoneNumber, String minute, String amount) {
        this.dateTimeString = dateTimeString;
        this.location = location;
        this.phoneNumber = phoneNumber;
        this.minute = Float.parseFloat(minute);
        this.amount = Float.parseFloat(amount);
    }

    public String getDateTimeString() {
        return dateTimeString;
    }

    public String getLocation() {
        return location;
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
