package com.exo.tools

import java.io.{ BufferedWriter, File, FileWriter }

import scala.io.{ Codec, Source }

import org.scalatest.{ FlatSpec, Matchers }

class ContactsCsvProcessor extends FlatSpec with Matchers with FileUtils {

  val eclipse_prefix = "src/main/resources/"

  "The Eko contacts file of size 123,810 + header" should "contain cleansed emails " in {

    val emails = Source.fromFile(
        new File(s"${eclipse_prefix}SalesMarketing_123810_alla/770002-SalesMarketing_123810_alla-CERTINT.csv"))(Codec.ISO8859)
        	.getLines().map(_.split(",")(0)).toList
        	
    println(s"The emails file size is: ${emails.size}")

    val contacts = Source.fromFile(new File(s"${eclipse_prefix}contacts/SalesMarketing_123810.csv"))(Codec.ISO8859).getLines.toList
      .filter(contact => {
//        println(contact)
        emails.exists(email => contact.contains(email))
      }).toList
//
    println(s"The matched file size is: ${contacts.size}")

//        write(contacts, s"${eclipse_prefix}contacts/SalesMarketing_123810_VALIDATED.csv")
  }
}