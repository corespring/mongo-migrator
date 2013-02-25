package org.corespring.migrator.commands

import org.specs2.mutable.{BeforeAfter, After, Specification}
import java.io.{FileWriter, File}
import org.corespring.migrator.models.{Script, Version}
import org.corespring.migrator.helpers.DbSingleton
import scala.sys.process._

class SynchTest extends Specification {


  Version.init(DbSingleton.db)


  sequential

  class synch extends BeforeAfter {

    def root = "src/test/resources/mock_files/synch"
    def rootFolder = new File(root)

    def before {
      rootFolder.mkdir
    }

    def after {
      rootFolder.delete
      Version.dropCollection()
    }
  }

  def writeToFile(s: String, path: String): File = {
    val fw = new FileWriter(path)
    fw.write(s)
    fw.close()
    new File(path)
  }


  "Synch" should {
    "synch db" in new synch {
      val file = dumpFile(root + "/one.js", "1")

      val scripts = Seq(
        Script(root + "/one.js", "1")
      )
      Version.create(Version(versionId = "1", scripts = scripts))

      writeToFile("2", file.getCanonicalPath)
      Synch("db", "1", DbSingleton.mongoUri, Seq(root)).begin()

      Version.findByVersionId("1") match {
        case Some(v) => {
          v.scripts(0).contents === "2"
        }
        case _ => failure("can't find version with id 1")
      }
    }

    def dumpFile(name: String, contents: String): File = {
      val file = new File(name)
      writeToFile(contents, file.getCanonicalPath)
      file
    }

    "when synching db - only synch items that already exist" in new synch {

      dumpFile(root + "/one.js", "1")
      dumpFile(root + "/two.js", "2")

      val scripts = Seq(
        Script(root + "/one.js", "1")
      )

      Version.create(
        Version(versionId = "1", scripts = scripts)
      )

      Synch("db", "1", DbSingleton.mongoUri, Seq(root)).begin()

      Version.findByVersionId("1") match {
        case Some(v) => {
          v.scripts.length === 1
        }
        case _ => failure("can't find version with id 1")
      }


    }

    "synch files" in new synch {
      dumpFile(root + "/one.js", "1")

      val scripts = Seq(
        Script(root + "/one.js", "2")
      )
      Version.create(Version(versionId = "1", scripts = scripts))

      Synch("files", "1", DbSingleton.mongoUri, Seq(root)).begin()

      val synchedScripts = ScriptSlurper.scriptsFromPaths(Seq(root))
      synchedScripts(0).contents === "2"
    }
  }
}

