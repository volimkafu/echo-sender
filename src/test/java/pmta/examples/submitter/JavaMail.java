// $Id: JavaMail.java 10444 2008-03-05 14:46:54Z robert $

package pmta.examples.submitter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import com.sun.mail.smtp.SMTPMessage;

/*
 * Example for using the Java Mail API with PowerMTA.
 *
 * Copyright 2002-2008, Port25 Solutions, Inc.
 * All rights reserved.
 *
 * <p>For further information on the JavaMail API see Sun's official JavaMail 
 * API homepage at <a href="http://java.sun.com/products/javamail">
 * http://java.sun.com/products/javamail</a>.</p>
 */
class JavaMail {
	public static void main(String args[]) {
		try {
			oneShot();
			// manyMails();
		} catch (MessagingException me) {
			System.err.println("Error sending message: ");
			me.printStackTrace();
		} catch (UnsupportedEncodingException uee) {
			System.err.println("Unsupported encoding: " + uee.getMessage());
		} catch (IOException ioe) {
			System.err.println("I/O error: " + ioe.getMessage());
		}
	}

	/**
	 * Sends a single email. This is a one-shot way of just calling static
	 * methods. For each message, a new connection is opened. No authentication
	 * is done.
	 */
	private static void oneShot() throws MessagingException, IOException {
	
		Properties props = System.getProperties();
		props.put("mail.host", "205.186.137.166");
		props.put("mail.port", "2525");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.user", "echo.live@ekobuzz.com");
		props.put("mail.smtp.password", "_#peakerF)rTheDead_");
		props.put("mail.smtp.sasl.enable", "true");
		props.put("mail.smtp.auth.mechanisms", "CRAM-MD5");

		Session session = Session.getInstance(props, null);
		session.setDebug(true);

		Message msg = new SMTPMessage(session);
		msg.setFrom(new InternetAddress("echo.live@ekobuzz.com"));
		msg.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse("alla@ekobuzz.com", false));
		msg.setSubject("Email from junit test");
		msg.setText("vitally important text");
		//msg.setHeader("X-Mailer", "JavaMail");
		msg.setSentDate(new Date());

		Transport tr = session.getTransport("smtp");
		tr.connect(props.getProperty("mail.host"),
				Integer.parseInt(props.getProperty("mail.port")),
				props.getProperty("mail.smtp.user"),
				props.getProperty("mail.smtp.password"));
		tr.sendMessage(msg, new Address[]{new InternetAddress("alla@ekobuzz.com")});
		
	}

	/**
	 * Sends multiple emails.
	 * 
	 * This code is more efficient than calling the static Transport.send()
	 * method. However, you have to do some things yourself: connect initially,
	 * saveChanges to the message before sending, and close the connection when
	 * done.
	 */
	private static void manyMails() throws MessagingException, IOException {
		Properties props = System.getProperties();
		// See the Javamail spec for more information on what you can put
		// in the properties (appendix A):
		// http://jcp.org/aboutJava/communityprocess/mrel/jsr919/index.html
		// props.put("mail.debug", "true"); // verbose JavaMail details
		Session session = Session.getInstance(props, null);
		Transport trans = session.getTransport("smtp");

		// Assuming that PowerMTA runs on the same machine as this code;
		// otherwise adjust below. Put in user name and password if needed.
		String host = "127.0.0.1"; // local machine
		int port = 25; // default SMTP port
		String username = null;
		String password = null;
		trans.connect(host, port, username, password);

		// Send messages
		for (int i = 0; i < 1; i++) {
			SMTPMessage msg = buildMessage(session);
			Address to[] = { new InternetAddress("you@example.port25.com") };
			trans.sendMessage(msg, to);
		}
		trans.close();
	}

	private static SMTPMessage buildMessage(Session session)
			throws MessagingException, IOException {
		SMTPMessage msg = new SMTPMessage(session);
		// Set options as desired:
		// msg.setNotifyOptions(SMTPMessage.NOTIFY_SUCCESS);
		// msg.setEnvelopeFrom("me@example.port25.com");
		msg.setSubject("Sending email with JavaMail and PMTA");
		msg.setSentDate(new Date());

		// Optionally: Add custom headers like below.
		msg.addHeader("x-my-special-id", "foo");

		// Optionally: Set the job ID. You can configure PowerMTA to include
		// the job ID in the accounting file. The process-x-job directive
		// must be set for the sending source in PowerMTA's configuration
		// file. See section 3.2.4 in the User's Guide.
		msg.addHeader("x-job", "myJobId");

		// Optionally: Pick a virtual MTA (as defined in PowerMTA).
		// The process-x-virtual-mta directive must be used for the sending
		// source in PowerMTA's configuration file. See sections 3.2.4 and
		// 8 in the User's Guide.
		// msg.addHeader("x-virtual-mta", "vmta1");

		// Optionally: set an envelope id. The process-x-envid directive
		// must be set for the sending sorce in PowerMTA's configuration
		// file. See section 3.2.4 in the User's Guide.
		// msg.addHeader("x-envid", "envelope-id");

		msg.setFrom(new InternetAddress("me@example.port25.com", "Me Here"));
		Address to[] = { new InternetAddress("you@example.port25.com",
				"You There") };
		msg.setRecipients(Message.RecipientType.TO, to);
		msg.saveChanges();
		setContent(msg);
		return msg;
	}

	private static void setContent(Message msg) throws MessagingException {
		MimeMultipart alternative = new MimeMultipart("alternative");

		MimeBodyPart plainTextBodyPart = new MimeBodyPart();
		plainTextBodyPart.setText("This is sent via JavaMail and PowerMTA.");
		alternative.addBodyPart(plainTextBodyPart);

		String logoId = "logo";
		MimeBodyPart htmlBodyPart = new MimeBodyPart();
		String htmlText = "<html>\n"
				+ "<body>\n"
				+
				// include an image on the web like this:
				// "<img src='http://www.port25.com/images/port25_LOGO.gif'" +
				// "   alt='PowerMTA' />" +
				// refer to an image sent with the email like this:
				"<img src='cid:"
				+ logoId
				+ "' alt='logo' />\n"
				+ "<p>This is sent via <b>JavaMail</b> and <b>PowerMTA</b>.</p>\n"
				+ "</body>\n" + "</html>\n";
		htmlBodyPart.setContent(htmlText, "text/html");
		alternative.addBodyPart(htmlBodyPart);

		MimeBodyPart wrap = new MimeBodyPart();
		wrap.setContent(alternative);

		MimeMultipart mixed = new MimeMultipart("related");
		// Optionally set a preamble for old mail clients that don't know MIME
		// (works only with JavaMail 1.4 and above):
		// mixed.setPreamble("This is a multi-part message in MIME format.");
		mixed.addBodyPart(wrap);

		MimeBodyPart imageBodyPart = new MimeBodyPart();
		imageBodyPart.setDataHandler(new DataHandler(new FileDataSource(
				"examples/Port25_LOGO.gif")));
		// With JavaMail 1.4 and above, you can attach files like this:
		// imageBodyPart.attachFile(new
		// java.io.File("examples/Port25_LOGO.gif"));
		imageBodyPart.setContentID("<" + logoId + ">");
		imageBodyPart.setDescription("Port25 logo");
		imageBodyPart.setDisposition(javax.mail.Part.INLINE);
		mixed.addBodyPart(imageBodyPart);

		// add more images here...

		msg.setContent(mixed);
		msg.saveChanges();
	}
}
