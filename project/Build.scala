import sbt._
import sbt.Keys._
import sbtassembly.Plugin._

object Build extends sbt.Build {


  object Dependencies{

    val all = Seq(
      "org.mongodb" %% "casbah" % "2.6.2",
      "org.specs2" %% "specs2" % "2.1.1" % "test",
      "com.novus" %% "salat" % "1.9.2",
      "org.clapper" % "grizzled-slf4j_2.10" % "1.0.1",
      "ch.qos.logback" % "logback-classic" % "1.0.3"
      )
  }

  object Resolvers{

    val all = Seq(
      "Sbt plugins" at "http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases/",
      "Sonatype OSS" at "http://oss.sonatype.org/content/repositories/releases/",
      "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
      "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
      "mvn repo" at "http://repo1.maven.org/maven2/",
      "repo.novus rels" at "http://repo.novus.com/releases/",
      "repo.novus snaps" at "http://repo.novus.com/snapshots/"
      )
  }


  lazy val mongoMigrator = Project(
    id = "mongo-migrator",
    base = file("."),
    settings = Project.defaultSettings ++ assemblySettings ++ Seq(
      name := "mongo-migrator",
      organization := "org.corespring",
      version := "0.2.2",
      scalaVersion := "2.10.2",
      libraryDependencies ++= Dependencies.all,
      resolvers ++= Resolvers.all,
      //because of all the db testing we need - only test serially
      parallelExecution.in(Test) := false,
      testOptions in Test += Tests.Cleanup( (loader: java.lang.ClassLoader) => {
        loader.loadClass("org.corespring.migrator.tests.Cleanup").newInstance
        } )
      )
    )
}
