package com.exo.container

import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.ApplicationContext
import org.springframework.scala.context.function.FunctionalConfigApplicationContext
import org.springframework.scala.context.function.FunctionalConfiguration
import com.exo.email.EmailMessage
import com.exo.email.service.EchoMailService
import com.exo.engine.CampaignWorker
import akka.actor.ActorRef
import akka.actor.ActorSystem
import com.exo.engine.CampaignMaster
import com.exo.model.service.EchoDataService
import akka.actor.Props

object AppConfiguration {
  val appConfiguration = FunctionalConfigApplicationContext(classOf[AppConfiguration])
  val actorSystem = appConfiguration.getBean(classOf[ActorSystem])
}

class AppConfiguration extends FunctionalConfiguration {

  importXml("classpath:/sender-context.xml")

  /**
   * Load implicit context
   */
  implicit val ctx = beanFactory.asInstanceOf[ApplicationContext]

  /**
   * Actor system singleton for this application.
   */
  val actorSystem = bean() {
    val system = ActorSystem("EchoSenderSystem")
    // initialize the application context in the Akka Spring Extension
    SpringExtentionImpl(system)
    system
  }

  val campaignWorkerActor = bean("echo.engine.campaign-worker", scope = BeanDefinition.SCOPE_PROTOTYPE) {
    val ca = new CampaignWorker
    ca.mailService = getBean[EchoMailService[EmailMessage]]("echo.mail-service")
    ca.dataService = getBean[EchoDataService]("echo.data-service")
    ca
  }
  
  val campaignMasterActor = bean("echo.engine.campaign-master", scope = BeanDefinition.SCOPE_PROTOTYPE){
    val master = new CampaignMaster(getBean[Int]("echo.engine.campaign-master.number-of-workers"))
    master.dataService = getBean[EchoDataService]("echo.data-service")
    master.props = Props(campaignWorkerActor())
    master
  }

}