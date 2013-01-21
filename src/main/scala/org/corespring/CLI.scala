package org.corespring

import commands.{Versions, Migrate, Rollback}

object CLI extends App {

  val Header =
    """
      |mongo-migrator
      |-----------------------
      |helps mongo database migrations
    """.stripMargin

  val Usage =
    """
      |mongo-migrator migrate [version_id], mongo_uri script_path_1 script_path2 ...
      |params:
      |@versionId - an arbitrary string value (eg: a git commit hash)
      |@mongo_uri - a valid mongo uri (eg: mongodb://user:pass@server:port/db)
      |@script_path - a relative path to a script folder
      |
      |mongo-migrator rollback version_id|object_id mongo_uri script_path_1 script_path2 ...
      |
      |mongo-migrator versions mongo_uri
      |
    """.stripMargin

  object Actions {
    val Migrate = "migrate"
    val Rollback = "rollback"
    val Versions = "versions"
  }

  args.toList match {
    case List() => println(Usage)
    case action :: params => {

      println("action: " + action)

      action match {
        case Actions.Migrate => {
          params match {
            case versionId :: mongoUri :: scripts if !versionId.startsWith("mongodb://") => {
              Migrate(mongoUri, scripts, Some(versionId)).begin
            }
            case mongoUri :: scripts => Migrate(mongoUri, scripts).begin
            case _ => println(Usage)
          }
        }
        case Actions.Rollback => {
          params match {
            case targetId :: mongoUri :: scripts => Rollback(targetId, mongoUri, scripts).begin
            case _ => println(Usage)
          }
        }
        case Actions.Versions => Versions(params.head).begin
        case _ => println(Usage)
      }

    }
    case _ => println(Usage)
  }
}
