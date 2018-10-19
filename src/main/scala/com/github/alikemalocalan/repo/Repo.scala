package com.github.alikemalocalan.repo

import ch.qos.logback.classic.{Level, Logger}
import com.github.alikemalocalan.model._
import com.github.alikemalocalan.utils.DateUtils
import org.slf4j
import org.slf4j.LoggerFactory
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

class Repo(db: Database) {
  val users = TableQuery[UserTable]
  val machines = TableQuery[MachineTable]
  val xps = TableQuery[XpsTable]
  val pulses = TableQuery[PulseTable]
  val langs = TableQuery[LanguageTable]

  val logger: slf4j.Logger = LoggerFactory.getLogger(this.getClass.getSimpleName)
  logger.asInstanceOf[Logger].setLevel(Level.DEBUG)

  def insertUser(user: User): Future[Int] = {
    logger.debug(s"User adding: ${user.id}")
    db.run(users += user)
  }

  def isDefinedLang(lang: String): Future[Option[Int]] = {
    val isDefined = langs.filter(_.name === lang).map(_.id).result.headOption
    db.run(isDefined)
  }

  def insertLang(lang: String): Future[Int] = {
    val insertingLang = Language(name = lang,
      inserted_at = new java.sql.Timestamp(System.currentTimeMillis()),
      updated_at = new java.sql.Timestamp(System.currentTimeMillis()),
      alias_of_id = 1
    )
    val req = (langs returning langs.map(_.id)) += insertingLang
    db.run(req)
  }

  def insertXpByArray(pulseId: Int, xps: Array[Xp]): Unit = {
    val result = xps.map { xp =>
      isDefinedLang(xp.language).map {
        case Some(langId) => insertXp(xp.xp, pulseId, langId)
        case None => insertLang(xp.language).map(insertXp(xp.xp, pulseId, _))
      }
    }


  def saveXpResponse(token: String, xpResponse: XpResponse): Future[Int] = {
    getMachineIdByToken(token).flatMap {
      case Some(machine) => insertPulse(machine.id.get, machine.userid, xpResponse.code_at)
      case None => Future.failed(new Exception("User not found"))
    }
  }

  def getMachineIdByToken(token: String): Future[Option[Machine]] = {
    val useridReq = machines.filter(_.api_salt === token)
      //.map(machine => (machine.userid,machine.id))
      .result
      .headOption
    db.run(useridReq)
  }

  def insertPulse(machineId: Int, userId: Int, code_at: String): Future[Int] = {
    val insertingPulse = Pulse(
      send_at = Try(DateUtils.strToTimestamp(code_at)).toOption,
      user_id = userId,
      inserted_at = DateUtils.strToTimestamp(code_at),
      updated_at = new java.sql.Timestamp(System.currentTimeMillis()),
      machine_id = machineId,
      send_at_local = new java.sql.Timestamp(System.currentTimeMillis()),
      tz_offset = 3)

    val req = (pulses returning pulses.map(_.id)) += insertingPulse
    db.run(req)
  }

}

  def insertXp(amount: Int, pulseId: Int, langId: Int): Future[Int] = {
    val insertingXp = Xps(amount = amount,
      pulse_id = pulseId,
      language_id = langId,
      inserted_at = new java.sql.Timestamp(System.currentTimeMillis()),
      updated_at = new java.sql.Timestamp(System.currentTimeMillis()),
      original_language_id = langId
    )
    val req = (xps returning xps.map(_.id)) += insertingXp
    db.run(req)
  }
