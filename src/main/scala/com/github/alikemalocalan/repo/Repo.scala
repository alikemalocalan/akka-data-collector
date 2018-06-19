package com.github.alikemalocalan.repo

import akka.event.slf4j.Logger
import com.github.alikemalocalan.model._
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
//val userid = TableQuery[TokenTable].filter(_.tokenkey === tokenKey).map(_.userid)
//val pulse = TableQuery[PulseTable] join userid on (_.userid === _.userid)


class Repo(db: Database) {
  val users = TableQuery[UserTable]

  def insertUser(user: User): Future[Int] = {
    logger.debug(s"User adding: ${user.name}")
    db.run(users += user)
  }

  def insertPulse(tokenKey: String, pulse: Pulse): Future[Int] = {
    //Await.result(db.run(TableQuery[PulseTable].schema.create),3 seconds)

    logger.debug(s"User adding: ${pulse.toString}")
    val tokens = TableQuery[TokenTable]
    val pulses = TableQuery[PulseTable]

    val userIdQuery = tokens.filter(_.tokenkey === "asd").map(_.userid).result.headOption

    def updatePulseQuery(userId: Int) = pulses += pulse.updateUserID(userId)

    for {
      userIdOpt <- db.run(userIdQuery)
      res <- userIdOpt.map(userId => db.run(updatePulseQuery(userId))).getOrElse(Future.failed(new Exception("User not found")))
    } yield res

  }

  def logger = Logger.apply(Repo.this.getClass.getSimpleName)

}
