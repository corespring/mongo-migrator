package org.corespring.models

import org.specs2.mutable.Specification

class ScriptTest extends Specification {

  "Script" should {

    val upContents = """
                       |function up(o){ return o; }
                     """.stripMargin
    val downContents =
      """
        |function down(o){ return o; }
      """.stripMargin

    "return up/down for js with no '//Down'" in {
      new Script("one.js", upContents).up.contents === upContents.trim
      new Script("one.js", upContents).down === None
    }

    "return up/down for js with '//Down'" in {
      val combined = List(upContents, Script.DownMarker, downContents).mkString("\n")
      new Script("one.js", combined).up.contents === upContents.trim
      new Script("one.js", combined).down.get.contents === downContents.trim
    }

    "ignores the marker when its part of another comment" in {
      val combined = List(upContents, Script.DownMarker + " along the street", downContents).mkString("\n")
      new Script("one.js", combined).up.contents === combined.trim
      new Script("one.js", combined).down === None
    }

    "if marker is first line of script - it throws an exception" in {
      val combined = List("\n", Script.DownMarker, downContents).mkString("\n")
      new Script("one.js", combined).up must  throwA[RuntimeException]
      new Script("one.js", combined).down.get.contents === downContents.trim
    }
  }

}
