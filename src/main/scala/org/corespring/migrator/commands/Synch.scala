package org.corespring.migrator.commands

import org.corespring.migrator.models.{Script, Version}
import java.io.{FileWriter, File}

class Synch(target: String,
            versionId: String,
            uri: String,
            scriptPaths: Seq[String]) extends BaseCommand(uri) {

  def begin() {
    target match {
      case "db" => withVersion(versionId)(synchDb)
      case "files" => withVersion(versionId)(synchFiles)
      case _ => println("Unknown target")
    }
  }

  private def withVersion(versionId: String)(fn: Version => Unit) {
    Version.findByVersionId(versionId) match {
      case Some(v) => fn(v)
      case _ => println("can't find version")
    }
  }

  def synchDb(v: Version) {
    val scripts = ScriptSlurper.scriptsFromPaths(scriptPaths)
    Version.update(v.copy(scripts = scripts))
  }

  def synchFiles(v: Version) {

    def writeToFile(s: String, path: String): File = {
      val fw = new FileWriter(path)
      fw.write(s)
      fw.close()
      new File(path)
    }

    v.scripts.foreach {
      sc: Script =>
        writeToFile(sc.contents, sc.name)
    }
  }

}

object Synch {

  def apply( target: String,
             versionId: String,
             uri: String,
             scripts: Seq[String]): Synch = {
    new Synch(target, versionId, uri, scripts)
  }
}
