package org.corespring.commands

import org.specs2.mutable.{After, Specification}
import org.corespring.helpers.DbSingleton
import com.mongodb.casbah.commons.MongoDBObject
import org.corespring.models.{Version, Script, DbName}
import org.joda.time.DateTime
import org.corespring.shell.MigrateShell
import com.mongodb.DBObject

class RollbackTest extends Specification {

  "Rollback" should {

    sequential
    Version.init(DbSingleton.db)

    "rollback a single version" in new dbtidyup {

      val dbo: DBObject = MongoDBObject("firstName" -> "Ed")

      DbSingleton.db("test_rollback_collection").insert(dbo)

      val oneScripts = List(Script("firstName_toName.js",
        """
          |db.test_rollback_collection.find().forEach(function(o){
          | o.name = o.firstName;
          | delete o.firstName;
          | db.test_rollback_collection.save(o);
          |});
        """.stripMargin))

      val twoScripts = List(Script("name_to_givenName.js",
        """
          |db.test_rollback_collection.find().forEach(function(o){
          | o.givenName = o.name;
          | delete o.name;
          | db.test_rollback_collection.save(o);
          |});
          |//Down
          |db.test_rollback_collection.find().forEach(function(o){
          | o.name = o.givenName;
          | delete o.givenName;
          | db.test_rollback_collection.save(o);
          |});
        """.stripMargin))

      val scriptsToRun: List[Script] = List(oneScripts, twoScripts).flatten.map(_.up)
      MigrateShell.run(DbName(DbSingleton.mongoUri), scriptsToRun)

      val migratedDbo = DbSingleton.db("test_rollback_collection").findOne()
      migratedDbo.get.get("givenName") === "Ed"
      migratedDbo.get.get("name") === null

      val rollbackVersion = Version.create(new Version(new DateTime(), oneScripts, Some("versionOne")))
      Version.create(new Version(new DateTime(), twoScripts, Some("versionTwo")))

      val rollback = new Rollback("versionOne", DbSingleton.mongoUri, List())

      rollback.begin

      val rolledBackDbo = DbSingleton.db("test_rollback_collection").findOne()
      rolledBackDbo.get.get("name") === "Ed"
      rolledBackDbo.get.get("givenName") === null

      rollbackVersion === Version.currentVersion
    }
  }

  trait dbtidyup extends After {
    def after {
      Version.dropCollection
      DbSingleton.db("test_rollback_collection").dropCollection()
    }
  }

}

