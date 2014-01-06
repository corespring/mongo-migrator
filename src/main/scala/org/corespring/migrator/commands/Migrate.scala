package org.corespring.migrator.commands

import org.corespring.migrator.models._
import org.corespring.migrator.shell.MigrateShell
import com.mongodb.casbah.{MongoURI, MongoDB, MongoConnection}
import org.corespring.migrator.models.Script


class Migrate(
               uri: String,
               scriptFolders: List[String],
               versionId: String,
               validateContents: (Seq[Script], List[String]) => Boolean) extends BaseDBCommand(uri) {

  override def begin() {
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

            val dbInfo = DbInfo(uri)
            debug("[Migrate] -> run shell")
            val successful = MigrateShell.run(dbInfo, migration.scripts)
            debug("[Migrate] -> run shell complete")

            if (successful)
              Version.create(Version(versionId, migration.scripts))
            else
              throw new RuntimeException("Migration unsuccessful")

          }
        }

    }
  }
}

object Migrate {

  def alwaysValid(scripts: Seq[Script], scriptPaths: List[String]): Boolean = true

  def apply(
             uri: String,
             scripts: List[String],
             versionId: String,
             validateContents: (Seq[Script], List[String]) => Boolean = alwaysValid): Migrate = {
    new Migrate(uri, scripts, versionId, validateContents)
  }
}
