package org.corespring.models

import com.novus.salat.{TypeHintFrequency, StringTypeHintStrategy, Context}

package object mongoContext {
  implicit val context = {
    val context = new Context {
      val name = "global"
      override val typeHintStrategy = StringTypeHintStrategy(when = TypeHintFrequency.WhenNecessary, typeHint = "_t")
    }

    context.registerGlobalKeyOverride(remapThis = "id", toThisInstead = "_id")
    context
  }
}
