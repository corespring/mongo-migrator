package org.corespring.migrator.commands

import com.mongodb.casbah.MongoDB
import grizzled.slf4j.Logging
import org.corespring.migrator.models.{DbHelper, Version}

trait BaseCommand extends Logging {
  def begin() : Unit

  def cleanup() : Unit
}

abstract class BaseDBCommand(uri: String) extends BaseCommand {

  lazy val helper : DbHelper = new DbHelper(uri)

  protected def withDb(fn: (MongoDB => Unit)) {
    Version.init(helper.mongoDB)
    fn(helper.mongoDB)
  }

  override def cleanup : Unit = {
    debug("closing connection: " + helper.connection)
    helper.connection.close()
  }

}
