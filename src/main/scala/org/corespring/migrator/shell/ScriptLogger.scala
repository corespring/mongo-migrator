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
    debug(s)
    outLog += (s + "\n")
  }

  def err(s: => String) {
    error(s)
    errorLog += (s + "\n")
  }

  def printToLogger() {
    debug("--------------")
    debug("Running: " + s.name)
    debug("--------------")
    debug(outLog)
    if (hasError) {
      debug("error: " + errorLog)
    }
    debug("--------------")
  }
}
