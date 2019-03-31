package com.github.alikemalocalan.repo

import java.sql.Timestamp

import akka.event.slf4j.Logger
import com.github.alikemalocalan.model._
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

class UserRepo(db: Database) extends TableQuery(new UserTable(_)) {
  private val logger= Logger(this.getClass.getSimpleName)

  val findByUsername = this.findBy(_.email)

  def insertUser(user: User): Future[Int] = {
    logger.info(s"User adding: ${user.username}")
    val query = this += User(
      username = user.username,
      email = user.email,
      password = user.password,
      inserted_at = Some(new Timestamp(System.currentTimeMillis())),
      updated_at = Some(new Timestamp(System.currentTimeMillis())),
      last_cached = Some(new Timestamp(System.currentTimeMillis())),
      private_profile = Some(false)
    )
    db.run(query.transactionally).recoverWith {
      case ex: Exception => logger.error("User Insert ERROR", ex)
        Future.failed(ex)
    }
  }
}
