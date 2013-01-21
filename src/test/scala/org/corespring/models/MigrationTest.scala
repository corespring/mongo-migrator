package org.corespring.models

import org.specs2.mutable.{Specification, After}
import org.joda.time.DateTime
import org.corespring.exceptions.MigrationException
import org.corespring.helpers.{DbSingleton, DbTest}

class MigrationTest extends Specification {

  sequential

  Version.init(DbSingleton.db)

  "Migration" should {

    "correctly remove scripts that have already been applied to the current version" in new DbTest {

      val current = script("1")
      val newScript = script("2")
      val v = version(List(current))
      val newScripts = List(current,newScript)
      val newMigration = Migration(v, newScripts)
      newMigration.scripts === newScripts.tail
    }

    def script(name:String) = new Script("dbchanges/" + name + ".js", "alert('" + name + "');")

    def version(s:List[Script]) = new Version(dateCreated = new DateTime(), scripts = s)

    "throw an error if there is a missing script - one" in new DbTest {
      val v = version(List(script("1")))
      Migration(v, List(script("0"), script("1"))) must throwA[MigrationException]
    }

    "throw an error if there is a missing script - two" in new DbTest {
      val v = version(List(script("1"), script("2")))
      Migration(v, List(script("1"), script("3"))) must throwA[MigrationException]
    }
  }

}

