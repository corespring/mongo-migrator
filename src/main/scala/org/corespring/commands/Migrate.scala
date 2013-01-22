package org.corespring.commands

import org.corespring.models.{DbName, Version, Migration, Script}
import org.corespring.shell.MigrateShell
import org.corespring.log.Logger
import com.mongodb.casbah.{MongoURI, MongoDB, MongoConnection}


class Migrate(
               uri: String,
               scriptFolders: List[String],
               versionId: Option[String] = None,
               validateContents: (List[Script], List[String]) => Boolean) extends BaseCommand(uri) {

  private val log = Logger.get("Migrate")

  def begin = {

    backup

    withDb {
      db =>

        val currentVersion = Version.currentVersionWithAllScripts
        val scripts = ScriptSlurper.scriptsFromPaths(scriptFolders)
        val migration = Migration(currentVersion, scripts)

        migration.scripts match {
          case List() => log.info("no scripts to run - up to date")
          case _ => {

            if (!validateContents(migration.scripts, scriptFolders)) {
              throw new RuntimeException("The scripts in the db and in the files don't match")
            }

            val dbName = DbName(uri)
            log.info("[Migrate] -> run shell")
            val successful = MigrateShell.run(dbName, migration.scripts.map(_.up))
            log.info("[Migrate] -> run shell complete")

            if (successful)
              Version.create(Version(versionId, migration.scripts))
            else
              throw new RuntimeException("Migration unsuccessful")

          }
        }
    }
  }

  def backup = log.info("TODO: backup: " + uri + " not a top priority as we can do this externally for now")
}

object Migrate {

  def alwaysValid(scripts: List[Script], scriptPaths: List[String]): Boolean = true

  def apply(
             uri: String,
             scripts: List[String],
             versionId: Option[String] = None,
             validateContents: (List[Script], List[String]) => Boolean = alwaysValid): Migrate = {
    new Migrate(uri, scripts, versionId, validateContents)
  }
}
