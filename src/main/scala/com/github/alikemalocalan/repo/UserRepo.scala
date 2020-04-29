package com.github.alikemalocalan.repo

import java.sql.Timestamp

import com.github.alikemalocalan.model._
import com.typesafe.scalalogging.LazyLogging
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

class UserRepo(db: Database)
              (implicit dbExecutor: ExecutionContext)
  extends TableQuery(new UserTable(_))
    with LazyLogging {

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

  def getUserbyName: String => Future[User] = userName =>
    db.run(findByUsername(userName).result.head)

  private def findByUsername(username: String) = this.findBy(_.username).apply(username)
}
