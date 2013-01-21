package org.corespring.commands

import org.corespring.models.{DBName, Version, Migration}
import org.corespring.shell.MigrateShell
import java.io.File
import org.corespring.log.Logger


class Migrate(uri: String, scriptFolders: List[String]) {

  private val log = Logger.get("Migrate")

  def begin = {
    backup
    val currentVersion = Version.currentVersionWithAllScripts
    val scripts = ScriptSlurper.scriptsFromPaths(scriptFolders)
    val migration = Migration(currentVersion, scripts)
    val dbName = DBName(uri)
    log.info("[Migrate] -> run shell")
    val successful = MigrateShell.run(dbName, migration)
    log.info("[Migrate] -> run shell complete")

    if (successful)
      Version.create(Version("hello from Migrate.begin", migration.scripts))
    else
      throw new RuntimeException("Migration unsuccessful")
  }

  def backup = println("backup: " + uri)
}

object Migrate {
  def apply(uri: String, scripts: List[String]): Migrate = {
    new Migrate(uri, scripts)
  }
}
