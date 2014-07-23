package com.exo.tools

import scala.io.Source
import scala.io.Codec
import java.io.BufferedWriter
import java.io.File

object ConcatContactsAndRemoveDupes extends App with FileUtils {

  override def main(args: Array[String]) {

    if (args.length == 1) {
      //      val dir = new File(args(0))
      //      val files = recursiveListFiles(dir)
      //      
      //      val regex = """(?i)\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,4}\b""".r //email regex
      //      val allContacts =
      //        //(
      //            for (contacts <- files) yield Source.fromFile(contacts)(Codec.ISO8859).getLines().toMap(x => x)            
      ////).flatten.toList
      //
      //     // write(allContacts, "all_contacts.csv")

      val regex = """(?i)\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,4}\b""".r //email regex
      
      val source = scala.io.Source.fromFile(args(0))

      val graph = source.getLines.foldLeft[Map[String, String]](Map.empty withDefaultValue "") {
          (email, line) =>  
          
          Map((regex findFirstMatchIn line).toString -> line) 
      }

      source.close()
      
      graph.foreach(println)

    } else
      println("Provide dir to read files from")
  }

}