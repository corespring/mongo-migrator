package org.corespring.models

case class Script(name:String, contents: String){

  /**
   * Return a new Script with the up contents of the js file
   * If the file doesn't include '//Down' then the whole file is returned
   * If '//Down' does exist then up is made of all the lines preceding it
   * @return
   */
  def up() : Script = {
   this
  }

  /**
   * Return a new Script with the down contents of the js file
   * If the file doesn't include '//Down' then return the contents as an empty string
   * If '//Down' does exist then down is made of all the lines after it
   * @return
   */
  def down() : Script = {
    this
  }
}


