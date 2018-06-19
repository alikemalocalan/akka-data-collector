package com.github.alikemalocalan.model

import java.sql.Timestamp

import com.github.alikemalocalan.utils.DateUtils.TimestampFormat
import slick.jdbc.PostgresProfile.api._
import slick.sql.SqlProfile.ColumnOption.SqlType
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

case class User(
                 id: Option[Int] = None,
                 username: String,
                 email: String,
                 password: String,
                 inserted_at: Timestamp,
                 updated_at: Timestamp,
                 last_cached: Timestamp,
                 private_profile: Boolean,
                 cache: String
               )

object UserProtocol extends DefaultJsonProtocol {
  implicit val incomingJsonFormat: RootJsonFormat[User] = jsonFormat9(User)
}

// create the schema
//TableQuery[UserTable].schema.create,
class UserTable(tag: Tag) extends Table[User](tag, "USERS") {
  def * = (id.?, username, email, password, inserted_at, updated_at, last_cached, private_profile, cache) <> (User.tupled, User.unapply)

  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

  def username = column[String]("USERNAME")

  def password = column[String]("PASSWORD")

  def email = column[String]("EMAIL", O.Unique)

  def inserted_at = column[Timestamp]("INSERTED_AT", SqlType("timestamp not null default CURRENT_TIMESTAMP "))

  def updated_at = column[Timestamp]("UPDATED_AT", SqlType("timestamp not null default CURRENT_TIMESTAMP "))

  def last_cached = column[Timestamp]("LAST_CACHED")

  def private_profile = column[Boolean]("PRIVATE_PROFILE")

  def cache = column[String]("CACHE")

}
