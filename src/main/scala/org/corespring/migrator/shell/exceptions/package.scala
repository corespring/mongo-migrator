package org.corespring.migrator.shell

package object exceptions {
  class ShellException(msg:String) extends RuntimeException(msg)
}
