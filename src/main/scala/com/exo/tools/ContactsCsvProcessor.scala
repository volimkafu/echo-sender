package com.exo.tools

import java.io.BufferedWriter
import java.io.FileWriter

import scala.io.Codec
import scala.io.Source

object ContactsCsvProcessor extends Application {

  override def main(args: Array[String]) {

    if (args.length == 2) {
      println (s"The contacts file is: ${args(0)}")
      println (s"The emails file is: ${args(1)}")
      
      val outputFile = args(0).replaceFirst(".csv", "_VALIDATED.csv")
      println(s"The output file will be: ${outputFile}")
      
      import collection.JavaConversions._
      val contacts = Source.fromFile(args(0))(Codec.ISO8859).getLines().toList
      val emails = Source.fromFile(args(1))(Codec.ISO8859).getLines().map(_.split(",")(0)).toList
      println (s"The contacts file size is: ${contacts.size}")
      println (s"The emails file size is: ${emails.size}")
      
      val results = contacts.filter(contact => 
        emails.exists(x => contact.contains(x))
      )
      println (s"The matched file size is: ${results.size}")
      
      
      val w = new BufferedWriter(new FileWriter(outputFile))
      results.foreach(x => w.write(x + util.Properties.lineSeparator))
      w.close() 

    } else
      Console.err.println("Please enter contacts file name and emails file name to match")

  }
}