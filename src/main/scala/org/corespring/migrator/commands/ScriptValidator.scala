package org.corespring.migrator.commands

import org.corespring.migrator.models.Script

object ScriptValidator {

  /** Validate that the script contents match those that are stored in the files
    *
    * @param scripts
    * @param paths
    * @return
    */
  def validateContents(scripts: Seq[Script], paths: List[String]): Boolean = {

    val fileScripts: Seq[Script] = ScriptSlurper.scriptsFromPaths(paths)

    val fileScriptsFiltered: Seq[Script] = fileScripts.filter(fs => scripts.exists(s => fs.name == s.name))

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
