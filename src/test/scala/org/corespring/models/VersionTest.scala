package org.corespring.models

import org.specs2.mutable.Specification
import org.joda.time.DateTime
import org.corespring.helpers.{DbSingleton, DbTest}
import org.corespring.log.Logger
import com.mongodb.casbah.MongoConnection
import com.mongodb.casbah.MongoDB

class VersionTest extends Specification {

  sequential

  import Logger._

  private val log = Logger.get("VersionTest")

  "Version" should {

    "log" in {

      log.info("info - 1")
      log.fine("debug- 1")
    }

    Version.init(DbSingleton.db)

    def s = scala.Math.random.toString

    "return the current version" in /*new DbTest*/ {

      Version.dropCollection
      create(new DateTime(), List(), s)
      create(new DateTime(), List(), s)
      val lastVersion = s
      println("last version: " + lastVersion)
      create(new DateTime(), List(), lastVersion)
      Version.currentVersion.versionId must equalTo(Some(lastVersion))
    }

    def script(s: String): Script = new Script(s, s)

    def version(d: DateTime, s: List[Script], v: String): Version = new Version(d, s, Some(v))

    def create(d: DateTime, s: List[Script], v: String) = Version.create(version(d, s, v))

    "returns all scripts run" in new DbTest {

      Version.dropCollection

      log.info(Version.allScripts(Version.currentVersion))

      Version.allScripts(Version.currentVersion).length === 0
      create(new DateTime(), List(script("1")), s)
      create(new DateTime(), List(script("2")), s)
      create(new DateTime(), List(script("3")), s)
      log.info(Version.allScripts(Version.currentVersion))

      Version.allScripts(Version.currentVersion).length === 3
      create(new DateTime(), List(script("4")), s)
      log.info(Version.allScripts(Version.currentVersion))

      Version.allScripts(Version.currentVersion).length === 4
      create(new DateTime(), List(script("5")), s)
      log.info(Version.allScripts(Version.currentVersion))

      Version.allScripts(Version.currentVersion).length === 5
    }

  }
}

