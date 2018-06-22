package com.github.alikemalocalan.model

import java.sql.Timestamp
import java.text.SimpleDateFormat

import slick.jdbc.PostgresProfile.api._
import slick.sql.SqlProfile.ColumnOption.SqlType
import spray.json.{DefaultJsonProtocol, JsString, JsValue, RootJsonFormat}

case class Token(tokenkey: String, machineid: Option[Int] = None, send_at: Timestamp, inserted_at: Timestamp, updated_at: Timestamp, id: Option[Int] = None)

object TokenProtocol extends DefaultJsonProtocol {

  implicit object timestampFormat extends RootJsonFormat[Timestamp] {
    val format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS'Z'")

    override def write(obj: Timestamp): JsValue = JsString(format.format(obj))

    override def read(json: JsValue): Timestamp = {
      val str = json.toString()
      new Timestamp(format.parse(str).getTime)
    }
  }


  implicit val incomingJsonFormat: RootJsonFormat[Token] = jsonFormat6(Token)
}

// create the schema
//TableQuery[TokenTable].schema.create,
class TokenTable(tag: Tag) extends Table[Token](tag, "TOKEN") {
  def * = (tokenkey, machineid.?, send_at, inserted_at, updated_at, id.?) <> (Token.tupled, Token.unapply)

  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

  def tokenkey = column[String]("TOKENKEY")

  def machineid = column[Int]("MACHINEID")

  def send_at = column[Timestamp]("SEND_AT", SqlType("timestamp not null default CURRENT_TIMESTAMP "))

  def inserted_at = column[Timestamp]("INSERTED_AT", SqlType("timestamp not null default CURRENT_TIMESTAMP "))

  def updated_at = column[Timestamp]("UPDATED_AT", SqlType("timestamp not null default CURRENT_TIMESTAMP "))

  def pk = foreignKey("TOKENS_MACHINE", machineid, TableQuery[MachineTable])(_.id)

}
