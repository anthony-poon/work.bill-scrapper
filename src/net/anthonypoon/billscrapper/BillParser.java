/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.anthonypoon.billscrapper;

/**
 *
 * @author anthony.poon
 */
public interface BillParser {
    PhoneSummaryData returnData = new PhoneSummaryData();
    public void feedText(String str);
}
