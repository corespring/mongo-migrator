package org.corespring.migrator.models

case class Script(name: String, contents: String) {

  private val upContents : String = split._1
  private val downContents : Option[String] = split._2

  /**
   * Return a new Script with the up contents of the js file
   * If the file doesn't include '//Down' then the whole file is returned
   * If '//Down' does exist then up is made of all the lines preceding it
   * @return
   */
  def up: Script = {
    if (upContents.isEmpty){
      throw new RuntimeException("Error: this script has no Up portion: " + name)
    }
    this.copy(contents = upContents)
  }

  /**
   * Return a new Script with the down contents of the js file
   * If the file doesn't include '//Down' then return the contents as an empty string
   * If '//Down' does exist then down is made of all the lines after it
   * @return
   */
  def down: Option[Script] =  downContents.map( d => this.copy(contents = d) )

  private def split() : (String, Option[String]) = {
    val split = this.contents.split("\n" + Script.DownMarker + "\n")
    (split(0).trim, if (split.length == 2) Some(split(1).trim) else None)
  }

}

object Script {
  val DownMarker: String = "//Down"
}


