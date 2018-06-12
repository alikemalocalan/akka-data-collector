package com.github.alikemalocalan.repo

import com.github.alikemalocalan.model.{User, UserTable}
import org.slf4j.{Logger, LoggerFactory}
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

class UserRepo(db: Database) {
  val users = TableQuery[UserTable]

  def logger: Logger = LoggerFactory.getLogger(UserRepo.this.getClass.getSimpleName)

  def insertUser(user: User): Future[Int] = {
    db.run(users += user)
  }

}
