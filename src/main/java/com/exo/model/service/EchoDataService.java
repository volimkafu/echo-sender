package com.exo.model.service;

import java.util.List;

import com.exo.model.Campaign;
import com.exo.model.Contact;
import com.exo.model.service.exception.EchoDataServiceException;

public interface EchoDataService {

	Campaign findCampaignById(String id) throws EchoDataServiceException;

	List<Campaign> findCampaignCollection() throws EchoDataServiceException;
	
	List<Campaign> findCampaignCollection(int offset, int limit) throws EchoDataServiceException;

	List<Contact> findContactsForCampaign(String campaignId) throws EchoDataServiceException;

	List<Contact> findContactsForCampaign(String campaignId, int offset, int limit) throws EchoDataServiceException;

	Contact findContactById(String id) throws EchoDataServiceException;

}
