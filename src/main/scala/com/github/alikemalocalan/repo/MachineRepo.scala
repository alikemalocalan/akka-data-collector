package com.github.alikemalocalan.repo

import java.sql.Timestamp

import akka.event.slf4j.Logger
import com.github.alikemalocalan.model.{Machine, MachineTable}
import com.github.alikemalocalan.utils.RandomUtil
import slick.jdbc.PostgresProfile.api._
import slick.lifted.TableQuery

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

class MachineRepo(db: Database) extends TableQuery(new MachineTable(_)) {
  private val logger = Logger(this.getClass.getSimpleName)

  def insertMachine(machine: Machine): Future[Option[String]] = {
    logger.info(s"User adding: ${machine.name}")
    val newMachine = Machine(
      name = machine.name,
      userid = machine.userid,
      inserted_at = Some(new Timestamp(System.currentTimeMillis())),
      created_at = Some(new Timestamp(System.currentTimeMillis())),
      updated_at = Some(new Timestamp(System.currentTimeMillis())),
      api_salt = Some(RandomUtil.alphanumeric(32)),
      activated = Some(true)
    )
    db.run((this returning this.map(_.api_salt)) insertOrUpdate newMachine)
      .recoverWith {
      case ex: Exception => logger.error("Machine Insert ERROR", ex)
        Future.failed(ex)
    }
  }

  def getMachineIdByToken(token: String): Future[Machine] = {
    val useridReq = this.filter(_.api_salt === token)
      .result.headOption
    db.run(useridReq)
      .flatMap {
        case Some(x) => Future.successful(x)
        case None => Future.failed(new Exception("Machine not Found"))
      }
  }
}
