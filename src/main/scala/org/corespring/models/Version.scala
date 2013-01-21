package org.corespring.models

import org.joda.time.DateTime
import com.mongodb.casbah.commons.conversions.scala.RegisterJodaTimeConversionHelpers
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.MongoConnection
import com.mongodb.casbah.query.dsl._
import org.corespring.models.mongoContext._

/*
import org.bson.types.ObjectId
import com.novus.salat.dao.{SalatDAO, ModelCompanion}
import com.mongodb.casbah.commons.conversions.scala.RegisterJodaTimeConversionHelpers
import com.mongodb.casbah.Imports._
import com.novus.salat._
import com.novus.salat.global._
import com.novus.salat.annotations._
*/

case class Version(commitHash: String,
                   dateCreated: DateTime,
                   scripts: List[Script],
                   id: ObjectId = new ObjectId())


object Version {

  private object Dao extends ModelCompanion[Version, ObjectId] {
    RegisterJodaTimeConversionHelpers()
    val collection = MongoConnection()("dbname")("mongo_migrator_versions")
    val dao = new SalatDAO[Version, ObjectId](collection = collection) {}
  }

  def apply(commitHash: String, scripts: List[Script]): Version = new Version(commitHash, new DateTime(), scripts)

  def dropCollection {
    Dao.collection.dropCollection()
  }

  def currentVersion: Version = {

    val cursor: SalatMongoCursor[Version] =
      Dao.find(MongoDBObject()).sort(MongoDBObject("_id" -> -1)).limit(1)

    cursor.toList match {
      case List() => {
        val defaultCurrent = new Version("defaultCurrent", new DateTime(), List())
        Dao.save(defaultCurrent)
        defaultCurrent
      }
      case List(v) => v
    }
  }

  def allScripts(v: Version): List[Script] = {
    val cursor = Dao.find("_id" $lte v.id)
    val scripts = cursor.toList.map( _.scripts ).flatten
    scripts
  }

  def currentVersionWithAllScripts : Version = {
    val latest = currentVersion
    latest.copy(scripts = allScripts(latest))
  }

  def create(v: Version): Unit = {
    val newCurrent = v.copy(dateCreated = new DateTime())
    Dao.save(newCurrent)
  }

  def count(dbo: DBObject): Long = Dao.count(dbo)
}
