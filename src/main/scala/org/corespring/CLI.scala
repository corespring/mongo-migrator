package org.corespring

import commands.Migrate

object CLI extends App {

  val Header =
    """
      |mongo-mover
      |-----------------------
      |helps mongo database migrations
    """.stripMargin

  val Usage =
    """
      |mongo-mover migrate mongo_uri script_path_1 script_path2 ...
      |mongo-mover rollback mongo_uri uid script_path_1 script_path2 ...
      |mongo-mover versions mongo_uri
      |
    """.stripMargin

  object Actions{
    val Migrate = "migrate"
    val Rollback = "rollback"
    val Versions = "versions"
  }

  args.toList match {
    case List() => println(Usage)
    case action :: mongoUri :: scripts => {

      println( "action: " + action )
      println( "mongoUri: " + mongoUri)
      println( "scripts: " + scripts)

      action match {
        case Actions.Migrate => Migrate(mongoUri, scripts).begin
        case Actions.Rollback => {

          println(">> rollback")

        }
        case Actions.Versions => {
          println(">> versions")

        }
        case _ => println(Usage)
      }

    }


    case _ => println(Usage)
  }


}
