package org.corespring.tests

import org.corespring.helpers.DbSingleton

class Cleanup {
  DbSingleton.db.dropDatabase()
  DbSingleton.connection.close()
}
