package com.exo.engine

import scala.collection.JavaConversions.seqAsJavaList
import scala.concurrent.duration.DurationInt
import org.junit.Rule
import org.junit.Test
import org.junit.internal.runners.JUnit4ClassRunner
import org.junit.runner.RunWith
import org.scalatest.BeforeAndAfterAll
import org.scalatest.FlatSpecLike
import org.scalatest.Matchers
import org.springframework.scala.context.function.FunctionalConfigApplicationContext
import com.exo.container.BeansBuilder
import com.exo.container.TestAppConfiguration
import com.exo.engine.impl.listener.ActorSystemExecutionListener
import com.exo.engine.letter_type.ChainRequest
import com.exo.engine.letter_type.ChainSucceded
import com.exo.model.Campaign
import akka.actor.ActorSystem
import akka.testkit.ImplicitSender
import akka.testkit.TestKit

@RunWith(classOf[JUnit4ClassRunner])
class CampaignMasterTest
  extends TestKit(FunctionalConfigApplicationContext(classOf[TestAppConfiguration]).getBean(classOf[ActorSystem]))
  with ImplicitSender
  with Matchers {

  @Rule
  val contactIds = (for (id <- (1 to 13)) yield toString).toList

  @Rule
  val numberOfWorkers = BeansBuilder(classOf[TestAppConfiguration]).numberOfWorkers

  @Rule 
  val masterRef = BeansBuilder(classOf[TestAppConfiguration]).campaignMaster
  
  @Rule
  val campaign = new Campaign(
    "123",
    "campaign worker test",
    "campaign worker",
    "campaign@worker.test",
    "Test",
    "empty")

  @Test
  def handleChainRequestTest() {
    //    Send a letter
    masterRef ! new ChainRequest(campaign)

    expectMsgPF(hint = s"successfully ran $numberOfWorkers workers. ") {
      case ChainSucceded(stepsTaken) =>
        stepsTaken should equal(numberOfWorkers)

      //TODO::Capture an error?
    }
  }
}