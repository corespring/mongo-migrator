package org.corespring.migrator.shell

import org.corespring.migrator.models.{DbName, Script}
import exceptions.ShellException
import java.io.{File,FileWriter}
import grizzled.slf4j.Logging


case class CmdResult(name:String,out:String,err:String,exitCode:Int)

trait BaseShell extends Logging {

  def run(dbName: DbName, scripts: List[Script]): Boolean = {

    val shellFile = "baseShell_tmp.js"

    def writeToFile(s:String) : File = {
      debug("write to file: " + shellFile)
      val fw = new FileWriter(shellFile)
      fw.write(s)
      fw.close()
      new File(shellFile)
    }

    val cmdResults : List[CmdResult] = scripts.map {
      sc =>
        import scala.sys.process._
        val logger = new ScriptLogger(sc)
        val f : File = writeToFile(sc.contents)
        val cmd = (dbName.toCmdLine + " " + f.getPath)
        info("running: [" + cmd + "]")
        val exitCode = cmd ! logger
        f.delete()
        CmdResult(sc.name, logger.outLog, logger.errorLog, exitCode)
    }

    val errorResults = cmdResults.filterNot( _.exitCode == 0)

    errorResults match {
      case Nil => true
      case _ => {
        val msg = errorResults.map( r => r.name + "\n" + r.err).mkString("\n")
        throw new ShellException(msg)
      }
    }
  }

}

