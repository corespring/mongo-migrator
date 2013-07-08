package org.corespring.migrator.models

import com.mongodb.ServerAddress
import com.mongodb.casbah.{MongoURI, MongoConnection, MongoDB}

class DbHelper(val uri: String) {

  lazy val info: DbInfo = DbInfo(uri)

  def hostPort: String = info.hostPort

  def db: String = info.db

  def username: Option[String] = info.username

  def password: Option[String] = info.password

  lazy val connection: MongoConnection = {
    info.replicaSet.map {
      s =>
        val hosts: List[ServerAddress] = hostPort.split(",").toList.map(hp => {
          val pair = hp.split(":")
          if (pair.length == 0) {
            new ServerAddress(pair(0))
          } else {
            new ServerAddress(pair(0), pair(1).toInt)
          }
        })
        MongoConnection(hosts)
    }.getOrElse(MongoConnection(MongoURI(uri)))
  }

  lazy val mongoDB: MongoDB = {
    val db: MongoDB = connection(info.db)

    info.username.map {
      u =>
        if (!db.isAuthenticated) {
          require(info.password.isDefined)
          db.authenticate(u, info.password.get)
        }
    }

    db
  }

}

