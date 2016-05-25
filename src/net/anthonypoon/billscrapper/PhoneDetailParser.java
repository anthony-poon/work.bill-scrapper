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
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 *
 * @author anthony.poon
 */
public class PhoneDetailParser implements BillParser{
    private enum MatchType {
        PHONE_NUMBER,
        FIXED_MONTHLY,
        LOCAL_CALL_FEE,
        IDD_FEE,
        ROAMING_VOICE_FEE,
        ROAMING_DATA_FEE,
        INT_SMS_FEE,
        DAY_PASS,
        INT_ROAMING_SMS_FEE,
        ROAMING_SMS,
        IDD_ROAMING_DISCOUNT,
        INTERNETWORK_SMS,
        MMS,
        CURRENT_CHARGES_SECTION_START_FLAG,
        CURRENT_CHARGES_SECTION_END_FLAG,
        IDD_DETAIL_SECTION_START_FLAG,
        IDD_DETAIL_ENTRY,
        IDD_DETAIL_TOTAL,
        ROAMING_DETAIL_SECTION_START_FLAG,
        ROAMING_DETAIL_ENTRY,
        ROAMING_DETAIL_COUNTRY_FLAG,
        ROAMING_DETAIL_NURMERIC,
        NULL_SECTION_FLAG
    }
    private List<String> textCache = new ArrayList();
    private PhoneDetailData currentPhoneDataObj;
    private Map<MatchType, Pattern> patternMap = new HashMap();
    private Map<MatchType, Pattern> currentChargePatternMap = new HashMap();
    private Map<MatchType, Pattern> iddDetailPatternMap = new HashMap();
    private Map<MatchType, Pattern> roamingDetailPatternMap = new HashMap();
    private Map<String, PhoneDetailData> returnData = new HashMap();
    MatchType sectionFlag = MatchType.NULL_SECTION_FLAG;
    public PhoneDetailParser() {
        patternMap.put(MatchType.CURRENT_CHARGES_SECTION_START_FLAG, Pattern.compile("(?i)^current charges$"));
        patternMap.put(MatchType.CURRENT_CHARGES_SECTION_END_FLAG, Pattern.compile("(?i)^local call charges"));
        patternMap.put(MatchType.IDD_DETAIL_SECTION_START_FLAG, Pattern.compile("(?i)^idd/infoline calls details"));
        patternMap.put(MatchType.ROAMING_DETAIL_SECTION_START_FLAG, Pattern.compile("(?i)^global roaming charges details$"));
        patternMap.put(MatchType.PHONE_NUMBER, Pattern.compile("(?i)^mobile no\\.\\s+:\\s+(\\d+)"));
        currentChargePatternMap.put(MatchType.FIXED_MONTHLY, Pattern.compile("(?i)^advance service charge[\\s\\w\\(\\)]* [\\d/-]+ ([\\d\\.]+)"));
        currentChargePatternMap.put(MatchType.IDD_FEE, Pattern.compile("(?i)^idd/infoline calls ([\\d\\.]+)"));
        currentChargePatternMap.put(MatchType.ROAMING_VOICE_FEE, Pattern.compile("(?i)^global roaming charges - voice ([\\d\\.]+)"));
        currentChargePatternMap.put(MatchType.ROAMING_DATA_FEE, Pattern.compile("(?i)^global roaming charges - data ([\\d\\.]+)"));
        currentChargePatternMap.put(MatchType.DAY_PASS, Pattern.compile("(?i)^dataroam day pass usage chg\\s+\\(\\s+(\\d+)\\s+daypass\\) ([\\d\\.]+)"));
        currentChargePatternMap.put(MatchType.IDD_ROAMING_DISCOUNT, Pattern.compile("(?i)^idd/roaming volume discount ([\\d\\.]+)"));
        iddDetailPatternMap.put(MatchType.IDD_DETAIL_ENTRY, Pattern.compile("(?i)^(\\d\\d/\\d\\d/\\d\\d \\d\\d:\\d\\d) \\d{3} (\\D+) (\\d+) ([\\d\\.]+) ([\\d\\.]+)$"));
        iddDetailPatternMap.put(MatchType.IDD_DETAIL_TOTAL, Pattern.compile("(?i)^\\$([\\d\\.]+)$"));
        roamingDetailPatternMap.put(MatchType.ROAMING_DETAIL_ENTRY, Pattern.compile("^(\\d\\d/\\d\\d/\\d\\d \\d\\d:\\d\\d).+\\s([\\d\\w]+)$"));
        roamingDetailPatternMap.put(MatchType.ROAMING_DETAIL_COUNTRY_FLAG, Pattern.compile("^[\\w/]{1,}( \\w{1,})?$"));
        roamingDetailPatternMap.put(MatchType.ROAMING_DETAIL_NURMERIC, Pattern.compile("^([\\d\\.]+) ([\\d\\.]+)$"));
    }
    
