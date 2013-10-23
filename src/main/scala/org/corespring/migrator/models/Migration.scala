package org.corespring.migrator.models

import org.corespring.migrator.exceptions.NonContiguousMigrationException

case class Migration(scripts: Seq[Script])

object Migration {

  def apply(currentVersion: Version, scripts: Seq[Script]) = {

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
    def trimmed: Seq[Script] = {
      currentVersion.scripts match {
        case List() => scripts.sortWith(_.name < _.name)
        case _ => {
          val currentSorted: Seq[Script] = currentVersion.scripts.sortWith(_.name < _.name)
          val newSorted: Seq[Script] = scripts.sortWith(_.name < _.name)

          def isExistingScript(n:Script) : Boolean = currentSorted.exists( currentScript => currentScript.name == n.name)

          val difference = newSorted.filterNot(isExistingScript)

          difference match {
            case List() => List()
            case head :: rest => {
              def contiguous = newSorted.indexOf(head) == currentSorted.length
              if (contiguous) difference else throw new NonContiguousMigrationException(currentSorted, newSorted)
            }
          }
        }
      }
    }

    new Migration(trimmed)
  }
}

