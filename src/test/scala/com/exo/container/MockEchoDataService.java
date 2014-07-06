package com.exo.container;

import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import com.exo.model.Campaign;
import com.exo.model.Contact;
import com.exo.model.exception.EchoDataServiceException;
import com.exo.model.service.EchoDataService;

public class MockEchoDataService implements EchoDataService{

	@Override
	public Campaign findCampaignById(String id) throws EchoDataServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Campaign> findCampaignCollection()
			throws EchoDataServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Campaign> findCampaignCollection(int offset, int limit)
			throws EchoDataServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Contact> findContactsForCampaign(String campaignId)
			throws EchoDataServiceException {
		List<Contact> mock = new ArrayList<Contact>(10);
		Contact c1 = mock(Contact.class);
		Contact c2 = mock(Contact.class);
		Contact c3 = mock(Contact.class);
		Contact c4 = mock(Contact.class);
		Contact c5 = mock(Contact.class);
		Contact c6 = mock(Contact.class);
		Contact c7 = mock(Contact.class);
		Contact c8 = mock(Contact.class);
		Contact c9 = mock(Contact.class);
		Contact c10 = mock(Contact.class);
		mock.add(c1);
		mock.add(c2);
		mock.add(c3);
		mock.add(c4);
		mock.add(c5);
		mock.add(c6);
		mock.add(c7);
		mock.add(c8);
		mock.add(c9);
		mock.add(c10);
		
		return mock;
	}

	@Override
	public List<Contact> findContactsForCampaign(String campaignId, int offset,
			int limit) throws EchoDataServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Contact findContactById(String id) throws EchoDataServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateContact(Contact contact) throws EchoDataServiceException {
		// TODO Auto-generated method stub
		
	}

}
