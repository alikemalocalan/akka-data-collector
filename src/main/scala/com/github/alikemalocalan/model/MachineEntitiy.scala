package com.github.alikemalocalan.model

import java.sql.Timestamp

import slick.jdbc.PostgresProfile.api._
import slick.sql.SqlProfile.ColumnOption.SqlType
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

case class Machine(
                    id: Option[Int] = None,
                    name: String,
                    created_at: Option[Timestamp] = None,
                    userid: Int,
                    inserted_at: Timestamp,
                    updated_at: Timestamp,
                    api_salt: String,
                    activated: Boolean
                  )

object MachineProtocol extends DefaultJsonProtocol {

  import com.github.alikemalocalan.utils.DateUtils.TimestampFormat

  implicit val incomingJsonFormat: RootJsonFormat[Machine] = jsonFormat8(Machine)
}

// create the schema
//TableQuery[UserTable].schema.create,
class MachineTable(tag: Tag) extends Table[Machine](tag, "machines") {
  def * = (id.?, name, created_at.?, userid, inserted_at, updated_at, api_salt, activated) <> (Machine.tupled, Machine.unapply)

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def name = column[String]("name")

  def created_at = column[Timestamp]("created_at", SqlType("timestamp not null default CURRENT_TIMESTAMP "))

  def inserted_at = column[Timestamp]("inserted_at", SqlType("timestamp not null default CURRENT_TIMESTAMP "))

  def updated_at = column[Timestamp]("updated_at", SqlType("timestamp not null default CURRENT_TIMESTAMP "))

  def api_salt = column[String]("api_salt")

  def activated = column[Boolean]("active")

  def pk = foreignKey("machines_user_id_fkey", userid, TableQuery[UserTable])(_.id)

  def userid = column[Int]("user_id")
}
