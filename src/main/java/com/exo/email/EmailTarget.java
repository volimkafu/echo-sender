package com.exo.email;

import com.exo.engine.Target;


public class EmailTarget implements Target {

	private final String id;
	private String email;

	public EmailTarget(String id) {
		super();
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "EmailTarget [id=" + id + ", email=" + email + "]";
	}

}
