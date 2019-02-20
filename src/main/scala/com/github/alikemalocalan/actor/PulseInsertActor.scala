package com.github.alikemalocalan.actor

import akka.actor.SupervisorStrategy.Restart
import akka.actor.{Actor, ActorLogging, OneForOneStrategy}
import com.github.alikemalocalan.model.InComingRequest
import com.github.alikemalocalan.repo.PulseRepo
import slick.jdbc.PostgresProfile.api.Database

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.{Failure, Success}


class PulseInsertActor(db: Database) extends Actor with ActorLogging {
  override val supervisorStrategy: OneForOneStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 5.minute) {
      case _: Exception => Restart
    }

  val repo = new PulseRepo(db)

  override def receive: Receive = {
    case event: InComingRequest =>
      val result = repo.insertPulse(event.token, event.xpResponse)
      val senderActor = sender()

      result.onComplete {
        case Success(x) => senderActor ! x
        case Failure(e) => senderActor ! akka.actor.Status.Failure(e)
      }

    case _ => akka.actor.Status.Failure(new Exception("Incorrect Pulse input"))
  }

  override def postRestart(reason: Throwable): Unit =
    log.info(s"Actor restarted due to ${reason.toString}")

}
