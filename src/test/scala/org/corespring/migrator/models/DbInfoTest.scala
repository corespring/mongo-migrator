package org.corespring.migrator.models

import org.specs2.mutable.Specification

class DbInfoTest extends Specification {


  "Db info" should {

    "create a simple info set" in {
      DbInfo("mongodb://localhost:27017/db") === DbInfo("localhost:27017", "db")
    }

    "add user/pass" in {
      DbInfo("mongodb://ed:pass@localhost:27017/db") === DbInfo("localhost:27017", "db", Some("ed"), Some("pass"))
    }

    "read the replica set" in {
      DbInfo("my-set|mongodb://ed:pass@server:27017,server2:27017/db") ===
        DbInfo("server:27017,server2:27017", "db", Some("ed"), Some("pass"), Some("my-set"))
    }

    "throw an exception for a bad uri" in {
      { DbInfo("...") } must throwA[IllegalArgumentException]
    }

    "command line works" in {

      DbInfo("mongodb://localhost:27017/db").toCmdLine === "mongo localhost:27017/db"
      DbInfo("mongodb://ed:pass@localhost:27017/db").toCmdLine === "mongo localhost:27017/db -u ed -p pass"

      DbInfo("my-set|mongodb://ed:pass@server:27017,server2:27017/db").toCmdLine ===
        "mongo my-set/server:27017,server2:27017/db -u ed -p pass"
    }
  }

}
