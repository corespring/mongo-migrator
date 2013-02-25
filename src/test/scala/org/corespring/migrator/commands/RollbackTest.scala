package org.corespring.migrator.commands

import org.specs2.mutable.{After, Specification}
import org.corespring.migrator.helpers.DbSingleton
import com.mongodb.casbah.commons.MongoDBObject
import org.corespring.migrator.models.{Version, Script, DbName}
import org.joda.time.DateTime
import org.corespring.migrator.shell.MigrateShell
import com.mongodb.DBObject

class RollbackTest extends Specification {

  "Rollback" should {

    sequential
    Version.init(DbSingleton.db)

    val testCollection: String = "test_rollback_collection"

    val oneScripts = List(Script("firstName_toName.js",
      """
        |function up(){
        |  db.test_rollback_collection.find().forEach(function(o){
        |   o.name = o.firstName;
        |   delete o.firstName;
        |   db.test_rollback_collection.save(o);
        |  });
        |}
        |
        |function down(){
        |  db.test_rollback_collection.find().forEach(function(o){
        |   o.firstName = o.name;
        |   delete o.name;
        |   db.test_rollback_collection.save(o);
        |  });
        |}
      """.stripMargin))

    val twoScripts = List(Script("name_to_givenName.js",
      """
        |function up(){
        |  db.test_rollback_collection.find().forEach(function(o){
        |   o.givenName = o.name;
        |   delete o.name;
        |   db.test_rollback_collection.save(o);
        |  });
        |}
        |
        |function down(){
        |  db.test_rollback_collection.find().forEach(function(o){
        |   o.name = o.givenName;
        |   delete o.givenName;
        |   db.test_rollback_collection.save(o);
        |  });
        |}
      """.stripMargin))

    val threeScripts = List(Script("givenName_to_nickname.js",
      """
        |function up() {
        |  db.test_rollback_collection.find().forEach(function(o){
        |   o.nickname = o.givenName;
        |   delete o.givenName;
        |   db.test_rollback_collection.save(o);
        |  });
        |}
        |function down(){
        |  db.test_rollback_collection.find().forEach(function(o){
        |   o.givenName = o.nickname;
        |   delete o.nickname;
        |   db.test_rollback_collection.save(o);
        |  });
        |}
      """.stripMargin))

    def seedDb = {
      val dbo: DBObject = MongoDBObject("firstName" -> "Ed")
      DbSingleton.db(testCollection).insert(dbo)
    }

    def createVersion(scripts: Seq[Script], versionId: String) = Version.create(new Version(new DateTime(), scripts, versionId))

    "rollback a single version" in new dbtidyup {

      seedDb

      val scriptsToRun: Seq[Script] = List(oneScripts, twoScripts).flatten
      MigrateShell.run(DbName(DbSingleton.mongoUri), scriptsToRun)

      val migratedDbo = DbSingleton.db(testCollection).findOne()
      migratedDbo.get.get("givenName") === "Ed"
      migratedDbo.get.get("name") === null

      val rollbackVersion = createVersion(oneScripts, "versionOne")
      createVersion(twoScripts, "versionTwo")

      val rollback = Rollback("versionOne", DbSingleton.mongoUri, List())

      rollback.begin

      val rolledBackDbo = DbSingleton.db(testCollection).findOne()
      rolledBackDbo.get.get("name") === "Ed"
      rolledBackDbo.get.get("givenName") === null

      rollbackVersion === Version.currentVersion
    }


    "rollback 2 versions" in new dbtidyup {
      seedDb

      val scriptsToRun = List(oneScripts, twoScripts, threeScripts).flatten
      MigrateShell.run(DbName(DbSingleton.mongoUri), scriptsToRun)

      val migratedDbo = DbSingleton.db(testCollection).findOne()
      migratedDbo.get.get("nickname") === "Ed"

      val zeroVersion = createVersion(List(), "versionZero")
      val rollbackVersion = createVersion(oneScripts, "versionOne")
      createVersion(twoScripts, "versionTwo")
      createVersion(threeScripts, "versionThree")

      Rollback("versionOne", DbSingleton.mongoUri, List()).begin

      rollbackVersion == Version.currentVersion

      val rolledBackDbo = DbSingleton.db(testCollection).findOne()
      rolledBackDbo.get.get("name") === "Ed"

      Rollback("versionZero", DbSingleton.mongoUri, List()).begin

      val versionZeroDbo = DbSingleton.db(testCollection).findOne()
      versionZeroDbo.get.get("firstName") === "Ed"
    }

    trait dbtidyup extends After {
      def after {
        Version.dropCollection
        DbSingleton.db(testCollection).dropCollection()
      }
    }
  }


}

