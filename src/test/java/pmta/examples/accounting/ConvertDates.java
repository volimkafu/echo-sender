// $Id: ConvertDates.java 9895 2007-09-20 23:33:03Z robert $

package pmta.examples.accounting;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.csvreader.CsvReader;


/**
 * Example code for reading PowerMTA's CSV accounting files.
 * This example reads CSV data from stdin, converts PowerMTA's timestamps
 * into human-readable dates, and writes the new records to stdout.
 * Call this program like this:
 *     java accounting/ConvertDates < acct.csv > new-acct.csv
 * 
 * <p>This code uses JavaCSV, an open-source Java library for reading (and
 * writing) CSV files. You can download JavaCSV from 
 * <a href="http://sourceforge.net/projects/javacsv/">
 * http://sourceforge.net/projects/javacsv/</a></p>.
 *
 * Copyright 2007 Port25 Solutions, Inc.
 * All rights reserved.
 */
public class ConvertDates {
    private static final char DELIMITER = ',';
    private static final char TEXTQUALIFIER = '"';
    private static final String CHARSET = "ISO-8859-1";

    
    public static void main(String args[]) throws IOException {
        CsvReader csvReader = new CsvReader(System.in, DELIMITER, Charset.forName(CHARSET)); 
        csvReader.setTrimWhitespace(false);
        csvReader.setTextQualifier(TEXTQUALIFIER);        
        csvReader.setUseTextQualifier(true);
        csvReader.setUseComments(false);
        csvReader.setEscapeMode(CsvReader.ESCAPE_MODE_DOUBLED);
        csvReader.readHeaders();
        writeHeader(csvReader);
        
        while (csvReader.readRecord()) {
            for (int i = 0; i < csvReader.getColumnCount(); i++) {
                String colName = csvReader.getHeaders()[i];
                // Convert non-empty fields (fields like timeImprinted are empty
                // if no gm-imprinting was used)
                if (colName.startsWith("time") && !"".equals(csvReader.get(i))) {
                    // Multiply time stamp by 1000: PowerMTA uses seconds, Java
                    // expects milliseconds
                    long ms = Long.parseLong(csvReader.get(i)) * 1000;
                    // PowerMTA stores times in UTC (=GMT); below line converts
                    // this to local time
                    ms += TimeZone.getDefault().getRawOffset();
                    // See the SimpleDateFormat JavaDoc for different date formats
                    // http://java.sun.com/j2se/1.4.2/docs/api/java/text/SimpleDateFormat.html
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss Z");
                    System.out.print(sdf.format(new Date(ms)));
                }
                else {
                    System.out.print(csvReader.get(i));
                }
                writeComma(csvReader, i);
            }
            System.out.println();
        }
        csvReader.close();
    }

    
    private static void writeComma(CsvReader csvReader, int i) {
        if (i < csvReader.getColumnCount() - 1) {
            System.out.print(",");
        }
    }


    private static void writeHeader(CsvReader csvReader) throws IOException {
        for (int i = 0; i < csvReader.getColumnCount(); i++) {
            System.out.print(csvReader.getHeader(i));
            writeComma(csvReader, i);
        }
        System.out.println();
    }
}
