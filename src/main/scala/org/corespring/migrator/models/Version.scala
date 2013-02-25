package org.corespring.migrator.models

import org.joda.time.DateTime
import com.mongodb.casbah.commons.conversions.scala.RegisterJodaTimeConversionHelpers
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import mongoContext._


case class Version(dateCreated: DateTime,
                   scripts: List[Script],
                   versionId: String,
                   id: ObjectId = new ObjectId())


object Version {

  def apply(versionId: String, scripts: List[Script]): Version = new Version(new DateTime(), scripts, versionId)

  private lazy val Dao = new Dao(db)

  private var db: MongoDB = null

  /** Initialise Version to use a established db
    *
    * @param db
    */
  def init(db: MongoDB) = {
    this.db = db
  }

  class Dao(db: MongoDB) extends ModelCompanion[Version, ObjectId] {
    RegisterJodaTimeConversionHelpers()
    val collection = db("mongo_migrator_versions")

    collection.ensureIndex(
      MongoDBObject("versionId" -> 1),
      MongoDBObject("unique" -> true)
    )

    val dao = new SalatDAO[Version, ObjectId](collection = collection) {}
  }

  def dropCollection() {
    Dao.collection.dropCollection()
  }

  def currentVersion: Version = {

    val cursor: SalatMongoCursor[Version] =
      Dao.find(MongoDBObject()).sort(MongoDBObject("_id" -> -1)).limit(1)

    cursor.toList match {
      case List() => {
        val defaultCurrent = new Version(new DateTime(), List(), "version_0")
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

  def create(v: Version): Version = {

    def alreadyExists = {
      val idCount = Dao.count(MongoDBObject("versionId" -> v.versionId))
      idCount > 0
    }

    if (alreadyExists){
      throw new RuntimeException("Can't create versionId: " + v.versionId + " is taken")
    }

    val newCurrent = v.copy(dateCreated = new DateTime())
    Dao.save(newCurrent)
    newCurrent
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
