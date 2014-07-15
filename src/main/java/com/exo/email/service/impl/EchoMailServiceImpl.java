package com.exo.email.service.impl;

import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.validator.EmailValidator;

import com.exo.email.EmailMessage;
import com.exo.email.exception.EchoMailServiceException;
import com.exo.email.service.EchoMailService;
import com.sun.mail.smtp.SMTPMessage;

public class EchoMailServiceImpl implements EchoMailService<EmailMessage> {

	protected final Properties serverProps;

	private Transport transport;
	private Session session;

	public EchoMailServiceImpl(Properties props) {
		serverProps = props;
	}

	public String getHostName() {
		return serverProps.getProperty("mail.host");
	}

	public String getPort() {
		return serverProps.getProperty("mail.port");
	}

	public String getUserName() {
		return serverProps.getProperty("mail.smtp.user");
	}

	public String getPassword() {
		return serverProps.getProperty("mail.smtp.password");
	}

	public Store getStore() throws NoSuchProviderException{
		return session.getStore("pop3");
	}
	
	@Override
	public void connect() throws EchoMailServiceException {
		session = Session.getInstance(serverProps, null);
		session.setDebug(true);
		try {
			transport = session.getTransport("smtp");
		} catch (NoSuchProviderException e) {
			throw new EchoMailServiceException(
					"Failed to find an SMTP provider ", e);
		}
		try {
			transport.connect(this.getHostName(),
					Integer.parseInt(this.getPort()), this.getUserName(),
					this.getPassword());
		} catch (NumberFormatException e) {
			throw new EchoMailServiceException("Failed to set the port to "
					+ this.getPort(), e);
		} catch (MessagingException e) {
			throw new EchoMailServiceException(
					"Failed to connect to host/port with user/pwd: ["
							+ this.getHostName() + "/" + this.getPort() + "] ["
							+ this.getUserName() + "/" + this.getPassword()
							+ "]", e);
		}
	}

	@Override
	public void disconnect() throws MessagingException {
		transport.close();
	}

	@Override
	public boolean isConnected() {
		return this.transport != null;
	}

	
	@Override
	public boolean send(EmailMessage message) throws EchoMailServiceException {

		javax.mail.Message msg = createEmailMessage(message);
		sendEmail(message, msg);
	
		return true;
	}

	protected void sendEmail(EmailMessage message, javax.mail.Message msg) throws EchoMailServiceException {
		try {
			transport.sendMessage(msg, new Address[] { new InternetAddress(message.getTarget().getEmail()) });
		} catch (AddressException e) {
			throw new EchoMailServiceException("Failed to *send* a message " + message, e);
		} catch (MessagingException e) {
			throw new EchoMailServiceException("Failed to *send* a message " + message, e);
		}
	}
	
	protected javax.mail.Message createEmailMessage(EmailMessage message)
			throws EchoMailServiceException {
		javax.mail.Message msg = new SMTPMessage(session);
		try {
			msg.setFrom(new InternetAddress(message.getFromEmail()));
			msg.setRecipients(javax.mail.Message.RecipientType.TO, 
					InternetAddress.parse(message.getTarget().getEmail(), false));
			msg.setSubject(message.getEmailSubject());
			msg.setText(new String(message.getEmailContent()));
			// msg.setHeader("X-Mailer", "JavaMail");
			msg.setSentDate(new Date());
		} catch (AddressException e) {
			throw new EchoMailServiceException("Failed to *create* a message " + message, e);
		} catch (MessagingException e) {
			throw new EchoMailServiceException("Failed to *create* a message " + message, e);
		}
		return msg;
	}

}
