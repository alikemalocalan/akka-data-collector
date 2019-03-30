package com.github.alikemalocalan.model


import java.sql.Timestamp

import slick.jdbc.PostgresProfile.api._
import slick.sql.SqlProfile.ColumnOption.SqlType
import spray.json.{DefaultJsonProtocol, RootJsonFormat}


case class Pulse(
                  id: Option[Int] = None,
                  user_id: Int,
                  machine_id: Int,
                  pulse_id: String,
                  lang_name: String,
                  xp_amount: Int,
                  send_at: Timestamp,
                  inserted_at: Timestamp,
                  updated_at: Timestamp,
                  send_at_local: Timestamp,
                  tz_offset: Int
                )

object PulseProtocol extends DefaultJsonProtocol {

  import com.github.alikemalocalan.utils.DateUtils.TimestampFormat

  implicit val incomingJsonFormat: RootJsonFormat[Pulse] = jsonFormat11(Pulse)
}

// create the schema
//TableQuery[UserTable].schema.create,
class PulseTable(tag: Tag) extends Table[Pulse](tag, "pulses") {
  def * = (id.?, user_id, machine_id, pulse_id, lang_name, xp_amount, send_at, inserted_at, updated_at, send_at_local, tz_offset) <> (Pulse.tupled, Pulse.unapply)

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def user_id = column[Int]("user_id")

  def machine_id = column[Int]("machine_id")

  def pulse_id = column[String]("pulse_id")

  def lang_name = column[String]("lang_name")

  def xp_amount = column[Int]("xp_amount")

  def send_at = column[Timestamp]("sent_at", SqlType("timestamp not null default CURRENT_TIMESTAMP "))

  def inserted_at = column[Timestamp]("inserted_at", SqlType("timestamp not null default CURRENT_TIMESTAMP "))

  def updated_at = column[Timestamp]("updated_at", SqlType("timestamp not null default CURRENT_TIMESTAMP "))

  def send_at_local = column[Timestamp]("sent_at_local")

  def tz_offset = column[Int]("tz_offset")


  def pk_machine = foreignKey("pulses_machine_id_fkey", machine_id, TableQuery[MachineTable])(_.id)

  def pk_user = foreignKey("pulses_user_id_fkey", user_id, TableQuery[UserTable])(_.id)
}