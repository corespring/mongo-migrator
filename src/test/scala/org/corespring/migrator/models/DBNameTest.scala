package org.corespring.migrator.models

import org.specs2.mutable.Specification

class DbNameTest extends Specification {

  "DbName" should {

    def assertParse(
                     uri:String,
                     host: String,
                     port: String,
                     db:String,
                     username:Option[String] = None,
                     password:Option[String] = None) = {

      val dbName = DbName(uri)
      dbName.host === host
      dbName.port === port
      dbName.db === db
      dbName.username === username
      dbName.password === password
    }

    "parse correctly" in {
      assertParse("mongodb://localhost/one", "localhost", "27017", "one")
      assertParse("mongodb://localhost:1111/one", "localhost", "1111", "one")
      assertParse("mongodb://ed@localhost:1111/one", "localhost", "1111", "one", Some("ed"), Some("password"))
      assertParse("mongodb://ed:pword@localhost:1111/one", "localhost", "1111", "one", Some("ed"), Some("pword"))
    }
  }

}
