// $Id: MultiThreaded.java 10505 2008-03-20 19:50:05Z robert $

package pmta.examples.submitter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import com.port25.pmta.api.submitter.Connection;
import com.port25.pmta.api.submitter.EmailAddressException;
import com.port25.pmta.api.submitter.Message;
import com.port25.pmta.api.submitter.Recipient;
import com.port25.pmta.api.submitter.ServiceException;


/*
 * Example for submitting in a multi-threaded program to PowerMTA.
 *
 * Copyright 2007-2008, Port25 Solutions, Inc.
 * All rights reserved.
 */
public class MultiThreaded {
    /**
     * The servers to connect to.
     */
    private static final Server[] SERVERS = {
        // you can specify the PowerMTA server either by IP or by name.
        new Server("localhost", 25), // 25 is the default SMTP port
        new Server("127.0.0.1", 25)
    };
    

    /**
     * How many connections to start to each server.
     */
    private static final int CONNECTIONS_PER_SERVER = 3;
    
    /**
     * How many threads should share each connection.
     */
    private static final int THREADS_PER_CONNECTION = 3;
    
    /**
     * The sender's email address. Used in the MAIL FROM.
     */
    public static final String SENDER_EMAIL = "java-api@example.port25.com";
    

    public static void main(String args[]) {
        RecipientProvider recipientProvider = getRecipients();
        Vector connections = new Vector();
        Vector threads = new Vector();
        
        // open connections and start threads
        for (int s = 0; s < SERVERS.length; s++) {
            for (int c = 0; c < CONNECTIONS_PER_SERVER; c++) {
                Connection connection = null;
                try {
                    // could also pass user name and password for authentication
                    // in the connection constructor
                    connection = new Connection(SERVERS[s].name, SERVERS[s].port);
                    connections.add(connection);
                    for (int t = 0; t < THREADS_PER_CONNECTION; t++) {
                        Thread thread = new SenderThread("vmta" + t, 
                                connection, recipientProvider);
                        threads.add(thread);
                        thread.start();
                    }
                }
                catch (ServiceException se) {
                    System.err.println("SMTP error "
                            + "while connecting (#" + c + ") to " 
                            + SERVERS[s].name + ":" + SERVERS[s].port + ": "
                            + se.getMessage());
                } 
                catch (IOException ioe) {
                    System.err.println("Network problem " 
                            + "while connecting (#" + c + ") to " 
                            + SERVERS[s].name + ":" + SERVERS[s].port + ": "
                            + ioe.getMessage());
                }
            }
        }
        
        // wait for all threads to finish
        Iterator iter;
        iter = threads.iterator();
        while (iter.hasNext()) {
            try {
                ((Thread) iter.next()).join();
            }
            catch (InterruptedException oops) {
                System.err.println("Interrupted: " + oops.getMessage());
            }
        }
        
        // cleanup: close all connections
        iter = connections.iterator();
        while (iter.hasNext()) {
            ((Connection) iter.next()).close();
        }
    }
    
    
    /**
     * This example does not use a database, hence this convenience method to
     * build a set of email addresses.
     */
    private static RecipientProvider getRecipients() {
        Vector addresses = new Vector();
        // some addresses for testing; change this code to actually use email
        // addresses from your database
        for (int i = 0; i < 1000; i++) {
            addresses.add("someone@your-powermta.com");
        }
        return new RecipientProvider(addresses);
    }
}

    
/**
 * Holds server name (or IP address) and SMTP port.
 * You could add user name and password for authentication here as well and
 * then have the code that creates the connection use these.
 */
class Server {
    public String name;
    public int port;
    
    public Server(String theName, int thePort) {
        name = theName;
        port = thePort;
    }
}


/**
 * Simple class from which the sender threads can obtain recipient email 
 * addresses. In real life, this would be an interface to your data base.
 */
class RecipientProvider {
    private Iterator iter;
    
    public RecipientProvider(Collection theRecipients) {
        iter = theRecipients.iterator();
    }
    
    public synchronized String getNext() {
        if (iter.hasNext()) {
            return (String) iter.next();
        }
        return null;
    }
}


/**
 * A sender thread. Each thread sends emails to recipients obtained from the
 * given RecipientProvider over the given connection, using the given
 * virtual MTA.
 */
