package org.corespring.migrator.shell

import org.corespring.migrator.models.{DbName, Script}
import exceptions.ShellException
import java.io.{File,FileWriter}
import grizzled.slf4j.Logging


case class CmdResult(name:String,out:String,err:String,exitCode:Int)

trait BaseShell extends Logging {

  /** Build the command to be executed.
   * @param dbName
   * @param path
   * @return
   */
  def cmd(dbName:DbName, path:String) : String = dbName.toCmdLine + " " + path

  def prepareScript(contents:String) : String = contents

  def run(dbName: DbName, scripts: List[Script]): Boolean = {

    val shellFile = "baseShell_tmp.js"

    def writeToFile(s:String) : File = {
      val fw = new FileWriter(shellFile)
      fw.write(s)
      fw.close()
      new File(shellFile)
    }

    val cmdResults : List[CmdResult] = scripts.map {
      sc =>
        import scala.sys.process._
        val logger = new ScriptLogger(sc)
        val prepped = prepareScript(sc.contents)
        val f : File = writeToFile(prepped)
        val command = cmd(dbName,f.getPath)
        info("running: [" + command + "]")
        val exitCode = command ! logger
        f.delete()
        println(logger.outLog)
        CmdResult(sc.name, logger.outLog, logger.errorLog, exitCode)
    }

    val errorResults = cmdResults.filterNot( _.exitCode == 0)

    errorResults match {
      case Nil => true
      case _ => {

        println(errorResults)
        val msg = errorResults.map( r => r.name + "\n" + r.err + "\n" + r.out).mkString("\n")
        println("shell exceptio: " + msg)
        throw new ShellException(msg)
      }
    }
  }

}

