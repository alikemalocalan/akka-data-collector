package com.github.alikemalocalan.repo

import ch.qos.logback.classic.{Level, Logger}
import com.github.alikemalocalan.model.{PulseTable,XpResponse}
import org.slf4j.LoggerFactory
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

class PulseRepo(db: Database) {
  private val logger= LoggerFactory.getLogger(this.getClass.getSimpleName)
  logger.asInstanceOf[Logger].setLevel(Level.DEBUG)

  val machineRepo = new MachineRepo(db)
  val pulses = TableQuery[PulseTable]


  def insertPulse(token: String, xpResponse: XpResponse): Future[Unit] = {
    machineRepo.getMachineIdByToken(token).flatMap {
      case Some(machine) =>
        val insertingPulse = pulses ++= xpResponse.toPulse(machine.userid, machine.id.get).toSeq
        db.run(DBIO.seq(insertingPulse))
      case None => Future.failed(new Exception("Machine not found"))
    }
  }
}
