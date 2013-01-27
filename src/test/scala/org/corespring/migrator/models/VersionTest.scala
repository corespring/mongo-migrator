package org.corespring.migrator.models

import org.specs2.mutable.Specification
import org.joda.time.DateTime
import org.corespring.migrator.helpers.{DbSingleton, DbTest}
import org.corespring.migrator.log.Logger
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

    def s = scala.math.random.toString

    def script(s: String): Script = new Script(s, s)

    def version(d: DateTime, s: List[Script], v: String): Version = new Version(d, s, v)

    def create(d: DateTime, s: List[Script], v: String) : Version = Version.create(version(d, s, v))

    "return the current version" in /*new DbTest*/ {

      Version.dropCollection
      create(new DateTime(), List(), s)
      create(new DateTime(), List(), s)
      val lastVersion = s
      println("last version: " + lastVersion)
      create(new DateTime(), List(), lastVersion)
      Version.currentVersion.versionId must equalTo(lastVersion)
    }


    "returns all scripts run" in new DbTest {

      Version.dropCollection

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

    "returns all later versions" in {

      val one = create(new DateTime(), List(script("1")), s)
      val two = create(new DateTime(), List(script("2")), s)
      val three = create(new DateTime(), List(script("3")), s)
      val four = create(new DateTime(), List(script("4")), s)
      val five = create(new DateTime(), List(script("5")), s)
      val six = create(new DateTime(), List(script("6")), s)

      Version.findVersionsLaterThan(one).length === 5
      Version.findVersionsLaterThan(one) === List(two,three,four,five,six)
      Version.findVersionsLaterThan(one) !== List(three, two,four,five,six)

      Version.findVersionsLaterThan(two).length === 4
      Version.findVersionsLaterThan(three).length === 3
      Version.findVersionsLaterThan(four).length === 2
      Version.findVersionsLaterThan(five).length === 1
      Version.findVersionsLaterThan(six).length === 0
    }

  }
}

