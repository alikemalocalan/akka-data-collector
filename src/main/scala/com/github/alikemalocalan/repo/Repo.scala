package com.github.alikemalocalan.repo

import akka.event.slf4j.Logger
import com.github.alikemalocalan.model._
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future
import scala.util.Success
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

    db.run(tokens.filter(_.tokenkey === tokenKey).map(_.machineid).result.head).value match {
      case Some(Success(x)) => db.run(updatePulseQuery(x))
      case _ => Future.failed(new Exception("User not found"))
    }
  }
}
