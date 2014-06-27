package com.exo.model.exception;

public class EchoMailServiceException extends Exception{

	private static final long serialVersionUID = 1L;

	public EchoMailServiceException(String message, Throwable error){
		super(message, error);
	}
	
}
