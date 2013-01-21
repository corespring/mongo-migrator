package org.corespring.models

import org.specs2.mutable.{Before, Specification, After}
import org.joda.time.DateTime
import com.mongodb.casbah.commons.MongoDBObject
import org.corespring.helpers.DbTest

class VersionTest extends Specification {

  sequential

  "Version" should {

    def s = scala.Math.random.toString

    "return the current version" in /*new DbTest*/ {
      Version.dropCollection
      Version.create(new Version(s, new DateTime(), List()))
      Version.create(new Version(s, new DateTime(), List()))
      val lastVersion = s
      println("last version: " + lastVersion)
      Version.create(new Version(lastVersion, new DateTime(), List()))
      Version.currentVersion.commitHash must equalTo(lastVersion)
    }

    def script(s: String): Script = new Script(s, s)

    "returns all scripts run" in new DbTest {

      Version.create(new Version(s, new DateTime(), List(script("1"))))
      Version.create(new Version(s, new DateTime(), List(script("2"))))

      Version.create(new Version(s, new DateTime(), List(script("3"))))
      Version.allScripts(Version.currentVersion).length === 3

      println(Version.allScripts(Version.currentVersion))

      Version.create(new Version(s, new DateTime(), List(script("4"))))
      Version.allScripts(Version.currentVersion).length === 4

      Version.create(new Version(s, new DateTime(), List(script("5"))))

      println(Version.allScripts(Version.currentVersion))
      Version.allScripts(Version.currentVersion).length === 5
    }

  }
}

