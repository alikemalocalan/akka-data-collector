package com.github.alikemalocalan.model

import java.sql.Timestamp

import com.github.alikemalocalan.utils.DateUtils
import slick.jdbc.PostgresProfile.api._
import slick.sql.SqlProfile.ColumnOption.SqlType
import spray.json.{DefaultJsonProtocol, JsString, JsValue, RootJsonFormat}

case class Language(
                     id: Option[Int] = None,
                     name: String,
                     inserted_at: Timestamp,
                     updated_at: Timestamp,
                     alias_of_id: Int
                   )

object LanguageProtocol extends DefaultJsonProtocol {

  implicit object timestampFormat extends RootJsonFormat[Timestamp] {

    override def write(obj: Timestamp): JsValue = JsString(DateUtils.timestampToString(obj))

    override def read(json: JsValue): Timestamp = {
      DateUtils.strToTimestamp(json.convertTo[String])
    }
  }


  implicit val incomingJsonFormat: RootJsonFormat[Language] = jsonFormat5(Language)
}

// create the schema
//TableQuery[TokenTable].schema.create,
class LanguageTable(tag: Tag) extends Table[Language](tag, "LANGUAGES") {
  def * = (id.?, name, inserted_at, updated_at, alias_of_id) <> (Language.tupled, Language.unapply)

  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

  def name = column[String]("NAME", O.Unique)

  def inserted_at = column[Timestamp]("INSERTED_AT", SqlType("timestamp not null default CURRENT_TIMESTAMP "))

  def updated_at = column[Timestamp]("UPDATED_AT", SqlType("timestamp not null default CURRENT_TIMESTAMP "))

  def alias_of_id = column[Int]("ALIAS_OF_ID")

  def pk = foreignKey("XPS_PULSE", id, TableQuery[LanguageTable])(_.alias_of_id)

}
