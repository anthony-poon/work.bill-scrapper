/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.anthonypoon.billscrapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import net.anthonypoon.billscrapper.database.DatabaseConnector;
import net.anthonypoon.billscrapper.database.DbWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
/**
 *
 * @author anthony.poon
 */
public class JavaBillScrapper {
    private enum Flags {
        VERBOSE ("-v"),
        INSERT_INTO_DB ("-i");
        private String str;
        private Flags(String str) {
            this.str = str;
        }
        
        @Override
        public String toString() {
            return str;
        }
        public static Flags fromString(String str) {
            for (Flags arg : Flags.values()) {
                if (str.equalsIgnoreCase(arg.toString())) {
                    return arg;
                }
            }
            throw new IllegalArgumentException("Illegal Flag:" + str);
        }

    }
    private Bill billObj = new Bill();
    private static List<String> filePaths = new ArrayList<>();
    private static List<Flags> options = new ArrayList<>();
    
    public JavaBillScrapper(File pdfFile) throws IOException {
        PDDocument doc = PDDocument.load(pdfFile);
        PDFTextStripper stripper = new PDFTextStripper();
        String rawText = stripper.getText(doc);
        String[] textArray = rawText.split("[\\r\\n]+");
        this.billObj = parsePdf(textArray);
        doc.close();
    }
    
    public Bill getBill() {
        return billObj;
    }
    
    public static void main(String[] args) {
        // TODO code application logic here
        try {
            for (String arg : args) {
                if (!arg.startsWith("-")){
                    filePaths.add(arg);
                } else {
                    try {                        
                        options.add(Flags.fromString(arg));
                    } catch (IllegalArgumentException ex) {
                        System.err.println("Illegal options: " + arg);
                    }
                }
            }
            Collections.sort(filePaths);
            for (String filePath : filePaths) {
                System.out.println("Loading: " + filePath);
                PDDocument doc = PDDocument.load(new File(filePath));
                PDFTextStripper stripper = new PDFTextStripper();
                String rawText = stripper.getText(doc);
                String[] textArray = rawText.split("[\\r\\n]+");
                Bill bill = parsePdf(textArray);
                if (options.contains(Flags.INSERT_INTO_DB)) {
                    DatabaseConnector db = new DatabaseConnector();
                    DbWriter writer = new DbWriter(db.getConnection());
                    boolean isInserted = writer.insertDetail(bill.getBillSummary(), bill.getPhoneSummaryData(), bill.getPhoneDetail());
                    writer.commit();
                    doc.close(); 
                    if (!isInserted) {
                        System.out.println(filePath + " was not inserted into database.");
                    }
                }
            }
            
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
    }
    
    private static Bill parsePdf (String[] tArray) {
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
        Bill returnObj = new Bill();
        returnObj.setBillSummary(billSummaryParser.getData());
        returnObj.setPhoneSummaryData(phoneSummaryParser.getData());
        returnObj.setPhoneDetail(phoneDetailParser.getData());
        if (options.contains(Flags.VERBOSE)) {
            billSummaryParser.dump();
            phoneSummaryParser.dump();
            phoneDetailParser.dump();
        }
        return returnObj;
    }
}
