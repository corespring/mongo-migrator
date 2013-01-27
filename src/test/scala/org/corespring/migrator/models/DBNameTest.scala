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
      assertParse("mongodb://ed:password@localhost:1111/one", "localhost", "1111", "one", Some("ed"), Some("password"))
      assertParse("mongodb://ed:pword@localhost:1111/one", "localhost", "1111", "one", Some("ed"), Some("pword"))
    }

    "is valid works" in {

      DbName.isValid(null) === false
      DbName.isValid("") === false
      DbName.isValid("blah blahlla") === false
      DbName.isValid("mongodb://blah blahlla") === false
      DbName.isValid("mongodb://server:port/") === false
      DbName.isValid("mongodb://server:port/name") === true
      DbName.isValid("mongodb://user@server:port/name") === false
    }
  }

}
