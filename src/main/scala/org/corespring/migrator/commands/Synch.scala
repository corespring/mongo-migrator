package org.corespring.migrator.commands

import org.corespring.migrator.models.{Script, Version}
import java.io.{FileWriter, File}

class Synch(target: String,
            versionId: String,
            uri: String,
            scriptPaths: Seq[String]) extends BaseCommand(uri) {

  def begin() {
    withDb {
      db =>
        target match {
          case "db" => withVersion(versionId)(synchDb)
          case "files" => withVersion(versionId)(synchFiles)
          case _ => error("Unknown target")
        }
    }
  }

  private def withVersion(versionId: String)(fn: Version => Unit) {
    Version.findByVersionId(versionId) match {
      case Some(v) => fn(v)
      case _ => error("can't find version")
    }
  }

  def synchDb(v: Version) {
    val scripts = ScriptSlurper.scriptsFromPaths(scriptPaths)
    def areInDb(dbScripts:Seq[Script])(s:Script) : Boolean = {
      dbScripts.exists{ dbs => dbs.name == s.name}
    }
    val trimmed = scripts.filter(areInDb(v.scripts))
    Version.update(v.copy(scripts = trimmed))
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

  def apply(target: String,
            versionId: String,
            uri: String,
            scripts: Seq[String]): Synch = {
    new Synch(target, versionId, uri, scripts)
  }
}
