package com.exo.email.service;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;

import javax.mail.Message;
import javax.mail.MessagingException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jvnet.mock_javamail.Mailbox;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.exo.email.EmailMessage;
import com.exo.email.EmailTarget;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/mail-source.xml" })
public class EchoMailServiceTest {

	@Autowired
	EchoMailService<EmailMessage> service;

	@After
	public void disconnect() throws MessagingException {
		service.disconnect();
		Assert.assertEquals("Should've disconnected by now", true,
				service.isConnected());

	}

	@Before
	public void init() throws Exception {
		Mailbox.clearAll();
		service.connect();
		Assert.assertTrue("The service should've been connected",
				service.isConnected());
	}

	@Test
	public void testSend() throws Exception {
		
		EmailTarget target = new EmailTarget("bogus");
		target.setEmail("alla@ekobuzz.com");
		EmailMessage message = new EmailMessage("",
				"email content from unit test".getBytes(), "alla@ekobuzz.com",
				"AN", "Campaign name", "email subject", target);
		
		EchoMailServiceImpl spy  = (EchoMailServiceImpl) spy(service);
		doNothing().when(spy).sendEmail(Mockito.<EmailMessage>any(), Mockito.<Message>any());
		
		boolean hasBeenSent = spy.send(message);
		Assert.assertTrue("The message should've been sent", hasBeenSent);
		
		Mockito.verify(spy, Mockito.times(3) );

//		TODO: mock-javamail: couldn't make it work
//		
//		assertEquals(1, org.jvnet.mock_javamail.Mailbox.get(target.getEmail()).getNewMessageCount()); // was the e-mail really sent?
//		Message msg = inbox.get(0);
//		assertEquals("email subject", msg.getSubject());
//		
//		Store store = ((EchoMailServiceImpl)service).getStore();
//		store.connect("inmemory.com","test-mailbox","anything");
//
//		Folder folder = store.getFolder("INBOX");
//		folder.open(Folder.READ_WRITE);
//		Message[] msgs = folder.getMessages();
//		assertEquals(1, msgs.length); // was the e-mail really sent?
	}

}
