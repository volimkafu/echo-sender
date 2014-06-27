// $Id: SimpleMessage.java 9641 2007-08-01 15:07:43Z robert $

package pmta.examples.submitter;

import java.io.IOException;
import com.port25.pmta.api.submitter.*;


/*
 * Example for usage of the Java PMTA Submitter API.
 *
 * Copyright 2002-2007, Port25 Solutions, Inc.
 * All rights reserved.
 */
class SimpleMessage {
    public static void main(String args[]) {
        Connection conn = null;
        try {
            conn = new Connection("205.186.137.166", 2525, "echo.live@ekobuzz.com", "_#peakerF)rTheDead_");
            String text = 
                "Subject: Sent using the PMTA Java API\n" +
                "To: You There <you@example.port25.com>\n" +
                "From: Me Here <me@example.port25.com>\n" +
                "Subject: PowerMTA example message\n" +
                "\n" +
                "This message was sent via PowerMTA's Java API.\n";
            Message msg = new Message("echo.live@ekobuzz.com");
            msg.addRecipient(new Recipient("alla@ekobuzz.com"));
            msg.addRecipient(new Recipient("anil@ekobuzz.com"));
            msg.addRecipient(new Recipient("ajaising@gmail.com"));
            msg.addRecipient(new Recipient("ukraine@earthlink.net"));
            msg.setReturnType(Message.RETURN_HEADERS);
            msg.setJobId("job001");
            msg.addDateHeader();
            msg.addData(text.getBytes());
            conn.submit(msg);
        }
        catch (EmailAddressException eae) {
            System.err.println(eae.getMessage());
        }
        catch (ServiceException se) {
            System.err.println("Error sending: " + se.getMessage());
        } 
        catch (IOException ioe) {
            System.err.println("I/O problem: " + ioe.getMessage());
        }
        finally {
            if (conn != null) {
                conn.close();
            }
        }
    }
}
