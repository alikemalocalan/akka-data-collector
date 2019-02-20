package com.github.alikemalocalan.actor

;

import akka.actor.SupervisorStrategy.Stop
import akka.actor.{Actor, ActorLogging, OneForOneStrategy, Props, Terminated}
import akka.routing.{ActorRefRoutee, RoundRobinRoutingLogic, Router}
import com.github.alikemalocalan.model.InComingRequest
import slick.jdbc.PostgresProfile.api.Database

import scala.concurrent.duration._

class MasterActor(db: Database, workerCount: Int) extends Actor with ActorLogging {

  override val supervisorStrategy: OneForOneStrategy =
    OneForOneStrategy(maxNrOfRetries = 1, withinTimeRange = 5 seconds) {
      case _: Exception => Stop
    }

  var workerActorRouter: Router = {
    val routees = Vector.fill(workerCount) {
      val r = context.actorOf(Props(new PulseInsertActor(db)))
      context.watch(r)
      ActorRefRoutee(r)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }

  override def receive: Receive = {

    case pulse: InComingRequest =>
      log.debug(s"Master Actor: tokenkey: ${pulse.token}, lang: ${pulse.xpResponse.code_at}, exp: ${pulse.xpResponse.xps.toString}")
      workerActorRouter.route(pulse, sender())

    case Terminated(s) =>
      log.error(s"${s.toString()} is terminated and will be killed.")
      workerActorRouter = workerActorRouter.removeRoutee(s)
      val r = context.actorOf(Props(new PulseInsertActor(db)))
      context.watch(r)
      workerActorRouter = workerActorRouter.addRoutee(r)

    case _ => log.error("Not known type as incoming message")
  }

  override def postRestart(reason: Throwable): Unit =
    log.info(s"Actor restarted due to ${reason.toString}")

}