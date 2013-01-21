package org.corespring.models

import org.joda.time.DateTime
import com.mongodb.casbah.commons.conversions.scala.RegisterJodaTimeConversionHelpers
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.MongoConnection
import com.mongodb.casbah.query.dsl._
import org.corespring.models.mongoContext._


case class Version(dateCreated: DateTime,
                   scripts: List[Script],
                   versionId: Option[String] = None,
                   id: ObjectId = new ObjectId())


object Version {

  private lazy val Dao = new Dao(db)

  private var db: MongoDB = null

  def init(db: MongoDB) = {
    this.db = db
  }

  class Dao(db: MongoDB) extends ModelCompanion[Version, ObjectId] {
    RegisterJodaTimeConversionHelpers()
    val collection = db("mongo_migrator_versions")
    val dao = new SalatDAO[Version, ObjectId](collection = collection) {}
  }

  def apply(versionId: Option[String], scripts: List[Script]): Version = new Version(new DateTime(), scripts, versionId)

  def dropCollection {
    Dao.collection.dropCollection()
  }

  def currentVersion: Version = {

    val cursor: SalatMongoCursor[Version] =
      Dao.find(MongoDBObject()).sort(MongoDBObject("_id" -> -1)).limit(1)

    cursor.toList match {
      case List() => {
        val defaultCurrent = new Version(new DateTime(), List())
        Dao.save(defaultCurrent)
        defaultCurrent
      }
      case List(v) => v
    }
  }

  def allScripts(v: Version): List[Script] = {
    val cursor = Dao.find("_id" $lte v.id)
    val scripts = cursor.toList.map(_.scripts).flatten
    scripts
  }

  def currentVersionWithAllScripts: Version = {
    val latest = currentVersion
    latest.copy(scripts = allScripts(latest))
  }

  def create(v: Version): Unit = {
    val newCurrent = v.copy(dateCreated = new DateTime())
    Dao.save(newCurrent)
  }

  def count(dbo: DBObject): Long = Dao.count(dbo)

  def list(): List[Version] = Dao.findAll().toList

  def findById(id: String): Option[Version] = {
    try {
      val objectId = new ObjectId(id)
      Dao.findOneById(objectId)
    }
    catch {
      case e: Throwable => None
    }
  }

  def findByVersionId(versionId: String): Option[Version] = Dao.findOne(MongoDBObject("versionId" -> versionId))

  def findVersionsLaterThan(v: Version): List[Version] = Dao.find("_id" $gt v.id).toList

  def remove(v: Version) {
    Dao.remove(v)
  }
}
