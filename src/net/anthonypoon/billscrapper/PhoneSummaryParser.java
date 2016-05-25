/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.anthonypoon.billscrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 *
 * @author anthony.poon
 */
public class PhoneSummaryParser implements BillParser{
    private List<String> textCache = new ArrayList();
    private PhoneSummaryData returnData = new PhoneSummaryData();

    @Override
    public void feedText(String str) {
        textCache.add(str);
        Pattern pattern = Pattern.compile("(?i)^(\\d+) \\w+ ([\\d\\.]+)");
        Matcher regex = pattern.matcher(str);
        if (regex.find()) {
            returnData.addEntry(regex.group(1), regex.group(2));
        }        
    }
    
    public void dump() {
        returnData.dump();
    }
    
    public PhoneSummaryData getData() {
        return returnData;
    }
}
