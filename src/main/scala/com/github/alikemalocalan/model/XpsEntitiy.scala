package com.github.alikemalocalan.model

import java.sql.Timestamp

import com.github.alikemalocalan.utils.DateUtils
import slick.jdbc.PostgresProfile.api._
import slick.sql.SqlProfile.ColumnOption.SqlType
import spray.json.{DefaultJsonProtocol, JsString, JsValue, RootJsonFormat}

case class Xps(
                id: Option[Int] = None,
                amount: Int,
                pulse_id: Int,
                language_id: Int,
                inserted_at: Timestamp = new Timestamp(System.currentTimeMillis()),
                updated_at: Timestamp,
                original_language_id: Int
              )

object XpsProtocol extends DefaultJsonProtocol {

  implicit object timestampFormat extends RootJsonFormat[Timestamp] {

    override def write(obj: Timestamp): JsValue = JsString(DateUtils.timestampToString(obj))

    override def read(json: JsValue): Timestamp = {
      DateUtils.strToTimestamp(json.convertTo[String])
    }
  }


  implicit val incomingJsonFormat: RootJsonFormat[Xps] = jsonFormat7(Xps)
}

// create the schema
//TableQuery[TokenTable].schema.create,
class XpsTable(tag: Tag) extends Table[Xps](tag, "XPS") {
  def * = (id.?, amount, pulse_id, language_id, inserted_at, updated_at, original_language_id) <> (Xps.tupled, Xps.unapply)

  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

  def amount = column[Int]("AMOUNT")

  def language_id = column[Int]("LANGUAGE_ID")

  def inserted_at = column[Timestamp]("INSERTED_AT", SqlType("timestamp not null default CURRENT_TIMESTAMP "))

  def updated_at = column[Timestamp]("UPDATED_AT", SqlType("timestamp not null default CURRENT_TIMESTAMP "))

  def original_language_id = column[Int]("ORIGINAL_LANGUAGE_ID")

  def pulse_id = column[Int]("PULSE_ID")

  def pk_lang = foreignKey("xps_language_id_fkey", language_id, TableQuery[LanguageTable])(_.id)

  def pk_org_lang = foreignKey("xps_original_language_id_fkey", original_language_id, TableQuery[LanguageTable])(_.id)

  def pk_pulse = foreignKey("xps_pulse_id_fkey", pulse_id, TableQuery[PulseTable])(_.id)
}
