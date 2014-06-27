package com.exo.container

import org.junit.runner.RunWith
import spock.lang.Specification
import org.scalatest.junit.JUnitRunner
import org.scalatest.FlatSpecLike
import org.springframework.scala.context.function.FunctionalConfigApplicationContext
import akka.actor.ActorSystem
import com.exo.email.service.EchoMailServiceImpl
import akka.actor.ActorRef

@RunWith(classOf[JUnitRunner])
class AppConfigurationTest extends FlatSpecLike {

  it should "create Spring App Context" in {

    // create a Spring context
    implicit val ctx = FunctionalConfigApplicationContext(classOf[AppConfiguration])
    assert(ctx != null, "App Context shoud've been created")

    // get hold of the actor system
    val system = ctx.getBean(classOf[ActorSystem])
    assert(system != null, "Echo Actors System should've been created")

    val mailService = ctx.getBean("echo.mail-service")
    assert(mailService != null, "Email Service bean should've been in app. context")
    assert(mailService.asInstanceOf[EchoMailServiceImpl].getHostName().matches("^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$"), 
        "Mail host should've been set")

    // use the Spring Extension to create props for a named actor bean
    val prop = SpringExtentionImpl(system).props("echo.engine.campaign-worker")

    // use the Spring Extension to create props for a named actor bean
    val counter: ActorRef = system.actorOf(prop, "counter")
    assert(counter != null, "Should be able to fetch actor ref from app. context")

  }
}