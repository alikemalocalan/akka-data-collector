package com.github.alikemalocalan.model

import slick.jdbc.PostgresProfile.api._
import spray.json.{DefaultJsonProtocol, RootJsonFormat}


case class Pulse(language: String, xp: Int, userid: Option[Int] = None, id: Option[Int] = None) {
  def updateUserID(user: Int): Pulse = {
    Pulse(language, xp, Some(user), None)
  }
}

object PulseProtocol extends DefaultJsonProtocol {
  implicit val incomingJsonFormat: RootJsonFormat[Pulse] = jsonFormat4(Pulse)
}

// create the schema
//TableQuery[UserTable].schema.create,
class PulseTable(tag: Tag) extends Table[Pulse](tag, "PULSE") {
  def * = (language, xp, userid.?, id.?) <> (Pulse.tupled, Pulse.unapply)

  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

  def xp = column[Int]("XP")

  def userid = column[Int]("USERID")

  def language = column[String]("LANGUAGE")
}

case class PulseRequest(token: String, pulse: Pulse)
