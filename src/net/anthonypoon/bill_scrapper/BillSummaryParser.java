/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.anthonypoon.bill_scrapper;

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
public class BillSummaryParser implements BillParser {
    private enum MatchType {
        BILLING_DATE,
        ACCOUNT_NO,
        SERVICE_FEE,
        LOCAL_CALL_FEE,
        IDD_FEE,
        ROAMING_VOICE_FEE,
        ROAMING_DATA_FEE,
        TOTAL_VOLUME_DISCOUNT,
        VAS,
        PREVIOUS_BALANCE,
        CURRENT_AMOUNT
    }
    private List<String> textCache = new ArrayList();
    private Map<MatchType, Pattern> patternMap = new HashMap();
    private BillSummaryData returnData = new BillSummaryData();
    
    @Override
    public void feedText(String str) {
        textCache.add(str);
        patternMap.put(MatchType.BILLING_DATE, Pattern.compile("(?i)^billing date\\s+:\\s+(\\d\\d/\\d\\d/\\d\\d)"));
        patternMap.put(MatchType.ACCOUNT_NO, Pattern.compile("(?i)^account no\\.\\s+:\\s+(\\d+)"));
        patternMap.put(MatchType.PREVIOUS_BALANCE, Pattern.compile("(?i)^previous balance ([\\d\\.]+)"));
        patternMap.put(MatchType.SERVICE_FEE, Pattern.compile("(?i)^service fees ([\\d\\.]+)"));
        patternMap.put(MatchType.LOCAL_CALL_FEE, Pattern.compile("(?i)^thereafter local calls ([\\d\\.]+)"));
        patternMap.put(MatchType.IDD_FEE, Pattern.compile("(?i)^idd/infoline calls ([\\d\\.]+)"));
        patternMap.put(MatchType.ROAMING_VOICE_FEE, Pattern.compile("(?i)^global roaming charges - voice ([\\d\\.]+)"));
        patternMap.put(MatchType.ROAMING_DATA_FEE, Pattern.compile("(?i)^global roaming charges - data ([\\d\\.]+)"));
        patternMap.put(MatchType.TOTAL_VOLUME_DISCOUNT, Pattern.compile("(?i)^total volume discount ([\\d\\.]+)"));
        patternMap.put(MatchType.VAS, Pattern.compile("(?i)^value added services ([\\d\\.]+)"));
        patternMap.put(MatchType.CURRENT_AMOUNT, Pattern.compile("(?i)^current amount \\$([\\d\\.]+)"));
        Iterator it = patternMap.entrySet().iterator();
        boolean hasMatch = false;
        while(it.hasNext() && !(hasMatch)) {
            Map.Entry pair = (Map.Entry<String, Pattern>)it.next();
            Pattern pattern = (Pattern) pair.getValue();
            Matcher regex = pattern.matcher(str);
            if (regex.find()) {
                hasMatch = true;
                MatchType matchType = (MatchType) pair.getKey();
                switch (matchType) {
                    case BILLING_DATE:
                        returnData.setBillingDate(regex.group(1));                        
                        break;
                    case ACCOUNT_NO:
                        returnData.setAccNumber(regex.group(1));
                        break;
                    case PREVIOUS_BALANCE:
                        returnData.setPreviousBalance(regex.group(1));
                        break;
                    case SERVICE_FEE:
                        returnData.setServiceFee(regex.group(1));
                        break;
                    case LOCAL_CALL_FEE:
                        returnData.setLocalCallFee(regex.group(1));
                        break;
                    case IDD_FEE:
                        returnData.setIddFee(regex.group(1));
                        break;
                    case ROAMING_VOICE_FEE:
                        returnData.setRoamingVoiceFee(regex.group(1));
                        break;
                    case ROAMING_DATA_FEE:
                        returnData.setRoamingDataFee(regex.group(1));
                        break;
                    case TOTAL_VOLUME_DISCOUNT:
                        returnData.setVolumnDiscount(regex.group(1));
                        break;
                    case VAS:
                        returnData.setVas(regex.group(1));
                        break;
                    case CURRENT_AMOUNT:
                        returnData.setCurrentAmount(regex.group(1));
                        break;
                }
            }
        }
    }
    
    public void dump() {
        returnData.dump();
    }

    public BillSummaryData getData() {
        return returnData;
    }
}
