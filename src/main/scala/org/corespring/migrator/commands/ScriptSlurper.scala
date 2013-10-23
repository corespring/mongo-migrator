package org.corespring.migrator.commands

import java.io.File
import org.corespring.migrator.models.Script
import grizzled.slf4j.Logging

object ScriptSlurper extends Logging {

  def scriptsFromPaths(paths: Seq[String]): Seq[Script] = {

    def readContents(f: File): String =
      try {
        val source = scala.io.Source.fromFile(f)("utf-8")
        val lines = source.getLines()
        val lineString = lines.mkString("\n")
        source.close()
        lineString
      } catch {
        case e: Throwable => {
          logger.info(s"Error: ${e.getMessage}")
          throw new RuntimeException(s"Error reading file: ${f.getPath}")
        }
      }


    val allFiles: Seq[File] = paths.map(folder => recursiveListFiles(new File(folder))).flatten

    allFiles
      .filter(f => f.isFile && f.getName.endsWith(".js"))
      .map(f => new Script(f.getPath, readContents(f)))
  }

  private def recursiveListFiles(f: File): Seq[File] = f match {
    case doesntExit : File if !f.exists() => List()
    case file: File if f.isFile => List(file)
    case directory: File if f.isDirectory => {
      val files = directory.listFiles.toList
      files ++ files.filter(_.isDirectory).flatMap(recursiveListFiles)
    }
    case _ => List()
  }

}
