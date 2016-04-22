/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.anthonypoon.bill_scrapper;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import net.anthonypoon.bill_scrapper.database.DatabaseConnector;
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
    public static void main(String[] args) {
        // TODO code application logic here
        try {
        List<String> argsArray = new ArrayList(Arrays.asList(args));
            if (argsArray.contains("-t")) {
                DatabaseConnector db = new DatabaseConnector();
            } else {

                    String filePath = args[args.length - 1];
                    System.out.println("Loading: " + filePath);
                    PDDocument doc = PDDocument.load(new File(filePath));
                    PDFTextStripper stripper = new PDFTextStripper();
                    String rawText = stripper.getText(doc);
                    String[] textArray = rawText.split("[\\r\\n]+");

                    divideSection(textArray);

            }
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
    }
    
    private static void divideSection (String[] tArray) {
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
        billSummaryParser.dump();
        phoneSummaryParser.dump();
        phoneDetailParser.dump();
    }
}
