package com.exo.engine

import org.slf4j.LoggerFactory
import com.exo.email.EmailMessage
import com.exo.email.EmailTarget
import com.exo.email.service.EchoMailService
import com.exo.model.Campaign
import com.exo.model.Contact
import akka.actor.Actor
import akka.actor.UntypedActor
import akka.actor.actorRef2Scala
import com.exo.engine.letter_type.LinkSucceded
import com.exo.engine.letter_type.LinkRequest

class CampaignWorker extends Actor {

  val log = LoggerFactory.getLogger(classOf[CampaignWorker])

//FIXME:: @scala.reflect.BeanProperty -- injecting from scala AppConfig, will uncomment when inject from spring config 
  var mailService: EchoMailService[EmailMessage] = _

  override def receive = {

    case LinkRequest(campaign, contacts) =>
      log.info("Attempting connection to smtp server ...")
 
      mailService.connect();
 
      log.info("Connected SUCCESSFULLY. Sending...")

      var countSuccessTargets = 0
      var countFailureTargets = 0

      contacts.map {
        contact =>
          val hasBeenSent = sendEmail(campaign, contact)

          if (hasBeenSent) {
            countSuccessTargets += 1
          } else
            countFailureTargets += 1

          contact.markAsSent(campaign.getId(), hasBeenSent)
      }

      log.info(s"Sent to (success, failure) : ($countSuccessTargets, $countFailureTargets). Done.")
      // Send a notification back to the sender
      sender ! LinkSucceded(campaign, contacts)

    case _ =>
      log.error("Got an unexpected request. Throwing IllegalArgumentException..." )
      sender ! new IllegalArgumentException(
        "Don't know how to handle..."
          + " I deal with objects of type " + classOf[LinkRequest])
  }

  private def sendEmail(campaign: Campaign, contact: Contact): Boolean = {
    val target = new EmailTarget(contact.getId())
    target.setEmail(contact.getEmail())
    val message = new EmailMessage(
      campaign.getId(),
      campaign.getContent(),
      campaign.getFromEmail(),
      campaign.getFromName(),
      campaign.getName(),
      campaign.getSubject(),
      target);

    log.debug("sending message: " + message)
    mailService.send(message);
  }

}
