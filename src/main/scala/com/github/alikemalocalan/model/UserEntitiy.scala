package com.github.alikemalocalan.model

import slick.jdbc.PostgresProfile.api._
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

case class User(name: String, id: Option[Int] = None)

object UserProtocol extends DefaultJsonProtocol {
  implicit val incomingJsonFormat: RootJsonFormat[User] = jsonFormat2(User)
}

// create the schema
//TableQuery[UserTable].schema.create,
class UserTable(tag: Tag) extends Table[User](tag, "USER") {
  def * = (name, id.?) <> (User.tupled, User.unapply)

  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

  def name = column[String]("NAME")
}
