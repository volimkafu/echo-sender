package com.exo.engine;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import com.exo.engine.impl.CampaignManagerImpl;
import com.exo.model.Campaign;

public class CampaignManagerTest {

	@Test
	public void testCreateSenders() throws Exception {
		ArrayList<String> targetIds = new ArrayList<String>(Arrays.asList(
				"alla@ekobuzz.com", "anil@ekobuzz.com",
				"ukraine@earthlink.net", "anil@jaising.net", "dummy@dummy.com",
				"support@ekobuzz.com"));

		Campaign campaign = new Campaign();
		campaign.setTargetIds(targetIds);

		int workersCount = targetIds.size();
		workersCount = workersCount > 10 ? 10 : workersCount;
		Manager<Campaign> manager = new CampaignManagerImpl(workersCount);
		
		manager.send(campaign);
	}
	
	public static void main(String[] args) throws Exception {
		new CampaignManagerTest().testCreateSenders();
	}

}
