package org.corespring.shell

import org.corespring.models.{DbName, Script}
import java.io.{File,FileWriter}

trait BaseShell {

  def run(dbName: DbName, scripts: List[Script]): Boolean = {

    val shellFile = "baseShell_tmp.js"

    def writeToFile(s:String) : File = {
      val fw = new FileWriter(shellFile)
      fw.write(s)
      fw.close()
      new File(shellFile)
    }

    val loggers: List[ScriptLogger] = scripts.map {
      sc =>
        import scala.sys.process._
        val logger = new ScriptLogger(sc)
        val f : File = writeToFile(sc.contents)
        val cmd = (dbName.toCmdLine + " " + f.getPath)
        println("running: [" + cmd + "]")
        cmd ! logger
        f.delete()
        logger
    }

    loggers.foreach(_.printToLogger())

    val hasErrors = loggers.foldRight(false)(_.hasError || _)
    !hasErrors
  }

}
