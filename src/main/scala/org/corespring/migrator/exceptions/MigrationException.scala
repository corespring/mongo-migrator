package org.corespring.migrator.exceptions

import org.corespring.migrator.models._

case class MigrationException( current : List[Script], proposed : List[Script])
  extends RuntimeException("something wrong with the scripts:" + current + ", " + proposed)



