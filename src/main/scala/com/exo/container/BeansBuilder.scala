package com.exo.container

import org.springframework.scala.context.function.FunctionalConfigApplicationContext
import akka.actor.ActorRef
import akka.actor.ActorSystem
import org.springframework.scala.context.function.FunctionalConfiguration

object BeansBuilder {
  def apply(configType : Class[_ <: FunctionalConfiguration]) = new BeansBuilder(configType)
}

class BeansBuilder (configType: Class[ _ <: FunctionalConfiguration]) {

  // create a Spring context
  implicit val context = FunctionalConfigApplicationContext(configType)

  // get hold of the actor system
  val system: ActorSystem = {
    assert(context != null, "App Context shoud've been created")
    context.getBean(classOf[ActorSystem])
  }
  
  val campaignMaster = {
    assert(system != null, "Echo Actors System should've been created")
    val props = SpringExtentionImpl(system).props("echo.engine.campaign-master")
    system.actorOf(props, "campaign-master")
  }
  
  val campaignWorker = {
    assert(system != null, "Echo Actors System should've been created")
    val props = SpringExtentionImpl(system).props("echo.engine.campaign-worker")
    system.actorOf(props, "campaign-worker")
  }
  
  val numberOfWorkers =  context.getBean("echo.engine.campaign-master.number-of-workers", classOf[Int])
}