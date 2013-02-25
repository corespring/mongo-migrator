package org.corespring.migrator.shell

import exceptions.ShellException
import org.specs2.mutable.{After, Specification}
import org.corespring.migrator.models.{DbName, Version, Script, Migration}
import org.corespring.migrator.commands.ScriptSlurper
import com.mongodb.Mongo
import com.mongodb.casbah.{MongoCollection, MongoConnection}
import com.mongodb.casbah.commons.MongoDBObject
import org.corespring.migrator.helpers.DbSingleton

class MigrateShellTest extends Specification {

  Version.init(DbSingleton.db)

  sequential

  val collection: MongoCollection = DbSingleton.db("mongo_migration_test")

  "MigrateShell" should {
    "run a single migration" in new dbtest {
      val paths = List("src/test/resources/mock_files/shell/one")
      val scripts: List[Script] = ScriptSlurper.scriptsFromPaths(paths)

      val migration = new Migration(scripts)
      val dbName : DbName = DbName(DbSingleton.mongoUri)
      MigrateShell.run(dbName,migration.scripts)
      collection.count(MongoDBObject()) === 1
      collection.findOne().get.get("name") === "Ed"
      collection.dropCollection()
    }

    "if no up is specified an error is thrown" in new dbtest {
      val paths = List("src/test/resources/mock_files/shell/two")
      val scripts: List[Script] = ScriptSlurper.scriptsFromPaths(paths)
      val migration = new Migration(scripts)
      val dbName : DbName = DbName(DbSingleton.mongoUri)
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
