package org.corespring.helpers

import org.specs2.mutable.After
import org.corespring.models.Version
import com.mongodb.casbah.{MongoURI, MongoDB, MongoConnection}

trait DbTest extends After {

  def after = Version.dropCollection
}

object DbSingleton {
  lazy val connection : MongoConnection = MongoConnection(MongoURI(mongoUri))
  lazy val db : MongoDB = connection(dbName)
  val dbName = "mongo_migrator_test_db"
  val mongoUri = "mongodb://localhost:27017/" + dbName
}
