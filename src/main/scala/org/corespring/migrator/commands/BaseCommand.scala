package org.corespring.migrator.commands

import com.mongodb.casbah.MongoDB
import grizzled.slf4j.Logging
import org.corespring.migrator.models.{DbHelper, Version}


abstract class BaseCommand(uri: String) extends Logging {

  lazy val helper : DbHelper = new DbHelper(uri)

  def begin()

  protected def withDb(fn: (MongoDB => Unit)) {
    Version.init(helper.mongoDB)
    fn(helper.mongoDB)
  }

  def cleanup : Unit = {
    debug("closing connection: " + helper.connection)
    helper.connection.close()
  }

}
