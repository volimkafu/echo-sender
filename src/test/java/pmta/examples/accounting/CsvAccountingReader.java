// $Id: CsvAccountingReader.java 9770 2007-08-28 14:54:01Z robert $

package pmta.examples.accounting;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import com.csvreader.CsvReader;


/**
 * Base class holding commonly used code for programs that read PowerMTA's
 * CSV accounting files. This class works with the template method pattern.
 * Derived classes call <code>readFile</code> to process a CSV file and
 * implement the <code>handleData</code> method.
 * 
 * <p>This code uses JavaCSV, an open-source Java library for reading (and
 * writing) CSV files. You can download JavaCSV from 
 * <a href="http://sourceforge.net/projects/javacsv/">
 * http://sourceforge.net/projects/javacsv/</a></p>.
 *
 * Copyright 2007 Port25 Solutions, Inc.
 * All rights reserved.
 */
public abstract class CsvAccountingReader {
    private static final char DELIMITER = ',';
    private static final char TEXTQUALIFIER = '"';
    private static final String CHARSET = "ISO-8859-1";


    /**
     * Reads the given CSV file.
     * 
     * @param filename
     *      path and name of the file to read.
     * @throws IOException
     *      if the used CsvReader throws. 
     */
    public void readFile(String filename) throws IOException {
        CsvReader csvReader = new CsvReader(filename, DELIMITER, 
                                            Charset.forName(CHARSET));
        doRead(csvReader);
    }
    
    
    public void readStream(InputStream in) throws IOException {
        CsvReader csvReader = new CsvReader(in, Charset.forName(CHARSET));
        doRead(csvReader);
    }
    
    
    private void doRead(CsvReader csvReader) throws IOException {
        csvReader.setDelimiter(DELIMITER);
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
    
    
    /**
     * Template method for derived classes. Called for each record read.
     * 
     * @param reader
     *      CsvReader which has just read a record. The fields contained depend
     *      on PowerMTA's configuration when the data was written.
     *      Check the User's Guide for field names in the various record types.
     * @throws IOException 
     *      if the underlying CsvReader throws.
     */
    protected abstract void handleData(CsvReader reader) throws IOException;
}
