package org.corespring.migrator.commands

import org.specs2.mutable.Specification
import org.corespring.migrator.models.Script

class ScriptSlurperTest extends Specification {

  "ScriptSlurper" should {

    "read all the files one" in {
      val scripts = ScriptSlurper.scriptsFromPaths(List("src/test/resources/mock_files/one"))
      scripts.length === 1
    }

    "read a single file one" in {
      val scripts = ScriptSlurper.scriptsFromPaths(List("src/test/resources/mock_files/one/1.js"))
      scripts.length === 1
    }

    "reads the contents" in {
      val scripts = ScriptSlurper.scriptsFromPaths(List("src/test/resources/mock_files/one"))
      val s = """function up(){
                |    alert("1");
                |}""".stripMargin

      scripts(0).contents === s
      scripts(0).name === "src/test/resources/mock_files/one/1.js"
    }

    "read all the files two" in {
      val scripts = ScriptSlurper.scriptsFromPaths(List("src/test/resources/mock_files/two"))
      scripts.length === 2
    }

    "only loads js" in {
      val scripts = ScriptSlurper.scriptsFromPaths(List("src/test/resources/mock_files/three"))
      scripts.length === 1
    }

    "loads from multiple" in {
      val scripts = ScriptSlurper.scriptsFromPaths(
        List(
          "src/test/resources/mock_files/one",
          "src/test/resources/mock_files/two",
          "src/test/resources/mock_files/three"
        )
      )
      scripts.length === 4
    }
  }

}
