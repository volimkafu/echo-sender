package com.exo.engine.it

import org.springframework.scala.context.function.FunctionalConfigApplicationContext
import com.exo.container.{AppConfiguration, BeansBuilder}
import com.exo.model.service.EchoDataService
import org.junit.Assert
import org.hamcrest.Matchers._
import com.exo.model.service.EchoServiceTest
import com.exo.model.service.impl.EchoDataServiceImpl

@org.junit.runner.RunWith(classOf[org.junit.internal.runners.JUnit4ClassRunner])
class CampaignManagerIT {

  @org.junit.Test
  def sendCampaignTest() {
    implicit val ctx = BeansBuilder(classOf[AppConfiguration]).context
    
    val service = ctx.getBean(classOf[EchoDataService])
    
    Assert.assertEquals("Data Service should be 'real', i.e. EchoDataServiceImpl",
        service.getClass(), classOf[EchoDataServiceImpl])

    //Receive a campaign id from a caller
    /**
     * Mongo search:
     * db.echo.contacts.find({"campaigns.campaign_id": "530cca92ce798c4815000029"}).sort({ "_id": 1}).skip(0).limit(30)
     */

    val campaignId = "530cca92ce798c4815000029"

    //Retrieve the campaign from db
    val contacts = service.findContactsForCampaign(campaignId)
    
    Assert.assertThat(s"There are 1409 contacts in the campaign id $campaignId", contacts.size(), is(1409))

    //Send 
  }

}
