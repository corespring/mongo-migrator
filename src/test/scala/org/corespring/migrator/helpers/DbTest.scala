package org.corespring.migrator.helpers

import org.specs2.mutable.After
import org.corespring.migrator.models.Version

trait DbTest extends After {
  def after = Version.dropCollection
}

