package com.exo.model.dao.impl;

import java.util.Iterator;
import java.util.Map;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.exo.model.dao.DataAccessService;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class DataAccessServiceImpl implements DataAccessService {

	public final Logger log = LoggerFactory
			.getLogger(DataAccessServiceImpl.class);

	protected final DB db;

	public DataAccessServiceImpl(String schema, MongoClient client) {
		db = client.getDB(schema);
	}

	@Override
	public DBCollection findCollection(String collectionName) {
		return db.getCollection(collectionName);
	}

	@Override
	public DBCursor findCollection(String collectionName,
			Map<String, Object> filterParams, int offset, int maxElements) {
		BasicDBObject query = buildAQuery(filterParams);
		DBCollection collection = db.getCollection(collectionName);

		return collection.find(query).skip(offset).limit(maxElements);
	}
	
	@Override
	public DBObject findRecordById(String collectionName, String id,
			String... returnableFieldNames) {
		DBCollection collection = db.getCollection(collectionName);

		DBObject searchById = new BasicDBObject("_id", new ObjectId(id));

		DBObject record;

		if (returnableFieldNames == null) {
			record = collection.findOne(searchById);
		} else {
			DBObject fields = fieldsToReturn(returnableFieldNames);
			record = collection.findOne(searchById, fields);
		}
		return record;
	}

	public DB getDb() {
		return db;
	}

	protected BasicDBObject buildAQuery(final Map<String, Object> filterParams) {
		BasicDBObject query = new BasicDBObject();
		for (Iterator<String> iterator = filterParams.keySet().iterator(); iterator
				.hasNext();) {
			String key = iterator.next();
			query.append(key, filterParams.get(key));
		}
		return query;
	}
 
	protected DBObject fieldsToReturn(String[] returnableFieldNames) {
		DBObject fields = new BasicDBObject();
		for (int i = 0; i < returnableFieldNames.length; i++) {
			fields.put(returnableFieldNames[i], "1");
		}
		return fields;
	}

}
