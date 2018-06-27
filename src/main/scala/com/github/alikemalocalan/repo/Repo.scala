package com.github.alikemalocalan.repo

import ch.qos.logback.classic.{Level, Logger}
import com.github.alikemalocalan.model._
import org.slf4j.LoggerFactory
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

//val userid = TableQuery[TokenTable].filter(_.tokenkey === tokenKey).map(_.userid)
//val pulse = TableQuery[PulseTable] join userid on (_.userid === _.userid)


class Repo(db: Database) {
  val users = TableQuery[UserTable]


  val logger = LoggerFactory.getLogger(this.getClass.getSimpleName)
  logger.asInstanceOf[Logger].setLevel(Level.DEBUG)

  def insertUser(user: User): Future[Int] = {
    logger.debug(s"User adding: ${user.id}")
    db.run(users += user)
  }

  //maybe return Future[Int] but wait timeout
  def insertPulse(tokenKey: String, pulse: Pulse) = {

    val tokens = TableQuery[TokenTable]
    val pulses = TableQuery[PulseTable]


    def updatePulseQuery(userId: Int) = pulses.+=(pulse.updateMachineID(userId)).asTry.map {
      case Failure(e) => Failure(e)
      case Success(_) => Future.successful("Success pulse inserting")
    }

    db.run(tokens.filter(_.tokenkey === tokenKey).map(_.machineid).result.headOption).flatMap {
      case Some(x) => db.run(updatePulseQuery(x))
      case None => Future.failed(new Exception("User not found"))
    }
  }
}
