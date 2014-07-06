package com.exo.email.service;

import javax.mail.MessagingException;

import com.exo.email.Message;
import com.exo.email.exception.EchoMailServiceException;

public interface EchoMailService<T extends Message>  {

	void connect() throws EchoMailServiceException;

	void disconnect() throws MessagingException;

	boolean isConnected();

	boolean send(T message) throws EchoMailServiceException;

}
