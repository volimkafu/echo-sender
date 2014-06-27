package com.exo.model.exception;

public class InvalidUserException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidUserException(String user, String pwd, String message){
		super(message);
	}
	
	public InvalidUserException(String user, String pwd){
		super("User: " + user + ", password: " + pwd);
	}
	
}
