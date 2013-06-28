package org.corespring.migrator.shell

import com.mongodb.casbah.MongoCollection
import com.mongodb.casbah.commons.MongoDBObject
import exceptions.ShellException
import org.corespring.migrator.commands.ScriptSlurper
import org.corespring.migrator.helpers.DbSingleton
import org.corespring.migrator.models._
import org.specs2.mutable.{After, Specification}

class MigrateShellTest extends Specification {

  Version.init(DbSingleton.db)

  sequential

  val collection: MongoCollection = DbSingleton.db("mongo_migration_test")

  "MigrateShell" should {
    "run a single migration" in new dbtest {
      val paths = List("src/test/resources/mock_files/shell/one")
      val scripts: Seq[Script] = ScriptSlurper.scriptsFromPaths(paths)

      val migration = new Migration(scripts)
      val dbName : DbInfo = DbInfo(DbSingleton.mongoUri)
      MigrateShell.run(dbName,migration.scripts)
      collection.count(MongoDBObject()) === 1
      collection.findOne().get.get("name") === "Ed"
      collection.dropCollection()
    }

    "if no up is specified an error is thrown" in new dbtest {
      val paths = List("src/test/resources/mock_files/shell/two")
      val scripts: Seq[Script] = ScriptSlurper.scriptsFromPaths(paths)
      val migration = new Migration(scripts)
      val dbName : DbInfo = DbInfo(DbSingleton.mongoUri)
      MigrateShell.run(dbName,migration.scripts) must throwA[ShellException]
    }
  }
}

trait dbtest extends After {

  def after = {
    Version.dropCollection
    DbSingleton.db("mongo_migration_test").dropCollection()
  }
}
