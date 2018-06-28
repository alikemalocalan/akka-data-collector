package com.github.alikemalocalan.actor

import akka.actor.{Actor, ActorLogging}
import com.github.alikemalocalan.model.PulseRequest
import com.github.alikemalocalan.repo.Repo
import slick.jdbc.PostgresProfile.api.Database

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class PulseInsertActor(db: Database) extends Actor with ActorLogging {
  val repo = new Repo(db)

  override def receive: Receive = {
    case PulseRequest(token, puls) =>

      log.info(s"Pulse Actor: tokenkey: $token, lang: ${puls.language}, exp: ${puls.xp}")

      val result = repo.insertPulse(token, puls)
      val senderActor = sender()

      result.onComplete {
        case Failure(e) => senderActor ! akka.actor.Status.Failure(e)
        case Success(x) => senderActor ! x
      }

    case _ => akka.actor.Status.Failure(new Exception("Incorrect Pulse input"))
  }

}
