package org.corespring.commands

import org.specs2.mutable.{Specification, After}
import com.mongodb.casbah.{MongoConnection, MongoCollection}
import com.mongodb.casbah.commons.MongoDBObject
import org.corespring.models.Version

class MigrateTest extends Specification {

  sequential

  val Db = "dbname"
  val mongoUri = "mongodb://localhost/" + Db
  val dummyCollection = "migrateTest"

  object TestCollections {
    val collection: MongoCollection = MongoConnection()(Db)(dummyCollection)
    val orgs: MongoCollection = MongoConnection()(Db)("organizations")
    val users: MongoCollection = MongoConnection()(Db)("users")
  }


  def path(n: String) = "src/test/resources/mock_files/" + n

  "Migrate" should {
    "do one migration" in new dbtest {

      import TestCollections._

      val cmd = new Migrate(mongoUri, List(path("/migrate/one")))
      cmd.begin
      collection.count(MongoDBObject()) === 1
    }


    "run two migrations" in new dbtest {

      import TestCollections._

      /** Note: for testing we are using multiple folders
        * To simulate the adding of new scripts. In real life
        * this will probably be files added to a single folder
        */
      val paths = List(path("migrate/two/first"), path("migrate/two/second"))
      val firstMigrate = new Migrate(mongoUri, List(paths.head))
      firstMigrate.begin
      collection.count(MongoDBObject()) === 1

      collection.findOne() match {
        case Some(dbo) => dbo.get("name") === "Ed"
        case _ => failure("didn't find item")
      }

      val secondMigrate = new Migrate(mongoUri, paths)
      secondMigrate.begin
      collection.count(MongoDBObject()) === 1
      collection.findOne() match {
        case Some(dbo) => dbo.get("firstName") === "Ed"
        case _ => failure("didn't find item")
      }
    }


    "run three migrations" in new dbtest {

      import TestCollections._

      val paths = List(path("migrate/three/a"), path("migrate/three/b"), path("migrate/three/c"))
      val firstMigrate = new Migrate(mongoUri, paths.take(1))
      firstMigrate.begin
      orgs.count(MongoDBObject()) === 3
      users.count(MongoDBObject()) === 2
      orgs.find( MongoDBObject("Location" -> "NYC")).length === 2

      val secondMigrate = new Migrate(mongoUri, paths.take(2))
      secondMigrate.begin
      orgs.count(MongoDBObject()) === 3
      users.count(MongoDBObject()) === 2
      println(orgs.find(MongoDBObject()).toList)
      val nycOrgs = orgs.find( MongoDBObject("city" -> "NYC"))
      println(nycOrgs)
      nycOrgs.length === 2

      val thirdMigrate = new Migrate(mongoUri, paths)
      thirdMigrate.begin
      orgs.count(MongoDBObject()) === 3
      users.count(MongoDBObject()) === 2

      orgs.find( MongoDBObject("city" -> "New York")).length === 2
    }
  }

  trait dbtest extends After {

    def after = {
      import TestCollections._

      Version.dropCollection
      collection.dropCollection()
      users.dropCollection()
      orgs.dropCollection()
    }
  }

}

