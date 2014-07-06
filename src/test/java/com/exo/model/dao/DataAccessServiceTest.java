package com.exo.model.dao;

import static com.lordofthejars.nosqlunit.mongodb.InMemoryMongoDb.InMemoryMongoRuleBuilder.newInMemoryMongoDbRule;
import static com.lordofthejars.nosqlunit.mongodb.MongoDbRule.MongoDbRuleBuilder.newMongoDbRule;
import static org.hamcrest.Matchers.isIn;

import java.net.UnknownHostException;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.exo.model.dao.impl.DataAccessServiceImpl;
import com.exo.model.exception.InvalidUserException;
import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import com.lordofthejars.nosqlunit.mongodb.InMemoryMongoDb;
import com.lordofthejars.nosqlunit.mongodb.MongoDbRule;
import com.mongodb.DBCollection;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/sender-context.xml" })
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
public class DataAccessServiceTest {

	private static final String defaultSchema = "test";

	public final Logger log = LoggerFactory
			.getLogger(DataAccessServiceTest.class);

	@Autowired
	DataAccessServiceImpl dao;
	
	@Autowired
	private ApplicationContext applicationContext;

	@ClassRule
	public static InMemoryMongoDb inMemoryMongoDb = newInMemoryMongoDbRule().build();
		
	@Rule
	public MongoDbRule mongoDbRule = newMongoDbRule().defaultSpringMongoDb(defaultSchema);
	
	static{
		System.setProperty("db.schema", defaultSchema);
	}
	
	@AfterClass
	public static void tearDown(){
		System.clearProperty("db.schema");
	}

	public void testInit() throws UnknownHostException, InvalidUserException {
		Assert.assertNotNull(dao.getDb());
	}

	@Test
	@UsingDataSet(locations={"/com/exo/model/service/EchoServiceTest#testFindCampaignCollection.json"}, 
	loadStrategy=LoadStrategyEnum.CLEAN_INSERT)
	public void testFindRecordById() throws Exception {

		Set<String> collectionNames = dao.getDb().getCollectionNames();
		Assert.assertThat("Campaign collection should exist!", "campaigns",
				isIn(collectionNames));

		DBCollection record = dao.findCollection("campaigns");
		
		Assert.assertNotSame(0, record.count());
	}
	
}
