package com.exo.model.exception;

public class EchoDataServiceException extends Exception {
	private static final long serialVersionUID = 1L;

	public EchoDataServiceException(String msg, Exception e) {
		super(msg, e);
	}

}
