package com.exo.email;

import java.util.Arrays;

import net.sf.oval.constraint.NotEmpty;
import net.sf.oval.constraint.NotNull;


public class EmailMessage implements Message {

	@NotNull
	@NotEmpty
	private final String campaignId;
	
	@NotNull
	@NotEmpty
	private final byte[] emailContent;
	
	@NotNull
	@NotEmpty
	private final String fromEmail;
	
	@NotNull
	@NotEmpty
	private final String fromName;

	@NotNull
	@NotEmpty
	private final String campaignName;

	@NotNull
	@NotEmpty
	private final String emailSubject;
	
	@NotNull
	@NotEmpty
	private final EmailTarget target;
	
	private String targetKey;
	
	private String bounceToEmail;
	
	public EmailMessage(String campaignId, byte[] emailContent,
			String fromEmail, String fromName, String campaignName,
			String emailSubject, EmailTarget target) {
		super();
		this.campaignId = campaignId;
		this.emailContent = emailContent;
		this.fromEmail = fromEmail;
		this.fromName = fromName;
		this.campaignName = campaignName;
		this.emailSubject = emailSubject;
		this.target = target;
	}

	public String getBounceToEmail() {
		return bounceToEmail;
	}

	public void setBounceToEmail(String bounceToEmail) {
		this.bounceToEmail = bounceToEmail;
	}

	public String getCampaignId() {
		return campaignId;
	}

	public byte[] getEmailContent() {
		return emailContent;
	}

	public String getFromEmail() {
		return fromEmail;
	}

	public String getFromName() {
		return fromName;
	}

	public String getCampaignName() {
		return campaignName;
	}

	public String getEmailSubject() {
		return emailSubject;
	}

	public EmailTarget getTarget() {
		return target;
	}


	public String getTargetKey() {
		return targetKey;
	}

	public void setTargetKey(String targetKey) {
		this.targetKey = targetKey;
	}

	@Override
	public String toString() {
		return "EmailMessage [campaignId=" + campaignId + ", emailContent="
				+ emailContent == null ? null : Arrays.toString(emailContent) + ", fromEmail=" + fromEmail
				+ ", fromName=" + fromName + ", campaignName=" + campaignName
				+ ", emailSubject=" + emailSubject + ", target=" + target
				+ ", targetKey=" + targetKey + ", bounceToEmail="
				+ bounceToEmail + "]";
	}
}
