package org.corespring.shell

import org.specs2.mutable.{After, Specification}
import org.corespring.models.{DbName, Version, Script, Migration}
import org.corespring.commands.ScriptSlurper
import com.mongodb.Mongo
import com.mongodb.casbah.{MongoCollection, MongoConnection}
import com.mongodb.casbah.commons.MongoDBObject
import org.corespring.helpers.DbSingleton

class MigrateShellTest extends Specification {

  Version.init(DbSingleton.db)

  val collection: MongoCollection = DbSingleton.db("mongo_migration_test")

  "MigrateShell" should {
    "run a single migration" in new dbtest {
      val paths = List("src/test/resources/mock_files/shell/one")
      val scripts: List[Script] = ScriptSlurper.scriptsFromPaths(paths)

      val migration = new Migration(scripts)
      val dbName : DbName = DbName(DbSingleton.mongoUri)
      MigrateShell.run(dbName,migration.scripts.map(_.up))
      collection.count(MongoDBObject()) === 1
      collection.findOne().get.get("name") === "Ed"
      collection.dropCollection()
    }
  }
}

trait dbtest extends After {

  def after = {
    Version.dropCollection
  }
}
