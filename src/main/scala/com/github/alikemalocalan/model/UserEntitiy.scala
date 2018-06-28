package com.github.alikemalocalan.model

import java.sql.Timestamp

import com.github.alikemalocalan.utils.DateUtils
import slick.jdbc.PostgresProfile.api._
import slick.sql.SqlProfile.ColumnOption.{Nullable, SqlType}
import spray.json.{DefaultJsonProtocol, JsString, JsValue, RootJsonFormat}

case class User(login: String,
                firstname: String,
                lastname: String,
                password_hash: String,
                email: String,
                image_url: String,
                lang_key: String,
                activated: Boolean,
                activation_key: String,
                created_by: String,
                reset_key: String,
                created_date: Timestamp,
                reset_date: Timestamp,
                last_modified_date: Timestamp,
                last_modified_by: String,
                id: Option[Int] = None)

object UserProtocol extends DefaultJsonProtocol {

  implicit object timestampFormat extends RootJsonFormat[Timestamp] {

    override def write(obj: Timestamp): JsValue = JsString(DateUtils.timestampToString(obj))

    override def read(json: JsValue): Timestamp = {
      DateUtils.strToTimestamp(json.convertTo[String])
    }
  }

  implicit val incomingJsonFormat: RootJsonFormat[User] = jsonFormat16(User)
}

// create the schema
//TableQuery[UserTable].schema.create,
class UserTable(tag: Tag) extends Table[User](tag, "USER") {
  def * = (login,
    firstname,
    lastname,
    password_hash,
    email,
    image_url,
    lang_key,
    activated,
    activation_key,
    created_by,
    reset_key,
    created_date,
    reset_date,
    last_modified_date,
    last_modified_by,
    id.?) <> (User.tupled, User.unapply)

  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

  def login = column[String]("LOGIN")

  def firstname = column[String]("FIRSTNAME", Nullable)

  def lastname = column[String]("LASTNAME", Nullable)

  def password_hash = column[String]("PASSWORD_HASH", Nullable)

  def email = column[String]("EMAIL", Nullable)

  def image_url = column[String]("IMAGE_URL", Nullable)

  def lang_key = column[String]("LANG_KEY", Nullable)

  def activated = column[Boolean]("ACTIVATED")

  def activation_key = column[String]("ACTIVATION_KEY", Nullable)

  def created_by = column[String]("CREATED_BY")

  def reset_key = column[String]("RESET_KEY", Nullable)

  def reset_date = column[Timestamp]("RESET_DATE", SqlType("timestamp not null default CURRENT_TIMESTAMP "), Nullable)

  def created_date = column[Timestamp]("CREATED_DATE", SqlType("timestamp not null default CURRENT_TIMESTAMP "))

  def last_modified_date = column[Timestamp]("LAST_MODIFIED_DATE", SqlType("timestamp not null default CURRENT_TIMESTAMP "), Nullable)

  def last_modified_by = column[String]("LAST_MODIFIED_BY", Nullable)

}
