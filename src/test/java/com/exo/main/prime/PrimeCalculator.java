package com.exo.main.prime;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.japi.Creator;

import com.exo.main.prime.actor.PrimeListener;
import com.exo.main.prime.actor.PrimeMaster;
import com.exo.main.prime.vo.NumberRangeMessage;

public class PrimeCalculator {
	
	static class MasterCreator implements Creator<PrimeMaster>{
		private ActorRef listener;
		private static final long serialVersionUID = 1L;
		
		public MasterCreator(ActorRef l){
			this.listener = l;
		}
		
		public PrimeMaster create() {
			return new PrimeMaster(10, listener);
		}
	}
	
	public void calculate(long startNumber, long endNumber) {
		// Create our ActorSystem, which owns and configures the classes
		ActorSystem actorSystem = ActorSystem.create("primeCalculator");

		// Create our listener
		final ActorRef primeListener = actorSystem.actorOf(
				Props.create(PrimeListener.class), "primeListener");

		// Create the PrimeMaster: we need to define an UntypedActorFactory so
		// that we can control
		// how PrimeMaster instances are created (pass in the number of workers
		// and listener reference
		ActorRef primeMaster = actorSystem.actorOf(Props.create(new MasterCreator(primeListener)), "primeMaster");
		// Start the calculation
		primeMaster.tell(new NumberRangeMessage(startNumber, endNumber), primeMaster);
	}

	public static void main(String[] args) {
		if (args.length < 2) {
			System.out
					.println("Usage: java com.geekcap.akka.prime.PrimeCalculator <start-number> <end-number>");
			System.exit(0);
		}

		long startNumber = Long.parseLong(args[0]);
		long endNumber = Long.parseLong(args[1]);

		PrimeCalculator primeCalculator = new PrimeCalculator();
		primeCalculator.calculate(startNumber, endNumber);
	}
}