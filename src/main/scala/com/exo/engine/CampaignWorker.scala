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
import com.exo.model.service.EchoDataService
import ch.qos.logback.classic.joran.action.LoggerAction
import akka.actor.ActorLogging
import com.exo.email.EmailValidator

class CampaignWorker extends Actor with ActorLogging with EmailValidator {

  var mailService: EchoMailService[EmailMessage] = _

  var dataService: EchoDataService = _

  override def receive = {

    case LinkRequest(campaign, contacts) =>
      log.info("Attempting connection to smtp server ...")

      mailService.connect();

      log.info("Connected SUCCESSFULLY. Sending...")

      var countSuccessTargets = 0
      var countFailureTargets = 0

      contacts.map {
        contact =>
          log.debug("validating contact's email: " + contact)

          val hasBeenSent :Boolean =
            if (validate(contact.getEmail())) {
             // sendEmail(campaign, contact)
              true
            }else
              false

          if (hasBeenSent) {
            countSuccessTargets += 1
          } else{
            log.debug("Target's email address [{}] is invalid..." + contact.getEmail())
            countFailureTargets += 1
          }

          contact.markAsSent(campaign.getId(), hasBeenSent)
          dataService.updateContact(contact)
      }

      log.info(s"Sent to (success, failure) : ($countSuccessTargets, $countFailureTargets). Done.")
      // Send a notification back to the sender
      sender ! LinkSucceded(campaign, contacts)

    case everythingElse =>
      log.error("Worker got an unexpected request. Throwing IllegalArgumentException...")
      sender ! new IllegalArgumentException(
        "From Worker: Don't know how to handle " + everythingElse + "..."
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
      target)

    log.debug("sending message: " + message)
    try {
      mailService.send(message)
    } catch {
      case ex: Throwable =>
        log.error(ex, "Failed to send a message" + message)
        false
    }
  }
  
  private def validateTargetEmailAddress(campaign: com.exo.model.Campaign, contact: com.exo.model.Contact): AnyVal = {
    if (!validate(contact.getEmail())) {
      log.debug("Target's email address [{}] is invalid..." + contact.getEmail())
    } else {
      sendEmail(campaign, contact)
    }
  }

}
