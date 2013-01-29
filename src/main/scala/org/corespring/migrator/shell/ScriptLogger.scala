package org.corespring.migrator.shell

import sys.process.ProcessLogger
import org.corespring.migrator.models.Script
import grizzled.slf4j.Logging

class ScriptLogger(val s: Script) extends ProcessLogger with Logging {

  var outLog: String = ""
  var errorLog: String = ""

  def hasError: Boolean = !errorLog.isEmpty

  def buffer[T](f: => T): T = f

  def out(s: => String) {
    info(s)
    outLog += (s + "\n")
  }

  def err(s: => String) {
    error(s)
    errorLog += (s + "\n")
  }

  def printToLogger() {
    println("--------------")
    println("Running: " + s.name)
    println("--------------")
    println(outLog)
    if (hasError) {
      println("error: " + errorLog)
    }
    println("--------------")
  }
}
