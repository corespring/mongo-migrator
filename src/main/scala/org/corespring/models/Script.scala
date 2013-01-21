package org.corespring.models

case class Script(name:String, contents: String){

  def up() : Script = {
   this
  }

  def down() : Script = {
    this
  }
}


