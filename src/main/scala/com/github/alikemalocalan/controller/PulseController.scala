package com.github.alikemalocalan.controller

import akka.actor.ActorRef
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{entity, _}
import akka.http.scaladsl.server.{ExceptionHandler, PathMatcher, Route}
import akka.pattern.ask
import akka.util.Timeout
import com.github.alikemalocalan.App.logger
import com.github.alikemalocalan.model.{InComingRequest, XpResponse}
import com.github.alikemalocalan.repo.PulseRepo
import org.slf4j.{Logger, LoggerFactory}
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.{Failure, Success}


class PulseController(db:Database, pulseActor: ActorRef)(implicit dbExecutor: ExecutionContext, eh: ExceptionHandler) extends CorsSupport {


  private val log: Logger = LoggerFactory.getLogger(classOf[PulseController])
  private val basePath: PathMatcher[Unit] = "api" / "v1" / "pulse"
  implicit val timeout: Timeout = Timeout(25 seconds)
  private val pulseRepo= new PulseRepo(db)

  """
    |{
    |	"code_at":"2019-03-25T12:13:20+03:00",
    |	"xps":[{"language":"Java","xp":15}]
    |}
  """.stripMargin
  private val insertPulseRoutes = path(basePath) {
    import com.github.alikemalocalan.model.XpResponseProtocol._
    pathEndOrSingleSlash {
      post {
        headerValueByName("X-API-Token") { authHeader =>
          if (authHeader.nonEmpty) {
            entity(as[XpResponse]) { entity =>
              //val token = authHeader.substring(6)

              logger.info(s"Request Pulse token : $authHeader")

              onComplete(pulseActor ? InComingRequest(authHeader, entity)) {
                case Success(_) => complete(StatusCodes.Created)
                case Failure(e) => complete(StatusCodes.InternalServerError, e.getLocalizedMessage)
              }
            }
          } else {
            complete(StatusCodes.Unauthorized)
          }
        }
      }
    }
  }
  private val listPulseRoute = path(basePath) {
    pathEndOrSingleSlash {
      get {
        headerValueByName("X-API-Token") { authHeader =>
          if (authHeader.nonEmpty) {
            val token = authHeader.substring(6)

            logger.info(s"List Request Pulse:  , token : $token")
            import com.github.alikemalocalan.model.PulseProtocol._

            onComplete(pulseRepo.listPulsesByLang(token)) {
              case Success(x) => complete(x)
              case Failure(e) => complete(StatusCodes.InternalServerError, e.getLocalizedMessage)
            }
          } else {
            complete(StatusCodes.Unauthorized)
          }
        }
      }
    }
  }
  private val pulseRoutes: Route = insertPulseRoutes ~ listPulseRoute

  val corsSupportedRoutes: Route = corsHandler(pulseRoutes, basePath)
}
