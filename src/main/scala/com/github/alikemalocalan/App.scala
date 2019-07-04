package com.github.alikemalocalan

import java.util.Locale
import java.util.concurrent.Executors

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.routing.RoundRobinPool
import akka.stream.ActorMaterializer
import com.github.alikemalocalan.actor.MasterActor
import com.github.alikemalocalan.controller.ErrorHandler._
import com.github.alikemalocalan.controller.{MachineController, PulseController, UserController}
import com.github.alikemalocalan.logging.LoggingAdapter
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}
import scala.util.{Failure, Success}

object App extends Config {

  Locale.setDefault(Locale.US)
  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val ec: ExecutionContextExecutor = actorSystem.dispatcher


  val logger = Logging(actorSystem, "game-experience-collector")

  val dbExecutor: ExecutionContextExecutor = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(5))
  val db: Database = Database.forConfig("slick-postgres")

  val masterProps: Props = Props(new MasterActor(db, workerCount))
    .withRouter(new RoundRobinPool(masterCount))
  val pulseActor: ActorRef = actorSystem.actorOf(masterProps, "pulseinsert-actor")

  val userController = new UserController(db)
  val machineController = new MachineController(db)
  val pulseController = new PulseController(db, pulseActor)

  def main(args: Array[String]): Unit = {

    val healthRoute = path("health") {
      pathEndOrSingleSlash {
        get {
          logger.info("Server OK")
          complete(StatusCodes.OK)
        }
      }
    }

    val routes = mapResponseEntity(_.withContentType(ContentTypes.`application/json`)) {
      healthRoute ~ pulseController.corsSupportedRoutes ~ machineController.corsSupportedRoutes ~ userController.corsSupportedRoutes
    }

    Http().bindAndHandle(LoggingAdapter.clientRouteLogged(routes),interface = address,port = port).onComplete {
      case Success(b) => logger.info(s"application is up and running at ${b.localAddress.getHostName}:${b.localAddress.getPort}")
      case Failure(e) => logger.error(s"could not start application: {}", e.getMessage)
    }
  }

}
