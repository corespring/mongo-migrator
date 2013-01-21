package org.corespring.tests

import org.corespring.helpers.DbSingleton
import org.corespring.log.Logger

class Cleanup {
  Logger.get("Cleanup").info("do cleanup")
  DbSingleton.db.dropDatabase()
  DbSingleton.connection.close()
}
