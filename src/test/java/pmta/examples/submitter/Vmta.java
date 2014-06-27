// $Id: Vmta.java 9641 2007-08-01 15:07:43Z robert $

package pmta.examples.submitter;

import java.io.IOException;
import com.port25.pmta.api.submitter.*;

/*
 * Example for usage of the Java PMTA Submitter API with specifying a virtual
 * mail transfer agent.
 *
 * Copyright 2002-2007, Port25 Solutions, Inc.
 * All rights reserved.
 */
class Vmta {
    public static void main(String args[]) {
        String vmta = "vmta1";
        String text = 
            "Subject: Sent over a virtual MTA\n" +
            "To: You There <you@example.port25.com>\n" +
            "From: Me Here <me@example.port25.com>\n" +
            "Subject: VMTA example\n" +
            "\n" +
            "This message was sent over the " + vmta + 
            " virtual mail transfer agent.\n";
        try {
            Message msg = new Message("me@example.port25.com");
            Recipient rcpt = new Recipient("you@example.port25.com");
            msg.addRecipient(rcpt);
            msg.setReturnType(Message.RETURN_FULL);
            msg.addDateHeader();
            msg.setEncoding(Message.ENCODING_8BIT);
            msg.addData(text.getBytes());
            msg.setVirtualMta(vmta);
            Connection conn = new Connection("localhost", 25);
            conn.submit(msg);
        }
        catch (ServiceException se) {
            System.err.println("ServiceException: " + se);
        }
        catch (EmailAddressException eae) {
            System.err.println("ServiceException: " + eae);
        }
        catch (IOException ioe) {
            System.err.println("ServiceException: " + ioe);
        }
    }
}
