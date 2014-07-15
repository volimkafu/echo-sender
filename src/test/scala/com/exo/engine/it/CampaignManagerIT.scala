package com.exo.engine.it

import org.springframework.scala.context.function.FunctionalConfigApplicationContext
import com.exo.container.{ AppConfiguration, BeansBuilder }
import com.exo.model.service.EchoDataService
import org.junit.{Assert,Test,Before,After}
import org.hamcrest.Matchers._
import com.exo.model.service.EchoServiceTest
import com.exo.model.service.impl.EchoDataServiceImpl
import org.junit.runner.RunWith
import org.junit.internal.runners.JUnit4ClassRunner
import com.exo.engine.impl.CampaignManagerImpl
import org.junit.Rule
import akka.testkit._

@RunWith(classOf[JUnit4ClassRunner])
class CampaignManagerIT  {

  @Rule
  val appConfig = classOf[AppConfiguration]
  
  @Before 
  def setup(){System.setProperty("env", "IT")}

  @Test
  def sendCampaignTest() {
    
    implicit val ctx = (BeansBuilder(appConfig)).context

    val service = ctx.getBean(classOf[EchoDataService])

    Assert.assertEquals("Data Service should be 'real', i.e. EchoDataServiceImpl",
      service.getClass(), classOf[EchoDataServiceImpl])

    //Receive a campaign id from a caller
    /**
     * Mongo search:
     * db.echo.contacts.find({"campaigns.campaign_id": "52fba54fce798c441400002b"}).sort({ "_id": 1}).skip(0).limit(30)
     */

    val campaignId = "52fba54fce798c441400002b"

    //Retrieve the campaign from db
    val campaign = service.findCampaignById(campaignId)

    //Send 
    
    import akka.actor.ActorDSL._
    implicit val actorSystem = (BeansBuilder(appConfig)).system
    implicit val i = inbox()

    val mngr = new CampaignManagerImpl(2, appConfig)
    mngr.send(campaign)
    
    val reply = i.receive()
    println("+++++++++++++++" + reply)
  }

  @After
  def teardown(){System.clearProperty("env")}
}
