package com.exo.model.service;

import java.util.List;

import org.mongojack.DBCursor;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;
import org.mongojack.JacksonDBCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.exo.model.Campaign;
import com.exo.model.Contact;
import com.exo.model.dao.DataAccessServiceImpl;
import com.exo.model.service.exception.EchoDataServiceException;
import com.mongodb.DBCollection;

public class EchoDataServiceImpl implements EchoDataService {

	private final Logger log = LoggerFactory
			.getLogger(EchoDataServiceImpl.class);

	public static final String COLLECTION_CONTACTS = "contacts";
	public static final String COLLECTION_CAMPAIGNS = "campaigns";

	protected final DataAccessServiceImpl dao;

	public EchoDataServiceImpl(DataAccessServiceImpl dao) {
		this.dao = dao;
	}

	@Override
	public List<Campaign> findCampaignCollection() throws EchoDataServiceException {
		try{
		DBCollection dbCollection = dao.findCollection(COLLECTION_CAMPAIGNS);
		JacksonDBCollection<Campaign, String> coll = JacksonDBCollection.wrap(
				dbCollection, Campaign.class, String.class);
		return jacksonAsJavaCollection(coll);
		}catch(Exception e){
				throw new EchoDataServiceException("Failed to fetch campaigns", e);
		}
	}

	@Override
	public List<Campaign> findCampaignCollection(int offset, int limit) throws EchoDataServiceException {
		try{
			DBCollection dbCollection = dao.findCollection(COLLECTION_CAMPAIGNS);
			JacksonDBCollection<Campaign, String> coll = JacksonDBCollection.wrap(
					dbCollection, Campaign.class, String.class);
	
			return jacksonAsJavaCollection(offset, limit, coll);
		}catch(Exception e){
			throw new EchoDataServiceException(
					"Failed to fetch campaigns with offset {" + offset
							+ "} and limit {" + limit + "}", e);
		}
	}

	@Override
	public Campaign findCampaignById(String id) {
		return findOne(id, COLLECTION_CAMPAIGNS, Campaign.class);
	}

	@Override
	public List<Contact> findContactsForCampaign(String campaignId)
			throws EchoDataServiceException {
		try {
			DBCursor<Contact> cursor = jacksonCursorForContactsByCampaignId(campaignId);
			List<Contact> contacts = cursor.toArray();
			return contacts;
		} catch (Exception e) {
			// // FIXME:: - just throw, need to fix campaign master to handle
			// exceptions
			log.error("Failed to fetch contacts for campaign id" + campaignId, e);
			throw new EchoDataServiceException("Failed to fetch contacts for campaign id" + campaignId, e);
		}

	}

	@Override
	public List<Contact> findContactsForCampaign(String campaignId, int offset,
			int limit) throws EchoDataServiceException {
		try {
			DBCursor<Contact> cursor = jacksonCursorForContactsByCampaignId(campaignId);
			return cursor.skip(offset).limit(limit).toArray();
		} catch (Exception e) {
			// FIXME:: - just throw, need to fix campaign master to handle exceptions
			log.error("Failed to fetch contacts for campaign id{" + campaignId
					+ "} with offset {" + offset + "} and limit {" + limit + "}", e);
			throw new EchoDataServiceException("Failed to fetch contacts for campaign id" + campaignId, e);
		}
	}

	@Override
	public Contact findContactById(String id) throws EchoDataServiceException {
		try {
			return findOne(id, COLLECTION_CONTACTS, Contact.class);
		} catch (Exception e) {
			throw new EchoDataServiceException("Failed to fetch contact by id=" + id, e);
		}
	}

	protected <T> List<T> jacksonAsJavaCollection(
			JacksonDBCollection<T, String> coll) {
		return coll.find().toArray();
	}

	protected <T> List<T> jacksonAsJavaCollection(int offset, int limit,
			JacksonDBCollection<T, String> coll) {
		return coll.find().skip(offset).limit(limit).toArray();

	}

	protected <T> T findOne(String id, String collectionName, Class<T> type) {
		DBCollection dbCollection = dao.findCollection(collectionName);
		JacksonDBCollection<T, String> collection = JacksonDBCollection.wrap(
				dbCollection, type, String.class);

		T record = collection.findOneById(id);

		return record;
	}

	protected DBCursor<Contact> jacksonCursorForContactsByCampaignId(
			String campaignId) {

		DBCollection dbCollection = dao.findCollection(COLLECTION_CONTACTS);
		JacksonDBCollection<Contact, String> coll = JacksonDBCollection.wrap(
				dbCollection, Contact.class, String.class);

		Query query = DBQuery.is("campaign_id", campaignId);
		DBCursor<Contact> cursor = coll.find().elemMatch("campaigns", query);
		return cursor;

	}
}
