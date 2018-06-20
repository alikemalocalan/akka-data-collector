package com.github.alikemalocalan.repo

import akka.event.slf4j.Logger
import com.github.alikemalocalan.model._
import slick.dbio.DBIOAction
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
//val userid = TableQuery[TokenTable].filter(_.tokenkey === tokenKey).map(_.userid)
//val pulse = TableQuery[PulseTable] join userid on (_.userid === _.userid)


class Repo(db: Database) {
  val users = TableQuery[UserTable]
  val logger = Logger.apply(Repo.this.getClass.getSimpleName)

  def insertUser(user: User): Future[Int] = {
    logger.debug(s"User adding: ${user.name}")
    db.run(users += user)
  }

  //maybe return Future[Int] but waıt timeout
  def insertPulse(tokenKey: String, pulse: Pulse): Future[Int] = {
    //Await.result(db.run(TableQuery[PulseTable].schema.create),3 seconds)

    logger.debug(s"Pulse adding: ${pulse.toString}")
    val tokens = TableQuery[TokenTable]
    val pulses = TableQuery[PulseTable]


    def updatePulseQuery(userId: Int) = pulses += pulse.updateUserID(userId)

    val userIdQuery = tokens.filter(_.tokenkey === tokenKey).map(_.userid).result
    val action: DBIO[Int] = for {
      existingUser <- userIdQuery.headOption
      rowsAffected <- existingUser match {
        case Some(n) => updatePulseQuery(n)
        case None => DBIOAction.failed(new Exception("User not found"))
      }
    } yield rowsAffected

    db.run(action)
  }
}
