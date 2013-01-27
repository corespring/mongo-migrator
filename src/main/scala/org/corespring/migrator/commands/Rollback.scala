package org.corespring.migrator.commands

import org.corespring.migrator.models.{Script,Version, DbName}
import org.corespring.migrator.shell.RollbackShell

class Rollback(
               targetId:String,
               uri:String,
               scriptPaths:List[String],
               validateContents : (List[Script], List[String]) => Boolean) extends BaseCommand(uri){



  private def pf[T]: PartialFunction[Option[T], Option[T]] = {
    case Some(thing) => Some(thing)
    case None => None
  }

  override def begin = {
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

            val allLaterVersionScripts = laterVersions.map( _.scripts ).flatten

            val isValid = validateContents(allLaterVersionScripts, scriptPaths)

            if (!isValid){
              throw new RuntimeException("The scripts in the db and in the files don't match")
            }

            def rollbackScripts:List[Script] = {
              val allScripts = laterVersions.map( _.scripts ).flatten
              val reversed = allScripts.reverse
              val downScripts = reversed.map(_.down).flatten
              downScripts
            }

            val success = RollbackShell.run(DbName(uri), rollbackScripts)

            if (success){
             laterVersions.map(Version.remove(_))
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

  def alwaysValid(scripts:List[Script], paths:List[String]) : Boolean = true

  def apply(targetId:String, uri:String, scripts : List[String], validateFn : (List[Script], List[String]) => Boolean = alwaysValid) = {
    new Rollback(targetId, uri, scripts, validateFn)
  }
}
