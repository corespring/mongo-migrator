package org.corespring.migrator.commands

import org.specs2.mutable.{Specification, After}
import com.mongodb.casbah.{MongoConnection, MongoCollection}
import com.mongodb.casbah.commons.MongoDBObject
import org.corespring.migrator.models.Version
import org.corespring.migrator.helpers.DbSingleton

class MigrateTest extends Specification {

  sequential

  Version.init(DbSingleton.db)

  val dummyCollection = "migrateTest"

  object TestCollections {
    val collection: MongoCollection = DbSingleton.db(dummyCollection)
    val orgs: MongoCollection = DbSingleton.db("organizations")
    val users: MongoCollection = DbSingleton.db("users")
  }


  def path(n: String) = "src/test/resources/mock_files/" + n

  "Migrate" should {

    import DbSingleton.mongoUri

    "do one migration" in new dbtest {

      Version.dropCollection

      import TestCollections._
      collection.remove(MongoDBObject())

      val cmd = Migrate(mongoUri, List(path("migrate/one")), "v1")
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
      val firstMigrate = Migrate(mongoUri, List(paths.head), "v1")
      firstMigrate.begin
      collection.count(MongoDBObject()) === 1

      collection.findOne() match {
        case Some(dbo) => dbo.get("name") === "Ed"
        case _ => failure("didn't find item")
      }

      val secondMigrate = Migrate(mongoUri, paths, "v2")
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
      val firstMigrate = Migrate(mongoUri, paths.take(1), "v1")
      firstMigrate.begin
      orgs.count(MongoDBObject()) === 3
      users.count(MongoDBObject()) === 2
      orgs.find(MongoDBObject("Location" -> "NYC")).length === 2

      val secondMigrate = Migrate(mongoUri, paths.take(2), "v2")
      secondMigrate.begin
      orgs.count(MongoDBObject()) === 3
      users.count(MongoDBObject()) === 2
      println(orgs.find(MongoDBObject()).toList)
      val nycOrgs = orgs.find(MongoDBObject("city" -> "NYC"))
      println(nycOrgs)
      nycOrgs.length === 2

      val thirdMigrate = Migrate(mongoUri, paths, "v3")
      thirdMigrate.begin
      orgs.count(MongoDBObject()) === 3
      users.count(MongoDBObject()) === 2

      orgs.find(MongoDBObject("city" -> "New York")).length === 2
    }

    "if there are no new scripts - still add a Version to the db with an empty script List" in {

      val firstCmd = Migrate(mongoUri, List(path("/migrate/one")), "v1")
      firstCmd.begin

      val secondCmd = Migrate(mongoUri, List(path("/migrate/one")), "v2")
      secondCmd.begin

      val secondRunVersion = Version.currentVersion

      secondRunVersion.scripts === List()
      secondRunVersion.versionId === "v2"
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

