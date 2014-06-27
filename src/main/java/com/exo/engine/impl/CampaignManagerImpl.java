package com.exo.engine.impl;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.japi.Creator;

import com.exo.engine.CampaignMaster;
import com.exo.engine.Manager;
import com.exo.model.Campaign;

public class CampaignManagerImpl implements Manager<Campaign> {

	private final ActorSystem actorSystem;

	private final int numberOfWorkers;

	private static class MasterCreator implements Creator<CampaignMaster> {

		private final int numberOfWorkers;

		private static final long serialVersionUID = 1L;

		private MasterCreator(int numberOfWorkers) {
			this.numberOfWorkers = numberOfWorkers;
		}

		@Override
		public CampaignMaster create() {
			return new CampaignMaster(numberOfWorkers);
		}
	}

	public CampaignManagerImpl(int numberOfWorkers) {
		actorSystem = ActorSystem.create("campaignSender");
		this.numberOfWorkers = numberOfWorkers;
	}

	@Override
	public void process(Campaign instance) throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public void save(Campaign instance) {

	}

	@Override
	public void send(Campaign instance) throws Exception {
		ActorRef campaignMaster = actorSystem.actorOf(
				Props.create(new MasterCreator(numberOfWorkers)),
				"campaignMaster");

		campaignMaster.tell(instance, campaignMaster);
	}

}
