package com.exo.model;

public enum EmailStatus {
	SENT_SUCCESSFULLY("sent"),
	SEND_FAILED("send_failed");
	
	private final String status;
	
	private EmailStatus(String status){
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

}
