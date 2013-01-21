package org.corespring.commands

import org.corespring.models.{DBName, Version, Migration}
import org.corespring.shell.MigrateShell
import org.corespring.log.Logger
import com.mongodb.casbah.{MongoURI, MongoDB, MongoConnection}


class Migrate(uri: String, scriptFolders: List[String], versionId : Option[String] = None)  {

  private val log = Logger.get("Migrate")

  def begin = {

    backup

    withDb{ db =>

      val currentVersion = Version.currentVersionWithAllScripts
      val scripts = ScriptSlurper.scriptsFromPaths(scriptFolders)
      val migration = Migration(currentVersion, scripts)

      migration.scripts match {
        case List() => log.info("no scripts to run - up to date")
        case _ => {

          val dbName = DBName(uri)
          log.info("[Migrate] -> run shell")
          val successful = MigrateShell.run(dbName, migration)
          log.info("[Migrate] -> run shell complete")

          if (successful)
            Version.create(Version(versionId, migration.scripts))
          else
            throw new RuntimeException("Migration unsuccessful")

        }
      }
    }
  }


  private def withDb( fn : (MongoDB => Unit) ){
    val connection : MongoConnection = MongoConnection(MongoURI(uri))
    val dbName : DBName = DBName(uri)
    val db : MongoDB = connection(dbName.db)
    Version.init(db)
    fn(db)
    connection.close()
  }

  def backup = log.info("TODO: backup: " + uri + " not a top priority as we can do this externally for now")
}

object Migrate {
  def apply(uri: String, scripts: List[String], versionId : Option[String] = None): Migrate = {
    new Migrate(uri, scripts, versionId)
  }
}