    @Override
    public void feedText(String str) {
        textCache.add(str);
        boolean hasMatch = false;
        Iterator it = patternMap.entrySet().iterator();
        while (!hasMatch && it.hasNext()) {
            Map.Entry<MatchType, Pattern> pair = (Map.Entry)it.next();
            Matcher regex = pair.getValue().matcher(str);
            
            if (regex.find()) {
                hasMatch = true;
                MatchType matchType = (MatchType) pair.getKey();                
                switch (matchType) {
                    case PHONE_NUMBER:
                        String matchedNumber = regex.group(1);
                        // Only store and create new obj when the matched number 
                        // is different then that current one since there will be 
                        // a match ever page at the start
                        if (currentPhoneDataObj != null && !matchedNumber.equals(currentPhoneDataObj.getPhoneNumber())) {
                            returnData.put(currentPhoneDataObj.getPhoneNumber(), currentPhoneDataObj);
                            currentPhoneDataObj = new PhoneDetailData(regex.group(1));
                        } else if (currentPhoneDataObj == null){
                            currentPhoneDataObj = new PhoneDetailData(regex.group(1));
                        }
                        break;
                    case CURRENT_CHARGES_SECTION_START_FLAG:
                        sectionFlag = MatchType.CURRENT_CHARGES_SECTION_START_FLAG;
                        break;
                    case CURRENT_CHARGES_SECTION_END_FLAG:
                        sectionFlag = MatchType.NULL_SECTION_FLAG;
                        break;
                    case IDD_DETAIL_SECTION_START_FLAG:
                        sectionFlag = MatchType.IDD_DETAIL_SECTION_START_FLAG;
                        break;
                    case ROAMING_DETAIL_SECTION_START_FLAG:
                        sectionFlag = MatchType.ROAMING_DETAIL_SECTION_START_FLAG;
                        break;
                }
            }
        }
        if (!hasMatch) {
            switch (sectionFlag) {
                case CURRENT_CHARGES_SECTION_START_FLAG:
                    hasMatch = currentChargeScrapping(str);
                    break;
                case IDD_DETAIL_SECTION_START_FLAG:
                    hasMatch = iddDetailScrapping(str);
                    break;
                case ROAMING_DETAIL_SECTION_START_FLAG:
                    hasMatch = roamingDetailScrapping(str);
                    break;
            }
            if (!hasMatch && sectionFlag == MatchType.CURRENT_CHARGES_SECTION_START_FLAG) {
                Pattern totalChargePattern = Pattern.compile("^([\\d\\.]+)$");
                Matcher regex = totalChargePattern.matcher(str);
                if (regex.find()) {
                    currentPhoneDataObj.setRegexSum(regex.group(1));
                } else {
                    Pattern miscChargePattern = Pattern.compile("^(?i)(.+) ([\\d\\.]+)(\\sCR)?$");
                    regex = miscChargePattern.matcher(str);
                    //System.out.println(str);
                    if (regex.find()) {
                        if (!regex.group(2).equals("0.00")) {
                            if (regex.group(3) != null) {
                                currentPhoneDataObj.addMiscCharge(regex.group(1).trim(), "-" + regex.group(2));
                            } else {
                                currentPhoneDataObj.addMiscCharge(regex.group(1).trim(), regex.group(2));
                            }
                        }
                    }
                }
            }
            //System.out.println(str);
        }
    }
    
    
    private boolean currentChargeScrapping(String str) {
        boolean hasMatch = false;
        Iterator it = currentChargePatternMap.entrySet().iterator();
        
        while (!hasMatch && it.hasNext()) {
            Map.Entry<MatchType, Pattern> pair = (Map.Entry)it.next();
            Matcher regex = pair.getValue().matcher(str); 
            if (regex.find()) {
                hasMatch = true;
                MatchType matchType = (MatchType) pair.getKey();                
                switch (matchType) {
                    case FIXED_MONTHLY:
                        currentPhoneDataObj.setFixedMonthlyFee(regex.group(1));
                        break;
                    case IDD_FEE:
                        currentPhoneDataObj.setIddFee(regex.group(1));
                        break;
                    case ROAMING_VOICE_FEE:
                        currentPhoneDataObj.setRoamingVoiceFee(regex.group(1));
                        break;
                    case ROAMING_DATA_FEE:
                        currentPhoneDataObj.setRoamingDataFee(regex.group(1));
                        break;
                    case DAY_PASS:
                        currentPhoneDataObj.setDayPass(regex.group(1), regex.group(2));
                        break;
                    case IDD_ROAMING_DISCOUNT:
                        currentPhoneDataObj.setIddRoamingDiscount(regex.group(1));
                        break;
                }
            }
        }
        
        return hasMatch;
    }
    
