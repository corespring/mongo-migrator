package org.corespring.migrator.commands

import org.corespring.migrator.models.Version

/** List versions available for rollback
  *
  * @param uri
  */
class Versions(uri: String) extends BaseDBCommand(uri) {

  override def begin {

    withDb {
      db =>
        info(s"Versions in: $uri")
        val list = Version.list
        val formatted = list.map(v => "id: " + v.id + ", versionId: " + v.versionId + " " + v.dateCreated)
        formatted.foreach(s => info("Version: " + s))
    }
  }
}

object Versions {
  def apply(uri: String) = {
    new Versions(uri)
  }
}
