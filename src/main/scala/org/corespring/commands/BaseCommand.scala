package org.corespring.commands

import com.mongodb.casbah.{MongoURI, MongoConnection, MongoDB}
import org.corespring.models.{Version,DbName}

class BaseCommand(uri:String) {

  protected def withDb( fn : (MongoDB => Unit) ){
    val connection : MongoConnection = MongoConnection(MongoURI(uri))
    val dbName : DbName = DbName(uri)
    val db : MongoDB = connection(dbName.db)
    Version.init(db)
    fn(db)
    connection.close()
  }

}
