package org.corespring.migrator.helpers

import com.mongodb.casbah.{MongoURI, MongoDB, MongoConnection}
import org.corespring.migrator.models.DbInfo

object DbSingleton {
  lazy val connection : MongoConnection = MongoConnection(MongoURI(mongoUri))
  lazy val db : MongoDB = init

  val dbName = "mongo_migrator_test_db"
  val envUri = System.getenv("MONGO_MIGRATOR_TEST_DB_URI")
  val mongoUri = if(envUri == null) "mongodb://localhost:27017/" + dbName else envUri

  private def init : MongoDB = {
    val db = connection(dbName)
    val name = DbInfo(mongoUri)
    println("db -> " + name + " uri: " + mongoUri)
    name.username.map { u =>
      db.authenticate(u, name.password.get)
    }
    db
  }

}
