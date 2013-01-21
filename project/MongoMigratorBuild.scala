import sbt._
import sbt.Keys._

object MongoMigratorBuild extends Build {

  lazy val mongoMigrator = Project(
    id = "mongo-migrator",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name := "mongo-migrator",
      organization := "org.corespring",
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.9.2",
      //because of all the db testing we need - only test serially
      parallelExecution.in(Test) := false,
      testOptions in Test += Tests.Cleanup( (loader: java.lang.ClassLoader) => {
        loader.loadClass("org.corespring.tests.Cleanup").newInstance
      } )
    )
  )
}
