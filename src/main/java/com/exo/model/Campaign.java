package com.exo.model;

import java.util.Arrays;

import org.mongojack.ObjectId;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)
public class Campaign {

	@ObjectId
	@JsonProperty("_id")
	protected String id;

	@JsonProperty("name")
	protected String name;

	@JsonProperty("from_name")
	protected String fromName;

	@JsonProperty("from_email")
	protected String fromEmail;

	@JsonProperty("subject")
	protected String subject;

	@JsonProperty("content")
	protected byte[] content;

	public Campaign() {
	}

	public Campaign(String id, String name, String fromName, String fromEmail,
			String subject, String content) {
		this.id = id;
		this.name = name;
		this.fromName = fromName;
		this.fromEmail = fromEmail;
		this.subject = subject;
		this.content = content.getBytes();
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getFromName() {
		return fromName;
	}

	public String getFromEmail() {
		return fromEmail;
	}

	public String getSubject() {
		return subject;
	}

	public byte[] getContent() {
		return content;
	}

	@Override
	public String toString() {
		return "Campaign [id=" + id + ", \nname=" + name + ", \nfromName="
				+ fromName + ", \nfromEmail=" + fromEmail + ", \nsubject="
				+ subject + ", \ncontent=" + Arrays.toString(content)+"]";
	}

}
