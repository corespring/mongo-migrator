package org.corespring.migrator.exceptions

import org.corespring.migrator.models._

case class NonContiguousMigrationException( current : Seq[Script], proposed : Seq[Script])
  extends RuntimeException(
    s"""The scripts aren't contiguous
      current:
      ${current.map(_.name).mkString("\n")},
      new:
      ${proposed.map(_.name).mkString("\n")}
      """
  )



