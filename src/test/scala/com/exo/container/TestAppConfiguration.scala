package com.exo.container

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.config.BeanDefinition
import com.exo.engine.CampaignMaster
import com.exo.engine.CampaignWorker
import akka.actor.Props

class TestAppConfiguration extends AppConfiguration {

  override val campaignWorkerActor = bean("echo.engine.campaign-worker", scope = BeanDefinition.SCOPE_PROTOTYPE) {
    val ca = new CampaignWorker
    ca.mailService = new MockEchoMailService()
    ca.dataService = new MockEchoDataService()
    ca
  }
  
  //FIXME::move to spring
  override val campaignMasterActor = bean("echo.engine.campaign-master", scope = BeanDefinition.SCOPE_PROTOTYPE) {
    val ca = new CampaignMaster(getBean[Int]("echo.engine.campaign-master.number-of-workers"))
    ca.dataService = new MockEchoDataService()
    ca.props = Props(campaignWorkerActor())
    ca
  }
}
