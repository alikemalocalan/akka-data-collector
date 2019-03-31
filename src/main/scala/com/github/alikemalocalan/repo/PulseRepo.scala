package com.github.alikemalocalan.repo

import akka.event.slf4j.Logger
import com.github.alikemalocalan.model.{Pulse, PulseTable, XpResponse}
import slick.jdbc.PostgresProfile.api._
import slick.lifted.TableQuery

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

class PulseRepo(db: Database) extends TableQuery(new PulseTable(_)) {
  private val logger= Logger(this.getClass.getSimpleName)

  val machineRepo = new MachineRepo(db)

  def insertPulse(token: String, xpResponse: XpResponse): Future[Unit] = {
    machineRepo.getMachineIdByToken(token).map{machine =>
      logger.info(s"alikemal:${machine.api_salt}")
      val insertingPulse = this ++= xpResponse.toPulse(machine.userid, machine.id.get).toSeq
      db.run(DBIO.seq(insertingPulse).transactionally)
        .recoverWith {
          case ex: Exception => logger.error("Pulse inserting ERROR", ex)
            Future.failed(ex)
        }
    }
  }

  def listPulsesByLang(token: String): Future[Map[String, Int]] = {
    import scalaz.Scalaz._
    machineRepo.getMachineIdByToken(token)
      .flatMap(machine => getPulseIdByToken(machine.id.get)).map(_.map(event=> Map(event.lang_name -> event.xp_amount)).suml)
  }

  private def getPulseIdByToken(machineID: Int): Future[List[Pulse]] = {
    val useridReq = this.filter(_.machine_id === machineID)
      .result
    db.run(useridReq).map(_.toList)
      .recoverWith {
        case ex: Exception => logger.error("Pulses listing ERROR", ex)
          Future.failed(ex)
    }
  }
}
