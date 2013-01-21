import sbt._
import sbt.Keys._

object MongomoverBuild extends Build {

  lazy val mongomover = Project(
    id = "mongo-mover",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name := "mongo-mover",
      organization := "org.corespring",
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.9.2"
      // add other settings here
    )
  )
}
