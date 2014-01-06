package org.corespring.migrator.commands

case class LogTest extends BaseCommand{
  def begin(): Unit = {
    trace("log-test trace")
    debug("log-test debug")
    info("log-test info")
    warn("log-test warn")
    error("log-test error")
  }

  def cleanup(): Unit = {}
}
