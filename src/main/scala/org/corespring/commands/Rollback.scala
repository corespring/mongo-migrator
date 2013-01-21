package org.corespring.commands

import org.corespring.models.{Script,Version, DbName}
import org.corespring.shell.RollbackShell

class Rollback(targetId:String,uri:String,scripts:List[String]) extends BaseCommand(uri){



  private def pf[T]: PartialFunction[Option[T], Option[T]] = {
    case Some(thing) => Some(thing)
    case None => None
  }

  def begin = {
    println("Rollback.begin")

    withDb { db =>

      val targetVersion : Option[Version] =
        pf(Version.findById(targetId))
          .orElse(Version.findByVersionId(targetId))

      targetVersion match {
        case Some(target) => {

          if (target  == Version.currentVersion ){
            println("already at current version - can't rollback")
          } else {

            val laterVersions : List[Version] = Version.findVersionsLaterThan(target)

            def rollback:List[Script] = { List() }

            val success = RollbackShell.run(DbName(uri), rollback.map(_.down))

            if (success){
             //laterVersions.map(Version.remove(_))
            } else {
             println("The rollback was unsuccessful!")
            }
          }
        }
        case _ => println("Can't find a version to Rollback to!")
      }
    }
  }
}

object Rollback{

  def apply(targetId:String, uri:String, scripts : List[String]) = {
    new Rollback(targetId, uri, scripts)
  }
}
