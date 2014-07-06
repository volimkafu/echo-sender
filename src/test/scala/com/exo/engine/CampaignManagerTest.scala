package com.exo.engine

import java.util.ArrayList
import java.util.Arrays
import org.junit.Test
import com.exo.model.Campaign
import com.exo.engine.impl.CampaignManagerImpl
import org.junit.runner.RunWith
import org.junit.internal.runners.JUnit4ClassRunner
import akka.testkit.{ ImplicitSender, TestActorRef, TestKit }
import org.springframework.scala.context.function.FunctionalConfigApplicationContext
import com.exo.container.{ SpringExtentionImpl, TestAppConfiguration }
import com.exo.model.Campaign
import com.exo.container.TestAppConfiguration
import com.exo.engine.impl.CampaignManagerImpl
import akka.actor.{ ActorSystem, Props }

@RunWith(classOf[JUnit4ClassRunner])
class CampaignManagerTest extends TestKit(FunctionalConfigApplicationContext(
  classOf[TestAppConfiguration]).getBean(classOf[ActorSystem]))
  with ImplicitSender {
  @Test
  def testCreateSenders() {
    val targetIds = new ArrayList[String](Arrays.asList(
      "alla@ekobuzz.com", "anil@ekobuzz.com",
      "ukraine@earthlink.net", "anil@jaising.net", "dummy@dummy.com",
      "support@ekobuzz.com"))

    val campaign = new Campaign()
    campaign.setTargetIds(targetIds)

    val workersCount = if (targetIds.size > 10) 10 else targetIds.size
    val manager = new CampaignManagerImpl(workersCount, classOf[TestAppConfiguration])
    manager.send(campaign)
  }

}
