package org.corespring.migrator.shell

object RollbackShell extends BaseShell {

  override def prepareScript(contents: String): String = {

    val template =
      """
        |try{
        |  print("running down.");
        |  down();
        |  print("done.");
        |} catch (e) {
        |  //do nothing
        |}
      """.stripMargin

    contents + "\n" + template
  }
}


