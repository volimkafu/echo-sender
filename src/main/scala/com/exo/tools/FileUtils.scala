package com.exo.tools

import java.io.BufferedWriter
import java.io.FileWriter
import java.io.File

trait FileUtils {

  def write(content: List[String], outputFileName: String) {
    val w = new BufferedWriter(new FileWriter(outputFileName))
    content.foreach(x => w.write(x + util.Properties.lineSeparator))
    w.close()
  }
  
  
  def recursiveListFiles(f: File): Array[File] = {
    val these = f.listFiles
    these ++ these.filter(_.isDirectory).flatMap(recursiveListFiles)
  }
}