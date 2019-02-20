package com.github.alikemalocalan.repo

import java.sql.Timestamp

import ch.qos.logback.classic.{Level, Logger}
import com.github.alikemalocalan.model._
import org.slf4j.LoggerFactory
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

class UserRepo(db: Database) extends TableQuery(new UserTable(_)) {
  private val logger= LoggerFactory.getLogger(this.getClass.getSimpleName)
  logger.asInstanceOf[Logger].setLevel(Level.DEBUG)

  val findByUsername = this.findBy(_.email)

  def insertUser(user: User): Future[Int] = {
    logger.debug(s"User adding: ${user.username}")
    val query = this += User(
      username = user.username,
      email = user.email,
      password = user.password,
      inserted_at = Some(new Timestamp(System.currentTimeMillis())),
      updated_at = Some(new Timestamp(System.currentTimeMillis())),
      last_cached = Some(new Timestamp(System.currentTimeMillis())),
      private_profile = Some(false)
    )
    db.run(query).recoverWith {
      case ex: Exception => logger.error("User Insert ERROR", ex)
        Future.failed(ex)
    }
  }
}
