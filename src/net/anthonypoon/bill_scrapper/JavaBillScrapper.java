/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.anthonypoon.bill_scrapper;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import net.anthonypoon.bill_scrapper.database.DatabaseConnector;
import net.anthonypoon.bill_scrapper.database.DbWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
/**
 *
 * @author anthony.poon
 */
public class JavaBillScrapper {
    private enum pageType {
        BILL_SUMMARY,
        PHONE_SUMMARY,
        PHONE_DETAIL,
    }
    
    private static BillSummaryData billSummary;
    private static PhoneSummaryData phoneSummary;
    private static Map<String, PhoneDetailData> phoneDetail = new HashMap();
    private static List<String> filePaths = new ArrayList<>();
    public static void main(String[] args) {
        // TODO code application logic here
        try {
            for (String arg : args) {
                if (!arg.startsWith("-")){
                    filePaths.add(arg);
                }
            }
            Collections.sort(filePaths);
            for (String filePath : filePaths) {
                System.out.println("Loading: " + filePath);
                PDDocument doc = PDDocument.load(new File(filePath));
                PDFTextStripper stripper = new PDFTextStripper();
                String rawText = stripper.getText(doc);
                String[] textArray = rawText.split("[\\r\\n]+");
                parsePdf(textArray);
                DatabaseConnector db = new DatabaseConnector();
                DbWriter writer = new DbWriter(db.getConnection());
                writer.insertBillSummary(billSummary);
                writer.insertPhoneSummary(phoneSummary);
                writer.insertPhoneDetail(phoneDetail);
                doc.close();
            }
            
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
    }
    
    private static void parsePdf (String[] tArray) {
        BillSummaryParser billSummaryParser = new BillSummaryParser();
        PhoneSummaryParser phoneSummaryParser = new PhoneSummaryParser();
        PhoneDetailParser phoneDetailParser = new PhoneDetailParser();
        BillParser currentParser = billSummaryParser;
        for (String text: tArray) {
            //System.out.print(text);
            currentParser.feedText(text);
            if (Pattern.matches("(?i)^phone summary$", text)) {
                currentParser = phoneSummaryParser;
            } else if (Pattern.matches("(?i)^phone details$", text)) {
                currentParser = phoneDetailParser;
            }
        }
        billSummary = billSummaryParser.getData();
        phoneSummary = phoneSummaryParser.getData();
        phoneDetail = phoneDetailParser.getData();
        //billSummaryParser.dump();
        //phoneSummaryParser.dump();
        //phoneDetailParser.dump();
    }
}
