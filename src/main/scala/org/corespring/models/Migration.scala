package org.corespring.models

import org.corespring.exceptions.MigrationException

case class Migration(scripts: List[Script])

object Migration {

  def apply(currentVersion: Version, scripts: List[Script]) = {

    /** Trim the incoming scripts so that only new scripts are added to the migration
      * using the following rules:
      * 1. The incoming scripts shall have no items before the current scripts
      * 2. The current scripts must be a contiguous subset of the incoming scripts
      *
      * eg:
      * valid: current: 0,1 -- incoming: 0,1,3
      * invalid current: 0,2 -- incoming: 0,1,2 (the 1 in incoming)
      * invalid current: 1,2 -- incoming: 0,1,2 (the 0 in incoming)
      * @return the new scripts only
      * @throws a MigrationException if the rules above are broken
      */
    def trimmed: List[Script] = {
      currentVersion.scripts match {
        case List() => scripts.sortWith(_.name < _.name)
        case _ => {
          val currentSorted: List[Script] = currentVersion.scripts.sortWith(_.name < _.name)
          val newSorted: List[Script] = scripts.sortWith(_.name < _.name)

          val difference = newSorted.filterNot( currentSorted.contains(_) )
          difference match {
            case List() => List()
            case head :: rest => {
              def contiguous = newSorted.indexOf(head) == currentSorted.length
              if (contiguous) difference else throw new MigrationException(currentSorted, newSorted)
            }
          }
        }
      }
    }

    new Migration(trimmed)
  }
}

