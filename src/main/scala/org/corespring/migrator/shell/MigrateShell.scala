package org.corespring.migrator.shell


object MigrateShell extends BaseShell {

  override def prepareScript(contents: String): String = {

    val template =
      """
        |if(!this["up"]){
        |  throw "You need to specify an 'up' function";
        |}
        |
        |up();
      """.stripMargin

    contents + "\n" + template
  }
}

