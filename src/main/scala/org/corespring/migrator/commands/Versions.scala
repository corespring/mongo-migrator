package org.corespring.migrator.commands

import org.corespring.migrator.models.Version

/** List versions available for rollback
  *
  * @param uri
  */
class Versions(uri:String) extends BaseCommand(uri) {

  def begin{

    withDb{ db =>
      val list = Version.list
      val formatted = list.map(v => "id: " + v.id + ", versionId: " + v.versionId.getOrElse("?") + " " + v.dateCreated)
      formatted.foreach( s => println("Version: " + s ) )
    }
  }
}

object Versions{
  def apply(uri:String) = { new Versions(uri)}
}
