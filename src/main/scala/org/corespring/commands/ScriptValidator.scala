package org.corespring.commands

import org.corespring.models.Script

object ScriptValidator {

  /** Validate that the script contents match those that are stored in the files
    *
    * @param scripts
    * @param paths
    * @return
    */
  def validateContents(scripts: List[Script], paths: List[String]): Boolean = {

    val fileScripts: List[Script] = ScriptSlurper.scriptsFromPaths(paths)

    val fileScriptsFiltered: List[Script] = fileScripts.filter(fs => scripts.exists(s => fs.name == s.name))

    val fileScriptsThatAreDifferent = fileScriptsFiltered.filter{ fs =>
      val scriptThatDiffers = scripts.find(_.name == fs.name) match {
        case Some(dbScript) => if(dbScript.contents != fs.contents) Some(dbScript) else None
        case _ => None
      }
      scriptThatDiffers.isDefined
    }

    fileScriptsThatAreDifferent.length == 0
  }
}
