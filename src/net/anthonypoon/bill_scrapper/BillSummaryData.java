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
import java.util.Date;

/**
 *
 * @author anthony.poon
 */
public class BillSummaryData{
    private String billingDate;
    private String accNumber;
    private float previousBalance;
    private float serviceFee;
    private float localCallFee;
    private float iddFee;
    private float roamingVoiceFee;
    private float roamingDataFee;
    private float volumeDiscount;
    private float vas;
    private float currentAmount;
    
    public float getPreviousBalance() {
        return previousBalance;
    }

    public void setPreviousBalance(String previousBalance) {
        this.previousBalance = Float.parseFloat(previousBalance);
    }

    public float getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(String currentAmount) {
        this.currentAmount = Float.parseFloat(currentAmount);
    }
    

    public String getBillingDate() {
        return billingDate;
    }
    
    public Date getBillingDateAsObj() throws ParseException {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yy");
        return formatter.parse(billingDate);
    }

    public void setBillingDate(String BillingDate) {
        this.billingDate = BillingDate;
    }

    public String getAccNumber() {
        return accNumber;
    }

    public void setAccNumber(String AccNumber) {
        this.accNumber = AccNumber;
    }

    public float getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(String serviceFee) {
        this.serviceFee = Float.parseFloat(serviceFee);
    }

    public float getLocalCallFee() {
        return localCallFee;
    }

    public void setLocalCallFee(String localCallFee) {
        this.localCallFee = Float.parseFloat(localCallFee);
    }

    public float getIddFee() {
        return iddFee;
    }

    public void setIddFee(String iddFee) {
        this.iddFee = Float.parseFloat(iddFee);
    }

    public float getRoamingVoiceFee() {
        return roamingVoiceFee;
    }

    public void setRoamingVoiceFee(String roamingVoiceFee) {
        this.roamingVoiceFee = Float.parseFloat(roamingVoiceFee);
    }

    public float getRoamingDataFee() {
        return roamingDataFee;
    }

    public void setRoamingDataFee(String roamingDataFee) {
        this.roamingDataFee = Float.parseFloat(roamingDataFee);
    }

    public float getVolumnDiscount() {
        return volumeDiscount;
    }

    public void setVolumnDiscount(String volumnDiscount) {
        this.volumeDiscount = Float.parseFloat(volumnDiscount);
    }

    public float getVas() {
        return vas;
    }

    public void setVas(String vas) {
        this.vas = Float.parseFloat(vas);
    }
    
    public void dump() {
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
