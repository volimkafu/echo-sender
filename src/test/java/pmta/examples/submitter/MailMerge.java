// $Id: MailMerge.java 11293 2009-09-17 20:50:52Z juan $

package pmta.examples.submitter;

import java.util.Map;
import java.util.HashMap;
import com.port25.pmta.api.submitter.*;

import java.io.IOException;
import java.sql.*;


/**
 * Example for using mailmerge and JDBC to send personalized email with the
 * PowerMTA Java Submitter API.
 *
 * <p>Copyright 2004-2009, Port25 Solutions, Inc.
 * All rights reserved.</p>
 *
 * <p>This example uses CsvJdbc, an open-source CSV JDBC driver, which you
 * can download from <a href="http://csvjdbc.sf.net">SourceForge</a>.</p>
 */
public class MailMerge {
    private static final String jdbcDriver = "org.relique.jdbc.csv.CsvDriver";
    private static final String conString = "jdbc:relique:csv:examples";
    private static final String query = "SELECT * FROM mailmerge";

    private java.sql.Connection con = null;
    private Statement stmt = null;
    private ResultSet customers = null;
    private ResultSetMetaData meta = null;


    public static void main(String args[]) {
        Message msg;
        try {
            msg = buildMessage("travel-alerts@example.port25.com");
            MailMerge emm = new MailMerge();
            emm.openDB(jdbcDriver, conString, query);
            Map variables;
            while ((variables = emm.readNextRecipient()) != null) {
                Recipient rcpt = null;
                try {
                    // System.out.println("Debug recipient variables: " + variables);
                    rcpt = new Recipient((String) variables.get("to"));
                    variables.remove("to"); // don't need "to" anymore, we got "*to"
                    rcpt.defineVariables(variables);
                    rcpt.defineVariable("*parts", "1");
                    msg.addRecipient(rcpt);
                }
                catch (EmailAddressException eae) {
                    System.out.println("Bad email in " + rcpt + ": " 
                        + eae.toString());
                }
            }
            emm.closeDB();
            com.port25.pmta.api.submitter.Connection conn =
                 new com.port25.pmta.api.submitter.Connection("localhost", 25);
            conn.submit(msg);
        } 
        catch (EmailAddressException eae) {
            System.err.println("Bad sender address");
        }
        catch (ClassNotFoundException cnfe) {
            System.err.println("Could not load classes for JDBC: " 
                    + cnfe.getMessage());
        }
        catch (SQLException sqle) {
            System.err.println("JDBC SQL problem: " + sqle.getMessage());
        } 
        catch (IOException ioe) {
            System.err.println("Network problem: " + ioe.getMessage());
        } 
        catch (ServiceException se) {
            System.err.println("Error sending email: " + se.getMessage());
        }
    }


    /**
     * Builds a mailmerge message by adding content.
     *
     * @param mailfrom
     *      Originator of the message; used in the MAIL FROM.
     * @return 
     *      Full message.
     */
    private static Message buildMessage(String mailfrom) 
    throws EmailAddressException {
        Message msg = new Message(mailfrom);
        msg.setEncoding(Message.ENCODING_7BIT);
        String boundary = "boundaryTagForAlternative";
        String content = 
            "From: [*from]\n" +
            "To: [*to]\n" +
            "Date: [*date]\n" +
            "Subject: Travel Alert\n" +
            "MIME-Version: 1.0\n" +
            "Content-Type: multipart/alternative; boundary=\"" + boundary + "\"\n" +
            "\n" +
            "--" + boundary + "\n" +
            "Content-Type: text/plain; charset=us-ascii\n" +
            "Content-Transfer-Encoding: 7bit\n" +
            "\n" +
            "Dear [name],\n" +
            "you are flying out of [airport] given that it is closest\n" +
            "to your location in [city], [state].\n" + 
            "\n" +
            "--" + boundary + "\n" +
            "Content-Type: text/html; charset=iso-8859-1\n" +
            "Content-Transfer-Encoding: 8bit\n" +
            "\n" +
            "<html>\n" +
            "<head>\n" +
            "   <title>Travel Alert</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "Dear [name],<br>\n" +
            "you are flying out of <a href='http://[url]'>[airport]</a> given " + 
            "that it is closest to your location in [city], [state].\n" +
            "</body>\n" +
            "</html>\n" +
            "\n" +
            "--" + boundary + "\n";
        msg.addMergeData(content.getBytes());
        return msg;
    }


    /**
     * Opens a connection to the database.
     * 
     * @param theDriver
     *      Java name of the JDBC driver class. Must be in the class path.
     * @param theConString
     *      the connection string the JDBC driver should use to connect to
     *      the database.
     * @param theQuery
     *      SQL statement to obtain the desired data.
     *
     * @throws ClassNotFoundException
     *      if the JDBC driver class can not be loaded.
     * @throws SQLException
     *      on internal problems in the JDBC driver.
     */
    private void openDB(String theDriver, String theConString, String theQuery)
    throws ClassNotFoundException, SQLException {
        Class.forName(theDriver);    // load class
        con = DriverManager.getConnection(theConString);
        stmt = con.createStatement();
        customers = stmt.executeQuery(theQuery);
        meta = customers.getMetaData();
    }


    /**
     * Returns the next recipients's data from the database.
     *
     * @return
     *      Map containing the key/value pairs to feed to PowerMTA for
     *      mailmerge.
     * @throws SQLException
     *      On problems in the JDBC driver.
     * @throws NullPointerException
     *      if no connection to the database was opened.
     */
    private Map readNextRecipient() throws SQLException {
        if (customers.next()) {
            HashMap variables = new HashMap();
            // Col 0 is special information
            for (int col = 1; col <= meta.getColumnCount(); col++) {
                String varName = meta.getColumnName(col).trim();
                variables.put(varName, (new String(customers.getBytes(col)).trim()));
            }
            return variables;
        }
        return null;
    }


    /**
     * Closes the database connection and frees its resources.
     *
     * @throws SQLException
     *      on problems reported by the JDBC driver.
     */
    private void closeDB() throws SQLException {
        customers.close();
        stmt.close();
        con.close();
    }
}
