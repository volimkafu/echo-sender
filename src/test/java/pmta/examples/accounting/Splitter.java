// $Id: Splitter.java 9770 2007-08-28 14:54:01Z robert $

package pmta.examples.accounting;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;

import com.csvreader.CsvReader;


/**
 * Example code for reading PowerMTA's CSV accounting files.
 * This example takes its arguments as file names, reads these CSV accounting
 * files, and writes all records matching a criteria (e.g., same job ID) to a
 * new file.
 * 
 * <p>This code uses JavaCSV, an open-source Java library for reading (and
 * writing) CSV files. You can download JavaCSV from 
 * <a href="http://sourceforge.net/projects/javacsv/">
 * http://sourceforge.net/projects/javacsv/</a></p>.
 *
 * Copyright 2007 Port25 Solutions, Inc.
 * All rights reserved.
 */
public class Splitter {
    private static final char DELIMITER = ',';
    private static final char TEXTQUALIFIER = '"';
    private static final String CHARSET = "ISO-8859-1";

    
    public static void main(String args[]) throws IOException {
        if (args.length < 2) {
            System.err.println("Args: field file [file...]");
            System.err.println("Where 'field' can be jobId, envId, vmta, etc.");
            System.err.println("Check the PowerMTA User's Guide for field names.");
            System.err.println("'field' is case-sensitive!");
            return;
        }
        Splitter s = new Splitter(args[0]);
        for (int i = 1; i < args.length; i++) {
            s.doRead(args[i]);
        }
        s.closeFiles();
    }

   
    private String want;
    private HashMap openFiles;
    
    
    public Splitter(String wanted) {
        want = wanted;
        openFiles = new HashMap();
    }
    
    
    private void doRead(String filename) throws IOException {
        CsvReader csvReader = new CsvReader(filename, DELIMITER, Charset.forName(CHARSET)); 
        csvReader.setTrimWhitespace(false);
        csvReader.setTextQualifier(TEXTQUALIFIER);        
        csvReader.setUseTextQualifier(true);
        csvReader.setUseComments(false);
        csvReader.setEscapeMode(CsvReader.ESCAPE_MODE_DOUBLED);
        csvReader.readHeaders();
        
        while (csvReader.readRecord()) {
            String id = csvReader.get(want);
            if (id == null) {
                throw new IllegalArgumentException("No '" + want + "' field " +
                        "in record " + csvReader.getCurrentRecord());
            }
            boolean isNewFile = !openFiles.containsKey(id);
            if (isNewFile) {
                openFiles.put(id, new PrintStream("acct-" + want + "-" + id + ".csv"));
            }
            PrintStream out = (PrintStream) openFiles.get(id);
            if (isNewFile) {
                writeHeader(csvReader, out);
            }
            for (int i = 0; i < csvReader.getColumnCount(); i++) {
                out.print(csvReader.get(i));
                writeComma(out, csvReader, i);
            }
            out.println();
        }
        
        csvReader.close();
    }

    
    private void writeComma(PrintStream out, CsvReader csvReader, int i) {
        if (i < csvReader.getColumnCount() - 1) {
            out.print(",");
        }
    }


    private void writeHeader(CsvReader csvReader, PrintStream out) throws IOException {
        for (int i = 0; i < csvReader.getColumnCount(); i++) {
            out.print(csvReader.getHeader(i));
            writeComma(out, csvReader, i);
        }
        out.println();
    }


    private void closeFiles() {
        Iterator i = openFiles.values().iterator();
        while (i.hasNext()) {
            PrintStream ps = (PrintStream) i.next();
            ps.close();
        }
    }
}
