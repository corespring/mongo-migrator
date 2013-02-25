package org.corespring.migrator.exceptions

import org.corespring.migrator.models._

case class MigrationException( current : Seq[Script], proposed : Seq[Script])
  extends RuntimeException("something wrong with the scripts:" + current + ", " + proposed)



