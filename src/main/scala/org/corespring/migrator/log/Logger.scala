package org.corespring.migrator.log

import java.util.logging.{Logger => JavaLogger, ConsoleHandler, LogRecord, Handler}

object Logger extends {

  val handler = new ConsoleHandler()

  //TODO: Flesh this out
  def get(name: String): JavaLogger = {

    val log = JavaLogger.getLogger(name)
    log
  }

  implicit def unwrap(a: Any): String = a.toString
}
