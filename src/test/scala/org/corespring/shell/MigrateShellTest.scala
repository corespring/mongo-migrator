package org.corespring.shell

import org.specs2.mutable.{After, Specification}
import org.corespring.models.{DBName, Version, Script, Migration}
import org.corespring.commands.ScriptSlurper
import com.mongodb.Mongo
import com.mongodb.casbah.{MongoCollection, MongoConnection}
import com.mongodb.casbah.commons.MongoDBObject

class MigrateShellTest extends Specification {

  val collection: MongoCollection = MongoConnection()("dbname")("mongo_migration_test")

  "MigrateShell" should {
    "run a single migration" in new dbtest {
      val paths = List("src/test/resources/mock_files/shell/one")
      val scripts: List[Script] = ScriptSlurper.scriptsFromPaths(paths)

      val migration = new Migration(scripts)
      val dbName : DBName = DBName("mongodb://localhost/dbname")
      MigrateShell.run(dbName,migration)
      collection.count(MongoDBObject()) === 1
      collection.findOne().get.get("name") === "Ed"
      collection.dropCollection()
    }

  }

}

trait dbtest extends After {
  lazy val collection: MongoCollection = MongoConnection()("dbname")("mongo_migration_test")

  def after = {
    collection.dropCollection()
    Version.dropCollection
  }
}
