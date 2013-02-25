package org.corespring.migrator.commands

import org.specs2.mutable.{After, Specification}
import java.io.{FileWriter, File}
import org.corespring.migrator.models.{Script, Version}
import org.corespring.migrator.helpers.DbSingleton
import scala.sys.process._

class SynchTest extends Specification {


  Version.init(DbSingleton.db)

  val root = "src/test/resources/mock_files/synch"

  sequential

  class tidySynch extends After {
    def after = {
      val cmd = "rm -fr " + root
      cmd.!!
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
    "synch db" in new tidySynch {

      val rootFolder = new File(root)
      rootFolder.mkdir()

      val file = new File(root + "/one.js")

      writeToFile("1", file.getCanonicalPath)

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

    "synch files" in new tidySynch{

      val rootFolder = new File(root)
      rootFolder.mkdir()

      val file = new File(root + "/one.js")

      writeToFile("1", file.getCanonicalPath)

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

