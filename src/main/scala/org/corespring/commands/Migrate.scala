package org.corespring.commands

import org.corespring.models.{DBName, Version, Migration}
import org.corespring.shell.MigrateShell
import java.io.File

class Migrate(uri: String, scriptFolders: List[String]) {


  def begin = {
    backup
    val currentVersion = Version.currentVersionWithAllScripts
    val scripts = ScriptSlurper.scriptsFromPaths(scriptFolders)
    val migration = Migration(currentVersion, scripts)
    val dbName = DBName(uri)
    println("[Migrate] -> run shell")
    val successful = MigrateShell.run(dbName, migration)
    println("[Migrate] -> run shell complete")

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
