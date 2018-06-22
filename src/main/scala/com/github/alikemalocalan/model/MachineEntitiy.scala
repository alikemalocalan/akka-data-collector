package com.github.alikemalocalan.model

import java.sql.Timestamp
import java.text.SimpleDateFormat

import slick.jdbc.PostgresProfile.api._
import slick.sql.SqlProfile.ColumnOption.SqlType
import spray.json.{DefaultJsonProtocol, JsString, JsValue, RootJsonFormat}

case class Machine(name: String, userid: Int, send_at: Timestamp, inserted_at: Timestamp, updated_at: Timestamp, id: Option[Int] = None)

object MachineProtocol extends DefaultJsonProtocol {

  implicit object timestampFormat extends RootJsonFormat[Timestamp] {
    val format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS'Z'")

    override def write(obj: Timestamp): JsValue = JsString(format.format(obj))

    override def read(json: JsValue): Timestamp = {
      val str = json.toString()
      new Timestamp(format.parse(str).getTime)
    }
  }

  implicit val incomingJsonFormat: RootJsonFormat[Machine] = jsonFormat6(Machine)
}

// create the schema
//TableQuery[UserTable].schema.create,
class MachineTable(tag: Tag) extends Table[Machine](tag, "MACHINE") {
  def * = (name, userid, send_at, inserted_at, updated_at, id.?) <> (Machine.tupled, Machine.unapply)

  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

  def name = column[String]("NAME")

  def userid = column[Int]("USERID")

  def send_at = column[Timestamp]("SEND_AT", SqlType("timestamp not null default CURRENT_TIMESTAMP "))

  def inserted_at = column[Timestamp]("INSERTED_AT", SqlType("timestamp not null default CURRENT_TIMESTAMP "))

  def updated_at = column[Timestamp]("UPDATED_AT", SqlType("timestamp not null default CURRENT_TIMESTAMP "))


  def pk = foreignKey("MACHINES_USER", userid, TableQuery[UserTable])(_.id)
}
