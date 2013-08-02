package org.corespring.migrator.tests

import grizzled.slf4j.Logging

import org.corespring.migrator.helpers.DbSingleton
import com.mongodb.casbah.commons.MongoDBObject

class Cleanup extends Logging {
  info("do cleanup")
  DbSingleton.db.getCollection("mongo_migrator_versions").remove(MongoDBObject())
  DbSingleton.connection.close()
}
