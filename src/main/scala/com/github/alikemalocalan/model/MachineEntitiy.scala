package com.github.alikemalocalan.model

import java.sql.Timestamp

import com.github.alikemalocalan.utils.DateUtils
import slick.jdbc.PostgresProfile.api._
import slick.sql.SqlProfile.ColumnOption.SqlType
import spray.json.{DefaultJsonProtocol, JsString, JsValue, RootJsonFormat}

case class Machine(name: String,
                   userid: Int,
                   activated: Boolean,
                   inserted_at: Timestamp,
                   updated_at: Timestamp,
                   id: Option[Int] = None)

object MachineProtocol extends DefaultJsonProtocol {

  implicit object timestampFormat extends RootJsonFormat[Timestamp] {

    override def write(obj: Timestamp): JsValue = JsString(DateUtils.timestampToString(obj))

    override def read(json: JsValue): Timestamp = {
      DateUtils.strToTimestamp(json.convertTo[String])
    }
  }

  implicit val incomingJsonFormat: RootJsonFormat[Machine] = jsonFormat6(Machine)
}

// create the schema
//TableQuery[UserTable].schema.create,
class MachineTable(tag: Tag) extends Table[Machine](tag, "MACHINE") {
  def * = (name, userid, activated, created_date, last_modified_date, id.?) <> (Machine.tupled, Machine.unapply)

  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

  def name = column[String]("NAME")

  def userid = column[Int]("USERID")

  def activated = column[Boolean]("ISACTIVE")

  def created_date = column[Timestamp]("CREATED_DATE", SqlType("timestamp not null default CURRENT_TIMESTAMP "))

  def last_modified_date = column[Timestamp]("LAST_MODIFIED_DATE", SqlType("timestamp not null default CURRENT_TIMESTAMP "))


  def pk = foreignKey("MACHINES_USER", userid, TableQuery[UserTable])(_.id)
}
