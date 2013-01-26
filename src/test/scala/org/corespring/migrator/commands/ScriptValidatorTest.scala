package org.corespring.migrator.commands

import org.corespring.migrator.models.Script

import org.specs2.mutable.Specification

class ScriptValidatorTest extends Specification {

  "ScriptValidator" should {
    "detect if a script's contents are changed" in {

      val scripts = List(Script("src/test/resources/mock_files/validator/one/1.js", "alert('1')"))
      val paths = List("src/test/resources/mock_files/validator/one")

      ScriptValidator.validateContents(scripts,paths) === true

      val diffScripts = List(Script("src/test/resources/mock_files/validator/one/1.js", "alert('dfferent')"))

      ScriptValidator.validateContents(diffScripts,paths) === false
    }
  }

}
