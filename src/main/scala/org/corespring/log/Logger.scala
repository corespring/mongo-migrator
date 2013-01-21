package org.corespring.log

object Logger extends {

  def get(name:String) : com.twitter.logging.Logger = {
    com.twitter.logging.Logger.get(name)
  }

}
