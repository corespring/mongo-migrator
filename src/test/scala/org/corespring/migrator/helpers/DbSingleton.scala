package org.corespring.migrator.helpers

import com.mongodb.casbah.{MongoDB, MongoConnection}
import org.corespring.migrator.models.DbHelper

object DbSingleton {
  val dbName = "mongo_migrator_test_db"
  lazy val envUri = System.getenv("MONGO_MIGRATOR_TEST_DB_URI")

  lazy val mongoUri = {
    val out = if(envUri == null) "mongodb://localhost:27017/" + dbName else envUri
    println("[DbSingleton] >>> using uri: " + out)
    out
  }

  lazy val helper : DbHelper = new DbHelper(mongoUri)
  lazy val connection : MongoConnection = helper.connection
  lazy val db : MongoDB = helper.mongoDB
}
