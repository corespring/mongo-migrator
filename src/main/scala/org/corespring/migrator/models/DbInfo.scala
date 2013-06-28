package org.corespring.migrator.models

case class DbInfo(
                   hostPort: String,
                   db: String,
                   username: Option[String] = None,
                   password: Option[String] = None,
                   replicaSet: Option[String] = None) {
  def toCmdLine: String = {

    def baseUri = (replicaSet ++ Seq(hostPort, db)).mkString("/")

    List(
      Some("mongo"),
      Some(baseUri),
      username.map("-u " + _),
      password.map("-p " + _)
    ).flatten.mkString(" ")
  }
}

object DbInfo {

  def isValid(uri:String) : Boolean = {
    try{
      DbInfo(uri)
      true
    }
    catch {
      case e : IllegalArgumentException => false
    }
  }

  def apply(uri: String): DbInfo = {

    val setRegex = """(.*?)\|(.*)""".r
    val userPassRegex = """mongodb://(.*?):(.*?)@(.*)/(.*)""".r
    val noUserRegex = """mongodb://(.*)/(.*)""".r

    def matcher: PartialFunction[String, DbInfo] = {
      case userPassRegex(user, pass, hostPort, db) => DbInfo(hostPort, db, Some(user), Some(pass))
      case noUserRegex(hostPort, db) => DbInfo(hostPort, db)
      case _ => null
    }

    val info = uri match {
      case setRegex(set, uri) => {
        val info = matcher(uri)
        if(info != null) info.copy(replicaSet = Some(set)) else info
      }
      case _ => matcher(uri)
    }

    if (info == null) throw new IllegalArgumentException("Bad uri: " + uri) else info
  }

}
