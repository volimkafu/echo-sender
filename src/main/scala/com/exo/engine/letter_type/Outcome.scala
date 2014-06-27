package com.exo.engine.letter_type

import com.exo.model.{Campaign, Contact}

sealed abstract class Outcome (val status:String){
  def plus(description:String):String = s"$status , $description"
}

case class LinkSucceded(campaign:Campaign, contacts:List[Contact]) extends Outcome("link-success")
case class ChainSucceded (links:Int) extends Outcome("chain-success")
case class InvalidChainRequest(campaign:Campaign, reason:String) extends Outcome(reason)

