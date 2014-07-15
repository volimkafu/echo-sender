package com.exo.engine.impl

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.japi.Creator
import com.exo.engine.CampaignMaster
import com.exo.engine.Manager
import com.exo.model.Campaign
import org.springframework.scala.context.function.FunctionalConfigApplicationContext
import com.exo.container.SpringExtentionImpl
import com.exo.container.BeansBuilder
import org.springframework.scala.context.function.FunctionalConfiguration
import org.slf4j.LoggerFactory
import akka.actor.dsl.Inbox
import com.exo.engine.letter_type.ChainRequest


class CampaignManagerImpl(
  val numberOfWorkers: Int,
  val appConfig : Class[_ <: FunctionalConfiguration])
  extends Manager[Campaign] {
  
    val log = LoggerFactory.getLogger(classOf[CampaignManagerImpl])

  private class MasterCreator(val numberOfWorkers: Int) extends Creator[CampaignMaster] {

    override def create(): CampaignMaster = {
      new CampaignMaster(numberOfWorkers)
    }
  }

  
  override def send(campaign: Campaign) {

    log.info("===-{Received a request to process campaign " + campaign + "}-==========")
    val masterRef = BeansBuilder(appConfig).campaignMaster
    
    masterRef ! new ChainRequest(campaign)
    
  }

}
