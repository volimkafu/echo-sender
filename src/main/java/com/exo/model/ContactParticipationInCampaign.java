package com.exo.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)
class ContactParticipationInCampaign {

	@JsonProperty("campaign_id")
	protected String campaignId;
	
	@JsonProperty("status")
	protected List<String> statuses = new ArrayList<String>();
}