    private boolean iddDetailScrapping(String str) {
        boolean hasMatch = false;
        Iterator it = iddDetailPatternMap.entrySet().iterator();
        while (!hasMatch && it.hasNext()) {
            Map.Entry<MatchType, Pattern> pair = (Map.Entry)it.next();
            Matcher regex = pair.getValue().matcher(str);
            if (regex.find()) {
                hasMatch = true;
                MatchType matchType = (MatchType) pair.getKey();                
                switch (matchType) {
                    case IDD_DETAIL_ENTRY:
                        currentPhoneDataObj.addIddDetail(new IddDetailData(regex.group(1), regex.group(2), regex.group(3), regex.group(4), regex.group(5)));
                        break;
                    case IDD_DETAIL_TOTAL:
                        currentPhoneDataObj.setIddRegexSum(regex.group(1));
                        break;
                }
            }
        }
        return hasMatch;
    }
    
    String currentRoamingCountry = null;
    RoamingDetailData currentRoamingObj = null;
    private boolean roamingDetailScrapping(String str) {
        boolean hasMatch = false;
        //System.out.println(str);
        Iterator it = roamingDetailPatternMap.entrySet().iterator();
        while (!hasMatch && it.hasNext()) {
            Map.Entry<MatchType, Pattern> pair = (Map.Entry)it.next();
            Matcher regex = pair.getValue().matcher(str);
            if (regex.find()) {
                hasMatch = true;
                MatchType matchType = (MatchType) pair.getKey();                
                switch (matchType) {
                    case ROAMING_DETAIL_COUNTRY_FLAG:
                        if (currentRoamingObj == null) {
                            currentRoamingCountry = str;
                            currentRoamingObj = new RoamingDetailData(str);
                        } else {
                            currentRoamingCountry = str;
                        }
                        
                        break;
                    case ROAMING_DETAIL_ENTRY:
                        currentRoamingObj.setEntry(regex.group(1), regex.group(2));
                        break;
                    case ROAMING_DETAIL_NURMERIC:
                        currentRoamingObj.setAmount(regex.group(1), regex.group(2));
                        currentPhoneDataObj.addRoamingDetail(currentRoamingObj);
                        currentRoamingObj = new RoamingDetailData(currentRoamingCountry);
                        break;
                }
            }
        }
        if (!hasMatch) {
            //System.out.println("Non-match:\t\t" + str);
        }
        return hasMatch;
    }
    
    public void dump() {
        for (Map.Entry<String, PhoneDetailData> pair : returnData.entrySet()) {
            pair.getValue().dump();
        }
    }
    
    public Map getData() {
        return returnData;
    }
}
