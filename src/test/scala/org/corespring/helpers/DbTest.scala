package org.corespring.helpers

import org.specs2.mutable.After
import org.corespring.models.Version

trait DbTest extends After {

  def after = Version.dropCollection
}
