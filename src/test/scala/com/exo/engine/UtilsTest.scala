package com.exo.engine

import org.junit.Rule
import org.junit.Test
import org.junit.Assert
import org.junit.runner.RunWith

@RunWith(classOf[org.junit.internal.runners.JUnit4ClassRunner])
class UtilsTest {

  @Rule
  val contactIds = (for (id <- (1 to 11)) yield toString).toList

  @Test
  def chunkedTargetsTest() {
    var chunkedContactIds = Utils.chunk(contactIds, 2)
    println(chunkedContactIds)
    Assert.assertEquals(2, chunkedContactIds.length)	
    Assert.assertEquals(6, chunkedContactIds(0).length)	
    Assert.assertEquals(5, chunkedContactIds(1).length)	
  }
}