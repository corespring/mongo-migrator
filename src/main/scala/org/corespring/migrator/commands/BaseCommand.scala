package org.corespring.migrator.commands

import com.mongodb.casbah.{MongoURI, MongoConnection, MongoDB}
import org.corespring.migrator.models.{Version, DbName}
import grizzled.slf4j.Logging


abstract class BaseCommand(uri: String) extends Logging {

  def begin : Unit

  protected def withDb(fn: (MongoDB => Unit)) {
    val connection: MongoConnection = MongoConnection(MongoURI(uri))
    debug("connection: " + connection)
    val dbName: DbName = DbName(uri)
    val db: MongoDB = connection(dbName.db)
    dbName.username.map {
      u =>
        require(dbName.password.isDefined)
        db.authenticate(u, dbName.password.get)
    }
    debug("db: " + db)
    Version.init(db)
    fn(db)
  }

  def cleanup : Unit = {
    val connection: MongoConnection = MongoConnection(MongoURI(uri))
    debug("closing connection: " + connection)
    connection.close()
  }

}
