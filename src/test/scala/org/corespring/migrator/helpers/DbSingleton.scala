package org.corespring.migrator.helpers

import com.mongodb.casbah.{MongoURI, MongoDB, MongoConnection}

object DbSingleton {
  lazy val connection : MongoConnection = MongoConnection(MongoURI(mongoUri))
  lazy val db : MongoDB = connection(dbName)
  val dbName = "mongo_migrator_test_db"
  val mongoUri = "mongodb://localhost:27017/" + dbName

}
