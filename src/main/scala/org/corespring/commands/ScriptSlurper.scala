package org.corespring.commands

import java.io.File
import org.corespring.models.Script

object ScriptSlurper {

  def scriptsFromPaths(paths: List[String]): List[Script] = {

    def readContents(f:File) : String = {
      val source = scala.io.Source.fromFile(f)
      val lines = source .mkString
      source.close ()
      lines
    }

    val allFiles: List[File] = paths.map(folder => recursiveListFiles(new File(folder))).flatten
    allFiles
      .filter(f => f.isFile && f.getName.endsWith(".js"))
      .map(f => new Script(f.getPath, readContents(f)))
  }

  private def recursiveListFiles(f: File): List[File] = {

    if (!f.exists())
      List()
    else {
      val these = f.listFiles.toList
      these ++ these.filter(_.isDirectory).flatMap(recursiveListFiles)
    }
  }
}
