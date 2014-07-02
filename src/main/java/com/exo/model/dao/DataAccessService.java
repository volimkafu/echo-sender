package com.exo.model.dao;

import java.util.Map;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public interface DataAccessService {

	DBCollection findCollection(String collectionName);

	DBObject findRecordById(String collectionName, String id, String... requestedFieldNames);

	DBCursor findCollection(String collectionName,
			Map<String, Object> filterParams, int offset, int maxElements);

}