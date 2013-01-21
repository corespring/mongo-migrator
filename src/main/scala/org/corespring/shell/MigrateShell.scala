package org.corespring.shell

import org.corespring.models.{Migration, DBName, Script}
import com.mongodb.casbah.MongoURI
import sys.process.ProcessLogger

object MigrateShell {

  class ScriptLogger(val s: Script) extends ProcessLogger {

    var outLog: String = ""
    var errorLog: String = ""

    def hasError: Boolean = !errorLog.isEmpty

    def buffer[T](f: => T): T = f

    def out(s: => String) {
      outLog += (s + "\n")
    }

    def err(s: => String) {
      errorLog += (s + "\n")
    }
  }

  def run(dbName: DBName, migration: Migration): Boolean = {

    def mongoCmd: String = {
      List(
        Some("mongo"),
        Some(dbName.host + ":" + dbName.port + "/" + dbName.db),
        dbName.username.map("-u " + _),
        dbName.password.map("-p " + _)
      ).flatten.mkString(" ")
    }

    val loggers: List[ScriptLogger] = migration.scripts.map {
      sc =>
        import scala.sys.process._
        val logger = new ScriptLogger(sc)
        val cmd = (mongoCmd + " " + sc.name)
        println("running: [" + cmd + "]")
        cmd ! logger
        logger
    }

    loggers.foreach {
      l =>
        println("--------------")
        println("Running: " + l.s.name)
        println("--------------")
        println(l.outLog)
        if (l.hasError) {
          println("error: " + l.errorLog)
        }
        println("--------------")
    }

    val hasErrors = loggers.foldRight(false)(_.hasError || _)
    !hasErrors
  }
}
