package com.exo.tools

import scala.io.Source
import scala.io.Codec
import java.io.BufferedWriter
import java.io.File

object ConcatContactsAndRemoveDupes extends App with FileUtils {

  override def main(args: Array[String]) {

    if (args.length == 1) {
      val dir = new File(args(0))
      val files = recursiveListFiles(dir)
files.foreach(println)
      val allContacts =
        (for (contacts <- files) yield Source.fromFile(contacts)(Codec.ISO8859).getLines().toList)
          .flatten.toList

      write(allContacts, "all_contacts.csv")
    }else
      println("Provide dir to read files from")
  }

}