package com.exo.engine
//import scala.collection.JavaConversions._
//import com.google.common.collect.Lists

trait Utils {

  def chunk[T](targetIds: List[T], numberOfWorkers: Int): List[List[T]] = {
    val elementsInAChunk = (targetIds.length * 1.0 / numberOfWorkers).ceil.toInt
    //Lists.partition(targetIds, elementsInAChunk).toList.map((el:java.util.List[T]) => el.toList)
    targetIds.grouped(elementsInAChunk).toList
  }

}

object Utils extends Utils