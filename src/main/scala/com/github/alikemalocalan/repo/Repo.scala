package com.github.alikemalocalan.repo

import java.sql.Timestamp

import ch.qos.logback.classic.{Level, Logger}
import com.github.alikemalocalan.model._
import com.github.alikemalocalan.utils.DateUtils
import org.slf4j
import org.slf4j.LoggerFactory
import slick.driver.PostgresDriver.api._

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

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

  def insertLang(lang: String,code_at:String): Future[Option[Int]] = {
    val insertingLang = Language(name = lang.toLowerCase,
      updated_at = DateUtils.strToTimestamp(code_at),
      alias_of_id = 1
    )
    val req = (langs returning langs.map(_.id)) insertOrUpdate insertingLang
    db.run(req)
  }

  def saveXpResponse(token: String, xpResponse: XpResponse): Future[Seq[Int]] = {
    getMachineIdByToken(token).flatMap {
      case Some(machine) => insertPulse(machine.id.get, machine.userid, xpResponse.code_at).flatMap {
        case value: Int => insertXpByArray(value, xpResponse.code_at, xpResponse.xps)
        case _ => Future.failed(new Exception("Pulse not found"))
      }
      case None => Future.failed(new Exception("User not found"))
    }
  }

  def insertXpByArray(pulseId: Int, code_at: String, xps: Array[Xp]): Future[Seq[Int]] = {

    val req = xps.map { xp =>
      insertLang(xp.language, code_at).flatMap {
        case Some(langId) => insertXp(xp.xp, pulseId, langId)
        case None => Future.failed(new Exception("Xps cant write"))
      }
    }
    Future.sequence(req.toSeq)

  }

  def getMachineIdByToken(token: String): Future[Option[Machine]] = {
    val useridReq = machines.filter(_.api_salt === token)
      //.map(machine => (machine.userid,machine.id))
      .result
      .headOption
    db.run(useridReq)
  }

  def insertXp(amount: Int, pulseId: Int, langId: Int): Future[Int] = {
    val insertingXp = Xps(amount = amount,
      pulse_id = pulseId,
      language_id = langId,
      inserted_at = new Timestamp(System.currentTimeMillis()),
      updated_at = new Timestamp(System.currentTimeMillis()),
      original_language_id = langId
    )
    val req = (xps returning xps.map(_.id)) += insertingXp
    db.run(req)
  }

  def insertPulse(machineId: Int, userId: Int, code_at: String): Future[Int] = {
    val insertingPulse = Pulse(
      send_at = DateUtils.strToTimestamp(code_at),
      user_id = userId,
      inserted_at = DateUtils.strToTimestamp(code_at),
      updated_at = new Timestamp(System.currentTimeMillis()),
      machine_id = machineId,
      send_at_local = new Timestamp(System.currentTimeMillis()),
      tz_offset = 3)

    val req = (pulses returning pulses.map(_.id)) += insertingPulse
    db.run(req)
  }

  def create_db(): Future[Unit] = {
    val tables = DBIO.seq(users.schema.create, machines.schema.create, pulses.schema.create, langs.schema.create, xps.schema.create)
    db.run(tables)

  }
}
