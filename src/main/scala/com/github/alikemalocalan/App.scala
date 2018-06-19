package com.github.alikemalocalan

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.event.slf4j.Logger
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.github.alikemalocalan.actor.{PulseInsertActor, UserInsertActor}
import com.github.alikemalocalan.model.{Pulse, PulseRequest, User}
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object App {

  val db: Database = Database.forConfig("slick-postgres")

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  implicit val timeout: Timeout = Timeout(25 seconds)
  val userActor: ActorRef = system.actorOf(Props(new UserInsertActor(db)), "userinsert-actor")
  val pulseActor: ActorRef = system.actorOf(Props(new PulseInsertActor(db)), "pulseinsert-actor")

  println(system.settings.Loggers.toString())
  def main(args: Array[String]): Unit = {

    def logger = Logger.apply(App.this.getClass.getSimpleName)


    val userRoutes = path("users") {
      import com.github.alikemalocalan.model.UserProtocol._
      post {
        entity(as[User]) { entity =>
          logger.debug(s"Request Pulse: ${entity.toString}")
          onComplete(userActor ? entity) {
            case Success(_) => complete(StatusCodes.Created)
            case Failure(_) => complete(StatusCodes.InternalServerError)
          }
        }
      }
    }

    val pulseRoutes = path("pulse") {
      import com.github.alikemalocalan.model.PulseProtocol._
      post {
        headerValueByName("Authorization") { authHeader =>
          if (authHeader.substring(6).nonEmpty && authHeader.startsWith("Basic ")) {
            entity(as[Pulse]) { entity =>
              logger.debug(s"Request Pulse: ${entity.toString}")
              onComplete(pulseActor ? PulseRequest(authHeader.substring(6), entity)) {
                case Success(_) => complete(StatusCodes.Created)
                case Failure(e) => complete(StatusCodes.InternalServerError, e.toString)
              }
            }
          } else {
            complete(StatusCodes.Unauthorized)
          }

        }
      }
    }

    val healthRoute = path("health") {
      get {
        logger.debug("Server OK")
        complete(StatusCodes.OK)
      }
    }

    val routes = userRoutes ~ healthRoute ~ pulseRoutes


    Http().bindAndHandle(routes, "0.0.0.0", 9000).map { r =>
      logger.info("Server started on port 9000")
    }
  }

}
