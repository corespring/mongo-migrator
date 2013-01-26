package org.corespring.migrator.tests

import org.corespring.migrator.helpers.DbSingleton
import org.corespring.migrator.log.Logger

class Cleanup {
  Logger.get("Cleanup").info("do cleanup")
  DbSingleton.db.dropDatabase()
  DbSingleton.connection.close()
}
