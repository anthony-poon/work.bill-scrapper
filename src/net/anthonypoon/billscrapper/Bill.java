/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.anthonypoon.billscrapper;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author anthony.poon
 */
public class Bill {
    private BillSummaryData billSummary;
    private PhoneSummaryData phoneSummaryData;
    private Map<String, PhoneDetailData> phoneDetail = new HashMap();

    public BillSummaryData getBillSummary() {
        return billSummary;
    }

    public void setBillSummary(BillSummaryData billSummary) {
        this.billSummary = billSummary;
    }

    public PhoneSummaryData getPhoneSummaryData() {
        return phoneSummaryData;
    }

    public void setPhoneSummaryData(PhoneSummaryData phoneSummaryData) {
        this.phoneSummaryData = phoneSummaryData;
    }

    public Map<String, PhoneDetailData> getPhoneDetail() {
        return phoneDetail;
    }

    public void setPhoneDetail(Map<String, PhoneDetailData> phoneDetail) {
        this.phoneDetail = phoneDetail;
    }
}
