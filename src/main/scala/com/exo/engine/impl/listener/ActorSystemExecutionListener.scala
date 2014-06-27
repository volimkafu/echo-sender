package com.exo.engine.impl.listener;

import akka.actor.UntypedActor
import org.slf4j.LoggerFactory

class ActorSystemExecutionListener extends UntypedActor {

   val log = LoggerFactory.getLogger(classOf[ActorSystemExecutionListener])

  override def onReceive(message: Any): Unit = {
  
		log.info("Shutting the system down")
		context.system.shutdown
//		if (message instanceof Result) {
//			Result result = (Result) message;
//
//			System.out.println("Results: ");
//			for (Long value : result.getResults()) {
//				System.out.print(value + ", ");
//			}
//			System.out.println();
//
//			// Exit
//			getContext().system().shutdown();
//		} else {
//			unhandled(message);
//		}
	}
}
