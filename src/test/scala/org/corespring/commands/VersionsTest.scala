package org.corespring.commands

import org.specs2.mutable.{After, Specification}
import org.joda.time.DateTime
import org.corespring.helpers.DbSingleton
import org.corespring.models.Version

class VersionsTest extends Specification {

  sequential
  Version.init(DbSingleton.db)

  "Versions" should {
    "list all the versions" in new dbTest {
      Version.create( new Version( new DateTime(), List(), Some("versionOne")))
      Version.create( new Version( new DateTime(), List(), Some("versionTwo")))
      Versions(DbSingleton.mongoUri).begin
    }
  }
}

trait dbTest extends After{
  def after = Version.dropCollection
}
