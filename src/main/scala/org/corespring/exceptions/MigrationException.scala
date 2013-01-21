package org.corespring.exceptions

import org.corespring.models._

case class MigrationException( current : List[Script], proposed : List[Script])
  extends RuntimeException("something wrong with the scripts:" + current + ", " + proposed)



