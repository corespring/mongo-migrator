package org.corespring.migrator.tests

import org.corespring.migrator.helpers.DbSingleton
import org.corespring.migrator.log.Logger
import com.mongodb.casbah.commons.MongoDBObject

class Cleanup {
  Logger.get("Cleanup").info("do cleanup")
  DbSingleton.db.getCollection("mongo_migrator_versions").remove(MongoDBObject())
  DbSingleton.connection.close()
}
