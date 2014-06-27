package com.exo.engine.letter_type

import com.exo.model.{Campaign, Contact}

sealed abstract class Request
case class ChainRequest(campaign:Campaign) extends Request
case class LinkRequest(campaign:Campaign, contacts:List[Contact]) extends Request
