package org.corespring.migrator.commands

import org.corespring.migrator.models.{DbName, Version, Migration, Script}
import org.corespring.migrator.shell.MigrateShell
import com.mongodb.casbah.{MongoURI, MongoDB, MongoConnection}


class Migrate(
               uri: String,
               scriptFolders: List[String],
               versionId: String,
               validateContents: (List[Script], List[String]) => Boolean) extends BaseCommand(uri) {

  override def begin = {

    backup

    withDb {
      db =>

        val currentVersion = Version.currentVersionWithAllScripts
        val scripts = ScriptSlurper.scriptsFromPaths(scriptFolders)
        val migration = Migration(currentVersion, scripts)

        migration.scripts match {
          case List() => {
            info("no scripts to run - storing version")
            Version.create(Version(versionId, List()))
          }
          case _ => {

            if (!validateContents(migration.scripts, scriptFolders)) {
              throw new RuntimeException("The scripts in the db and in the files don't match")
            }

            val dbName = DbName(uri)
            info("[Migrate] -> run shell")
            val successful = MigrateShell.run(dbName, migration.scripts.map(_.up))
            info("[Migrate] -> run shell complete")

            if (successful)
              Version.create(Version(versionId, migration.scripts))
            else
              throw new RuntimeException("Migration unsuccessful")

          }
        }
    }
  }

  def backup = debug("TODO: backup: " + uri + " not a top priority as we can do this externally for now")
}

object Migrate {

  def alwaysValid(scripts: List[Script], scriptPaths: List[String]): Boolean = true

  def apply(
             uri: String,
             scripts: List[String],
             versionId: String,
             validateContents: (List[Script], List[String]) => Boolean = alwaysValid): Migrate = {
    new Migrate(uri, scripts, versionId, validateContents)
  }
}
