package com.github.alikemalocalan.model


import java.sql.Timestamp

import com.github.alikemalocalan.utils.DateUtils
import slick.jdbc.PostgresProfile.api._
import slick.lifted.ProvenShape
import slick.sql.SqlProfile.ColumnOption.SqlType
import spray.json.{DefaultJsonProtocol, JsString, JsValue, RootJsonFormat}

import scala.util.Try


case class Pulse(language: String, xp: Int,
                 machineid: Option[Int] = None,
                 send_at: Option[Timestamp] = None,
                 created_date: Option[Timestamp] = None,
                 last_modified_date: Option[Timestamp] = None,
                 id: Option[Int] = None) {
  def updateMachineID(machine: Int): Pulse = {
    Pulse(language, xp, Try(machine).toOption, send_at, Try(DateUtils.now()).toOption, Try(DateUtils.now()).toOption, None)
  }
}

object PulseProtocol extends DefaultJsonProtocol {

  implicit object timestampFormat extends RootJsonFormat[Timestamp] {

    override def write(obj: Timestamp): JsValue = JsString(DateUtils.timestampToString(obj))

    override def read(json: JsValue): Timestamp = {
      DateUtils.strToTimestamp(json.convertTo[String])
    }
  }

  implicit val incomingJsonFormat: RootJsonFormat[Pulse] = jsonFormat7(Pulse)
}

// create the schema
//TableQuery[UserTable].schema.create,
class PulseTable(tag: Tag) extends Table[Pulse](tag, "PULSE") {
  def * : ProvenShape[Pulse] = (language, xp, machineid.?, send_at.?, created_date.?, last_modified_date.?, id.?) <> (Pulse.tupled, Pulse.unapply)

  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

  def xp = column[Int]("XP")

  def language = column[String]("LANGUAGE")

  def machineid = column[Int]("MACHINEID")

  def send_at = column[Timestamp]("SEND_AT", SqlType("timestamp not null default CURRENT_TIMESTAMP "))

  def created_date = column[Timestamp]("CREATED_DATE", SqlType("timestamp not null default CURRENT_TIMESTAMP "))

  def last_modified_date = column[Timestamp]("LAST_MODIFIED_DATE", SqlType("timestamp not null default CURRENT_TIMESTAMP "))

  def pk = foreignKey("PULSES_MACHINE", machineid, TableQuery[MachineTable])(_.id)
}

case class PulseRequest(token: String, pulse: Pulse)
