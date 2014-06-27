package com.exo.engine

import org.junit.Rule
import org.junit.Test
import org.junit.internal.runners.JUnit4ClassRunner
import org.junit.runner.RunWith
import org.springframework.scala.context.function.FunctionalConfigApplicationContext
import org.springframework.test.annotation.DirtiesContext
import com.exo.container.SpringExtentionImpl
import com.exo.container.TestAppConfiguration
import com.exo.engine.letter_type.{LinkRequest, LinkSucceded}
import com.exo.model.{Campaign, Contact}
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.testkit.ImplicitSender
import akka.testkit.TestKit

@RunWith(classOf[JUnit4ClassRunner])
class CampaignWorkerTest
  extends TestKit(FunctionalConfigApplicationContext(classOf[TestAppConfiguration]).getBean(classOf[ActorSystem]))
  with ImplicitSender {

  @Rule
  val contacts = List[Contact](
    new Contact("a@a.com", "A First", "A Last"),
    new Contact("b@b.com", "B First", "B Last"),
    new Contact("c@c.com", "C First", "C Last"),
    new Contact("d@d.com", "D First", "D Last"),
    new Contact("e@e.com", "E First", "E Last"),
    new Contact("f@f.com", "F First", "F Last"),
    new Contact("g@g.com", "G First", "G Last"),
    new Contact("h@h.com", "H First", "H Last"),
    new Contact("j@j.com", "J First", "J Last"),
    new Contact("k@k.com", "K First", "K Last"))

  @Rule
  val campaign = new Campaign(
    "123",
    "campaign worker test",
    "campaign worker",
    "campaign@worker.test",
    "Test",
    "empty")

  @Rule
  val actorRef = {
    implicit val appContext = FunctionalConfigApplicationContext(classOf[TestAppConfiguration])
    val prop = SpringExtentionImpl(system).props("echo.engine.campaign-worker")
    system.actorOf(prop, "campaign-worker")
  }

  @Test
  def invalidRequestTest() {

    val future = actorRef ! contacts

    expectMsgPF(hint = "correctly rejected the request. ") {
      case ex: IllegalArgumentException => //
    }
  }

  @Test
  def integrationTest() {

    val future = actorRef ! LinkRequest(campaign, contacts)

    expectMsgPF(hint = "successfully ran one worker. ") {
      case LinkSucceded(campaign, contacts) => //just check that it's success, not error
    }
  }
}