package com.exo.container

import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader
import org.springframework.context.ApplicationContext
import org.springframework.context.support.GenericApplicationContext
import org.springframework.scala.context.function.FunctionalConfigApplicationContext
import org.springframework.scala.context.function.FunctionalConfiguration
import com.exo.email.EmailMessage
import com.exo.email.service.EchoMailService
import com.exo.engine.CampaignMaster
import com.exo.engine.CampaignWorker
import com.exo.model.service.EchoDataService
import akka.actor.ActorSystem
import akka.actor.Props
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ImportResource
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader

object AppConfiguration {
  val appConfiguration = FunctionalConfigApplicationContext(classOf[AppConfiguration])
  appConfiguration.refresh()

  val actorSystem = appConfiguration.getBean(classOf[ActorSystem])
}

class AppConfiguration extends FunctionalConfiguration {

  //  importXml("classpath:/prod-sender-context.xml")
  importClass(classOf[DsConfig])

  /**
   * Load implicit context
   */
  implicit val ctx = beanFactory.asInstanceOf[ApplicationContext]

  override def importClass(annotatedClasses: Class[_]*) {
   
    val beanDefinitionReader = new AnnotatedBeanDefinitionReader(beanRegistry, environment)
    beanDefinitionReader.register(annotatedClasses: _*)
    beanDefinitionReader.getRegistry().getBeanDefinitionNames().foreach(x => println ("DFDFDFDF"+ x))
  }

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
    ca.mailService = mailService
    ca.dataService = dataService
    ca
  }

  val campaignMasterActor = bean("echo.engine.campaign-master", scope = BeanDefinition.SCOPE_PROTOTYPE) {
    val master = new CampaignMaster(getBean[Int]("echo.engine.campaign-master.number-of-workers"))
    master.dataService = dataService
    master.props = Props(campaignWorkerActor())
    master
  }

  val dataService = getBean[EchoDataService]("echo.data-service")

  val mailService = getBean[EchoMailService[EmailMessage]]("echo.mail-service")

}