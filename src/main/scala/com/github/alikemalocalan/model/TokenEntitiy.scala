package com.github.alikemalocalan.model

import java.sql.Timestamp

import com.github.alikemalocalan.utils.DateUtils
import slick.jdbc.PostgresProfile.api._
import slick.sql.SqlProfile.ColumnOption.SqlType
import spray.json.{DefaultJsonProtocol, JsString, JsValue, RootJsonFormat}

case class Token(tokenkey: String,
                 machineid: Option[Int] = None,
                 created_date: Timestamp,
                 last_modified_date: Timestamp,
                 id: Option[Int] = None)

object TokenProtocol extends DefaultJsonProtocol {

  implicit object timestampFormat extends RootJsonFormat[Timestamp] {

    override def write(obj: Timestamp): JsValue = JsString(DateUtils.timestampToString(obj))

    override def read(json: JsValue): Timestamp = {
      DateUtils.strToTimestamp(json.convertTo[String])
    }
  }


  implicit val incomingJsonFormat: RootJsonFormat[Token] = jsonFormat5(Token)
}

// create the schema
//TableQuery[TokenTable].schema.create,
class TokenTable(tag: Tag) extends Table[Token](tag, "TOKEN") {
  def * = (tokenkey, machineid.?, created_date, last_modified_date, id.?) <> (Token.tupled, Token.unapply)

  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

  def tokenkey = column[String]("TOKENKEY")

  def machineid = column[Int]("MACHINEID")

  def created_date = column[Timestamp]("CREATED_DATE", SqlType("timestamp not null default CURRENT_TIMESTAMP "))

  def last_modified_date = column[Timestamp]("LAST_MODIFIED_DATE", SqlType("timestamp not null default CURRENT_TIMESTAMP "))

  def pk = foreignKey("TOKENS_MACHINE", machineid, TableQuery[MachineTable])(_.id)

}
