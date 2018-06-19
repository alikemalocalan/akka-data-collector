package com.github.alikemalocalan.model

import slick.jdbc.PostgresProfile.api._
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

case class Token(tokenkey: String, userid: Int, id: Option[Int] = None)

object TokenProtocol extends DefaultJsonProtocol {
  implicit val incomingJsonFormat: RootJsonFormat[Token] = jsonFormat3(Token)
}

// create the schema
//TableQuery[TokenTable].schema.create,
class TokenTable(tag: Tag) extends Table[Token](tag, "TOKEN") {
  def * = (tokenkey, userid, id.?) <> (Token.tupled, Token.unapply)

  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

  def tokenkey = column[String]("TOKENKEY")

  def userid = column[Int]("USERID")
}
