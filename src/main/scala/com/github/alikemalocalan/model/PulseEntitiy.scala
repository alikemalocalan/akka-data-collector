package com.github.alikemalocalan.model


import java.sql.Timestamp
import java.text.SimpleDateFormat

import slick.jdbc.PostgresProfile.api._
import slick.lifted.ProvenShape
import slick.sql.SqlProfile.ColumnOption.SqlType
import spray.json.{DefaultJsonProtocol, JsString, JsValue, RootJsonFormat}


case class Pulse(language: String, xp: Int, machineid: Option[Int] = None, send_at: Timestamp, inserted_at: Timestamp, updated_at: Timestamp, id: Option[Int] = None) {
  def updateUserID(machine: Int): Pulse = {
    Pulse(language, xp, Some(machine), send_at, inserted_at, updated_at, None)
  }
}

object PulseProtocol extends DefaultJsonProtocol {

  implicit object timestampFormat extends RootJsonFormat[Timestamp] {
    val format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS'Z'")

    override def write(obj: Timestamp): JsValue = JsString(format.format(obj))

    override def read(json: JsValue): Timestamp = {
      val str = json.toString()
      new Timestamp(format.parse(str).getTime)
    }
  }

  implicit val incomingJsonFormat: RootJsonFormat[Pulse] = jsonFormat7(Pulse)
}

// create the schema
//TableQuery[UserTable].schema.create,
class PulseTable(tag: Tag) extends Table[Pulse](tag, "PULSE") {
  def * : ProvenShape[Pulse] = (language, xp, machineid.?, send_at, inserted_at, updated_at, id.?) <> (Pulse.tupled, Pulse.unapply)

  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

  def xp = column[Int]("XP")

  def language = column[String]("LANGUAGE")

  def machineid = column[Int]("MACHINEID")

  def send_at = column[Timestamp]("SEND_AT", SqlType("timestamp not null default CURRENT_TIMESTAMP "))

  def inserted_at = column[Timestamp]("INSERTED_AT", SqlType("timestamp not null default CURRENT_TIMESTAMP "))

  def updated_at = column[Timestamp]("UPDATED_AT", SqlType("timestamp not null default CURRENT_TIMESTAMP "))

  def pk = foreignKey("PULSES_MACHINE", machineid, TableQuery[MachineTable])(_.id)
}

case class PulseRequest(token: String, pulse: Pulse)
