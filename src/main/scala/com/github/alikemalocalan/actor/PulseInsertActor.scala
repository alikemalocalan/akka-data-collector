package com.github.alikemalocalan.actor

import akka.actor.{Actor, ActorLogging}
import com.github.alikemalocalan.model.PulseRequest
import com.github.alikemalocalan.repo.Repo
import slick.jdbc.PostgresProfile.api.Database

import scala.concurrent.Future

class PulseInsertActor(db: Database) extends Actor with ActorLogging {
  val repo = new Repo(db)

  override def receive: Receive = {
    case PulseRequest(token, puls) =>
      log.info(s"Pulse request: name: $token, id: ${puls.language}")
      sender() ! repo.insertPulse(token, puls)

    case _ => Future.failed(new Exception("Incorrect Pulse input"))
  }

}
