package com.exo.engine

import com.exo.model.Campaign
import com.exo.model.Contact
import com.exo.model.service.EchoDataService
import akka.actor.{Actor, ActorRef, ActorLogging, Props, UntypedActor}
import akka.actor.actorRef2Scala
import net.sf.oval.constraint.NotNull
import net.sf.oval.constraint.NotEmpty
import com.exo.engine.letter_type._
import com.exo.engine.exception.CampaignMasterException

class CampaignMaster(val numberOfWorkers: Int = 1)
  extends Actor
  with Utils 
  with ActorLogging {


  @NotNull
  var dataService: EchoDataService = _

  @NotNull
  var props: Props = _

  private[this] var stepsTaken = 0
  private[this] var caller: ActorRef = _

  private[this] var workerRouter: ActorRef = _

  override def preStart(): Unit = {
    log.debug("Starting Campaign Master")
    workerRouter = context.actorOf(props.withRouter(new akka.routing.RoundRobinPool(numberOfWorkers)))
  }

  
  override def preRestart(reason: Throwable, message: Option[Any]) {
    log.error(reason, "Restarting due to [{}] when processing [{}]", reason.getMessage, message.getOrElse(""))
  }

  override def receive = {

    case ChainRequest(campaign) =>
      caller = sender
      log.info(s"Received request to send a campaign: {$campaign}")
      val contacts = fetchContactsForCampaign(campaign)
      if (contacts.size < 1)
        caller ! InvalidChainRequest(campaign, s"There are no contacts for requested campaign $campaign")

      try {
        sendToWorkers(campaign, contacts)
      } catch {
        case ex: Exception => errorToSender("Failed to launch a campaign. Received an error from workers...", ex)
      }

    case LinkSucceded(campaign, contacts) =>
      try {
        handleWorkerSuccessReply(campaign)
      } catch {
        case ex: Exception => errorToSender("Failed to understand workers reply...", ex)
      }

    case everythingElse =>
      log.error( "Master got an unexpected request. Throwing IllegalArgumentException...")
      sender ! new IllegalArgumentException(
        "From Master: Don't know how to handle" + everythingElse
          + "\n I deal with objects of type " + classOf[ChainRequest] + " and " + classOf[LinkSucceded])
  }

  protected def errorToSender(msg: String, ex: Exception) = sender ! new CampaignMasterException(msg, ex)

  protected def sendToWorkers(campaign: Campaign, contacts: List[Contact]): Unit = {

    //chop contacts into N chunks where N is a number of worker threads  
    val chunks = chunk(contacts, numberOfWorkers)
    log.debug("number of chunks is " + chunks.size)

    //tell each worker to process its chunk 
    chunks.foreach {

      chunk =>
        workerRouter.tell(LinkRequest(campaign, chunk), self)
    }
  }

  protected def handleWorkerSuccessReply(campaign: com.exo.model.Campaign): Unit = {
    stepsTaken += 1
    log.info(s"Received success reply from worker # ${stepsTaken}...")

    if (stepsTaken >= numberOfWorkers) {
      log.info(s"DONE processing request for campaign id ${campaign.getId()}")
      caller ! ChainSucceded(stepsTaken)
    }
  }

  private def fetchContactsForCampaign(campaign: Campaign): List[Contact] = {
    //fetch contacts for campaign 
    import scala.collection.JavaConverters._
    dataService.findContactsForCampaign(campaign.getId()).asScala.toList
  }

}
