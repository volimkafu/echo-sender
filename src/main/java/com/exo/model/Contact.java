package com.exo.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.mongojack.ObjectId;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)
public class Contact {

	@ObjectId
	@JsonProperty("_id")
	protected String id;

	@JsonProperty("Email")
	protected String email;

	@JsonProperty("FirstName")
	protected String firstName;

	@JsonProperty("LastName")
	protected String lastName;

	@JsonProperty("campaigns")
	private List<ContactParticipationInCampaign> campaigns = new ArrayList<ContactParticipationInCampaign>();

	public Contact() {
	}

	public Contact(String email, String firstName, String lastName) {
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public String getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public List<ContactParticipationInCampaign> getCampaigns() {
		return campaigns;
	}
	
	public void markAsSent(String campaignId, boolean hasBeenSent){
		for( Iterator<ContactParticipationInCampaign> iter = campaigns.iterator(); iter.hasNext();){
			ContactParticipationInCampaign p =iter.next();
			if (p.campaignId.equals(campaignId))
				p.statuses.add(EmailStatus.SENT_SUCCESSFULLY.getStatus());
		}
	}

}
