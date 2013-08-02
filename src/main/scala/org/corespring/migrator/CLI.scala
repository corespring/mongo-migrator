package org.corespring.migrator.migrator

import grizzled.slf4j.Logging
import org.corespring.migrator.commands.{BaseCommand,ScriptValidator,Versions,Synch,Migrate,Rollback}
import org.corespring.migrator.models.DbInfo
import scala.Some

object CLI extends App with Logging {

  def handleError(e: Throwable) {
    error("An error has occured: " + e.getMessage)
    error(e.getMessage, e)
    System.exit(1)
  }

  val Header =
    """
      |mongo-migrator
      |-----------------------
      |helps mongo database migrations
    """.stripMargin

  val Usage =
    """
      |mongo-migrator migrate version_id mongo_uri script_path_1 script_path2 ...
      |params:
      |@versionId - an arbitrary string value (eg: a git commit hash)
      |@mongo_uri - a valid mongo uri (eg: mongodb://user:pass@server:port/db)
      |@script_path - a relative path to a script folder
      |
      |mongo-migrator rollback version_id|object_id mongo_uri script_path_1 script_path2 ...
      |
      |mongo-migrator versions mongo_uri
      |
      |mongo-migrator synch db|files version_id mongo_uri scripts*
      |
    """.stripMargin

  object Actions {
    val Migrate = "migrate"
    val Rollback = "rollback"
    val Versions = "versions"
    val Synch = "synch"
  }

  try {

    val argsList = args.toList

    info(s"Mongo Migrator - Args ${argsList.mkString(" ")}")

    argsList match {
      case List() => println(Usage)
      case action :: params => {

        def cmd: Option[BaseCommand] = {
          action match {
            case Actions.Migrate => {
              params match {
                case versionId :: mongoUri :: scripts => {
                  if (DbInfo.isValid(mongoUri))
                    Some(Migrate(mongoUri, scripts, versionId, ScriptValidator.validateContents))
                  else
                    None
                }
                case _ => None
              }
            }
            case Actions.Rollback => {
              params match {
                case targetId :: mongoUri :: scripts => Some(Rollback(targetId, mongoUri, scripts, ScriptValidator.validateContents))
                case _ => None
              }
            }
            case Actions.Versions => Some(Versions(params.head))
            case Actions.Synch => {
              params match {
                case target :: versionId :: mongoUri :: scripts => Some(Synch(target,versionId,mongoUri,scripts))
                case _ => None
              }

            }
            case _ => None
          }
        }

        cmd match {
          case Some(cmd) => {
            cmd.begin
            cmd.cleanup
          }
          case _ => println(Usage)
        }
      }
    }
  }
  catch {
    case io: java.io.IOException => handleError(io)
    case e: Throwable => handleError(e)
  }
}
