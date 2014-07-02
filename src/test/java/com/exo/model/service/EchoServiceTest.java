package com.exo.model.service;

import static com.lordofthejars.nosqlunit.mongodb.InMemoryMongoDb.InMemoryMongoRuleBuilder.newInMemoryMongoDbRule;
import static com.lordofthejars.nosqlunit.mongodb.MongoDbRule.MongoDbRuleBuilder.newMongoDbRule;
import static org.hamcrest.Matchers.isIn;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.List;

import javax.inject.Inject;

import org.junit.AfterClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.exo.model.Campaign;
import com.exo.model.Contact;
import com.exo.model.EmailStatus;
import com.exo.model.exception.InvalidModelObjectException;
import com.exo.model.service.exception.EchoDataServiceException;
import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import com.lordofthejars.nosqlunit.mongodb.InMemoryMongoDb;
import com.lordofthejars.nosqlunit.mongodb.MongoDbRule;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/sender-context.xml" })
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class EchoServiceTest {

	static final String defaultSchema = "test";

	@Inject
	EchoDataService service;

	@Autowired
	private ApplicationContext applicationContext;

	@ClassRule
	public static InMemoryMongoDb inMemoryMongoDb = newInMemoryMongoDbRule()
			.build();

	@Rule
	public MongoDbRule mongoDbRule = newMongoDbRule().defaultSpringMongoDb(
			defaultSchema);

	static {
		System.setProperty("db.schema", defaultSchema);
	}

	@AfterClass
	public static void tearDown() {
		System.clearProperty("db.schema");
	}

	@Test
	@UsingDataSet(locations = { "/com/exo/model/service/EchoServiceTest#testFindCampaignCollection.json" }, loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
	public void testFindCampaignCollection() throws Exception {
		List<Campaign> cs = service.findCampaignCollection(0, 1);

		assertEquals(1, cs.size());
		assertThat("campaign id cannot be null", cs.get(0).getId(),
				notNullValue());
	}

	@Test
	@UsingDataSet(locations = { "/com/exo/model/service/EchoServiceTest#testFindCampaignCollection.json" }, loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
	public void testFindCampaignById() throws Exception {
		List<Campaign> cs = service.findCampaignCollection(0, 1);
		Campaign source = cs.get(0);
		Campaign target = service.findCampaignById(source.getId());
		assertNotNull(target);
		assertEquals(target.getId(), source.getId());
	}

	@Test
	@UsingDataSet(locations = {
			"/com/exo/model/service/EchoServiceTest#testFindCampaignCollection.json",
			"/com/exo/model/service/contacts.json" }, loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
	public void testFindContactsByCampaignId() throws Exception {

		List<Campaign> cs = service.findCampaignCollection(0, 1);
		Campaign campaign = cs.get(0);

		List<Contact> contacts = service.findContactsForCampaign(campaign.getId());
		assertEquals(2, contacts.size());
	}

	@Test
	@UsingDataSet(locations = {
			"/com/exo/model/service/EchoServiceTest#testFindCampaignCollection.json",
			"/com/exo/model/service/contacts.json" }, loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
	public void testFindContactById() throws EchoDataServiceException{
		List<Campaign> cs = service.findCampaignCollection();
		Campaign campaign = cs.get(0);

		List<Contact> contacts = service.findContactsForCampaign(campaign.getId());
		
		Contact source = contacts.get(0);
		Contact target = service.findContactById(source.getId());
		assertEquals(target.getId(), source.getId());
	}
	
	@Test
	@UsingDataSet(locations = {
			"/com/exo/model/service/contacts.json" }, loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
	public void testContactCampaignUpdate() throws EchoDataServiceException, InvalidModelObjectException {
		
		final String contactId = "52ddf9e456340cad07ce9ee5";
		final String campaignId = "52fba54fce798c441400002b";
		
		Contact contact = service.findContactById(contactId);
		int numberOfCampaignStatuses = contact.getCampaign(campaignId).getStatuses().size();
		
		contact.markAsSent(campaignId, true);
		contact.markAsSent(campaignId, false);
		
		service.updateContact(contact);
		
		Contact savedContact = service.findContactById(contactId);
		assertEquals("The campaign should have another status", 
				savedContact.getCampaign(campaignId).getStatuses().size(), numberOfCampaignStatuses + 2);
		
		assertThat("The campaign should have another status", EmailStatus.SENT_SUCCESSFULLY.getStatus(),
				isIn(savedContact.getCampaign(campaignId).getStatuses()));
		
		assertThat("The campaign should have another status", EmailStatus.SEND_FAILED.getStatus(),
				isIn(savedContact.getCampaign(campaignId).getStatuses()));
	}
}
