// $Id: CountDeliveriesAndBouncesForDomain.java 9770 2007-08-28 14:54:01Z robert $

package pmta.examples.accounting;


import java.io.IOException;
import java.nio.charset.Charset;

import com.csvreader.CsvReader;


/**
 * Example for processing PowerMTA's CSV accounting records.
 * This program counts the number of deliveries and bounces for a given domain
 * in some CSV accounting files.
 *
 * Copyright 2007 Port25 Solutions, Inc.
 * All rights reserved.
 */
public class CountDeliveriesAndBouncesForDomain {
    private static final char DELIMITER = ',';
    private static final char TEXTQUALIFIER = '"';
    private static final String CHARSET = "ISO-8859-1";
    
    private String domain;
    private int deliveries;
    private int bounces;
    

    public static void main(String args[]) throws Exception {
        if (args.length < 2) {
            System.err.println("Arguments: domain accounting-file[s]");
            return;
        }
        CountDeliveriesAndBouncesForDomain acc = 
            new CountDeliveriesAndBouncesForDomain(args[0]);
        for (int i = 1; i < args.length; i++) {
            acc.readFile(args[i]);
        }
        acc.print();
    }


    public CountDeliveriesAndBouncesForDomain(String theDomain) {
        domain = theDomain;
        deliveries = 0;
        bounces = 0;
    }
    
    
    private void readFile(String filename) throws IOException {
        CsvReader csvReader = new CsvReader(filename, DELIMITER, Charset.forName(CHARSET));
        csvReader.setTrimWhitespace(false);
        csvReader.setTextQualifier(TEXTQUALIFIER);        
        csvReader.setUseTextQualifier(true);
        csvReader.setUseComments(false);
        csvReader.setEscapeMode(CsvReader.ESCAPE_MODE_DOUBLED);
        csvReader.readHeaders();
        
        while (csvReader.readRecord()) {
            handleData(csvReader);
        }
        
        csvReader.close();
    }


    private void handleData(CsvReader reader) throws IOException {
        String type = reader.get("type");
        String rcpt = reader.get("rcpt");
        if (rcpt != null && rcpt.endsWith(domain)) {
            if ("b".equals("type")) {
                bounces++;
            }
            else if ("d".equals(type)) {
                deliveries++;
            }
        }
    }

    
    public void print() {
        System.out.println("Saw " + deliveries + " deliveries and " + bounces +
                " bounces for " + domain + ".");
    }
}
