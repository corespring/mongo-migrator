import sbt._
import sbt.Keys._
import sbtassembly.Plugin._
import sbtrelease.ReleasePlugin._

object Build extends sbt.Build {

  (credentials in ThisBuild) += {
    val envCredentialsPath = System.getenv("CREDENTIALS_PATH")
    val path = if (envCredentialsPath != null) envCredentialsPath else Seq(Path.userHome / ".ivy2" / ".credentials").mkString
    val f: File = file(path)
    if (f.exists()) {
      println("[credentials] using credentials file")
      Credentials(f)
    } else {
      //https://devcenter.heroku.com/articles/labs-user-env-compile
      def repoVar(s: String) = System.getenv("ARTIFACTORY_" + s)
      val args = Seq("REALM", "HOST", "USER", "PASS").map(repoVar)
      println("[credentials] args: " + args)
      Credentials(args(0), args(1), args(2), args(3))
    }
  }

  publishTo in ThisBuild <<= version {
    (v: String) =>
      def isSnapshot = v.trim.contains("-")
      val base = "http://repository.corespring.org/artifactory"
      val repoType = if (isSnapshot) "snapshot" else "release"
      val finalPath = base + "/ivy-" + repoType + "s"
      Some("Artifactory Realm" at finalPath)
  }


  object Dependencies {

    val all = Seq(
      "org.mongodb" %% "casbah" % "2.6.2",
      "org.specs2" %% "specs2" % "2.1.1" % "test",
      "com.novus" %% "salat" % "1.9.2",
      "org.clapper" % "grizzled-slf4j_2.10" % "1.0.1",
      "ch.qos.logback" % "logback-classic" % "1.0.3"
    )
  }

  object Resolvers {

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
    settings = Project.defaultSettings ++ assemblySettings ++ releaseSettings ++ Seq(
      name := "mongo-migrator",
      organization := "org.corespring",
      scalaVersion := "2.10.3",
      libraryDependencies ++= Dependencies.all,
      resolvers ++= Resolvers.all,
      //because of all the db testing we need - only test serially
      parallelExecution.in(Test) := false,
      testOptions in Test += Tests.Cleanup((loader: java.lang.ClassLoader) => {
        loader.loadClass("org.corespring.migrator.tests.Cleanup").newInstance
      })
    )
  )
}