class SenderThread extends Thread {
    private String vmta;
    private Connection connection;
    private RecipientProvider nextAddress;

    
    public SenderThread(String theVmta, Connection theConnection, 
            RecipientProvider theNextAddress) {
        vmta = theVmta;
        connection = theConnection;
        nextAddress = theNextAddress;
    }
    
    
    public void run() {
        final String outerBoundary = "outerboundary";
        final String innerBoundary = "innerboundary";
        String address = nextAddress.getNext();  
        while (address != null) {
            Message msg;
            try {
                msg = new Message(MultiThreaded.SENDER_EMAIL);
                msg.setVirtualMta(vmta);
                Recipient rcpt = new Recipient(address);
                msg.addRecipient(rcpt);
                
                String headers = 
                    "To: \"You There\" <" + address + ">\n" + 
                    "From: \"Me Here\" <" 
                        + MultiThreaded.SENDER_EMAIL + ">\n" +
                    "Subject: Multi-threaded PowerMTA example message\n" +
                    "MIME-Version: 1.0\n" +
                    "Content-Type: multipart/related; boundary=\"" 
                        + outerBoundary + "\"\n";
                msg.addData(headers.getBytes()); 
                msg.addDateHeader();

                // Optionally add a preamble for old mail clients that don't 
                // know MIME:
                String preamble =                     
                    "\n" +
                    "This is a multi-part message in MIME format.\n" +
                    "\n";
                msg.addData(preamble.getBytes());
                
                String innerPart = 
                    "--" + outerBoundary + "\n" +
                    "Content-Type: multipart/alternative; boundary=\"" 
                        + innerBoundary + "\"\n" +
                    "\n";
                msg.addData(innerPart.getBytes());
                
                String plainTextBody =
                    "--" + innerBoundary + "\n" +
                    "Content-Type: text/plain; charset=us-ascii\n" +
                    "\n" +
                    "This is sent via PowerMTA's Java API.\n";
                msg.addData(plainTextBody.getBytes());
               
                String logoId = "logo";
                String htmlBody =
                    "--" + innerBoundary + "\n" +
                    "Content-Type: text/html; charset=us-ascii\n" +
                    "\n" +
                    "<html>\n" +
                    "<body>\n" +
                    // include an image on the web like this:
                    // "<img src='http://www.port25.com/images/port25_LOGO.gif'" +
                    // "   alt='logo' />" +
                    // refer to an image sent with the email like this:
                    "<img src='cid:" + logoId + "' alt='logo' />\n" +
                    // see below for the MIME part holding the image.
                    "<p>This is sent via <b>PowerMTA</b>&apos;s Java API.</p>\n" +
                    "</body>\n" +
                    "</html>\n";
                msg.addData(htmlBody.getBytes());

                String partEnd =
                    "\n--" + innerBoundary + "--\n";
                msg.addData(partEnd.getBytes());
                
                String attachedImage =
                    "\n--" + outerBoundary + "\n" +
                    "Content-ID: <" + logoId + ">\n" +
                    "Content-Type: image/gif\n" + 
                    "Content-Transfer-Encoding: base64\n" +
                    // the following ones are optional
                    "Content-Description: Port25 logo\n" +
                    "Content-Disposition: inline; filename=Port25_LOGO.gif\n" +
                    "\n";
                msg.addData(attachedImage.getBytes());
                FileInputStream fin = null;
                try {
                    fin = new FileInputStream("examples/Port25_LOGO.gif");
                    msg.setEncoding(Message.ENCODING_BASE64);
                    byte[] chunk = new byte[fin.available()];
                    fin.read(chunk);
                    msg.addData(chunk);
                }
                finally {
                    if (fin != null) {
                        fin.close();
                    }
                }
                
                // add more images here...
                
                msg.setEncoding(Message.ENCODING_7BIT);
                String endOfMessage = 
                    "\n--" + outerBoundary + "--\n";
                msg.addData(endOfMessage.getBytes());
                connection.submit(msg);
                address = nextAddress.getNext();
            } 
            catch (EmailAddressException oops) {
                System.err.println("Bad email address: " + oops.getMessage());
            }
            catch (FileNotFoundException oops) {
                System.err.println("Cannot attach image: " + oops.getMessage());
            }
            catch (IOException oops) {
                System.err.println("Network I/O problem: " + oops.getMessage());
            } 
            catch (ServiceException oops) {
                System.err.println("SMTP problem: " + oops.getMessage());
            }
        }
    }
}
