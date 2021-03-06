package com.github.alikemalocalan

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.{ContentTypes, StatusCodes}
import akka.http.scaladsl.server.Directives.{entity, _}
import akka.pattern.ask
import akka.routing.RoundRobinPool
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.github.alikemalocalan.actor.MasterActor
import com.github.alikemalocalan.logging.LoggingAdapter
import com.github.alikemalocalan.model._
import com.github.alikemalocalan.repo.{MachineRepo, PulseRepo, UserRepo}
import com.github.alikemalocalan.service.LoginRequestProtocol._
import com.github.alikemalocalan.service.{JwtAuth, LoginRequest}
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object App extends Config {

  val db: Database = Database.forConfig("slick-postgres")
  val machineRepo = new MachineRepo(db)
  val userRepo = new UserRepo(db)
  val pulseRepo = new PulseRepo(db)

  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val ec: ExecutionContextExecutor = actorSystem.dispatcher
  implicit val timeout: Timeout = Timeout(25 seconds)

  val logger = actorSystem.log
  val masterProps: Props = Props(new MasterActor(db, workerCount))
    .withRouter(new RoundRobinPool(masterCount))
  val pulseActor: ActorRef = actorSystem.actorOf(masterProps, "pulseinsert-actor")

  def main(args: Array[String]): Unit = {

    """
      |{
      |	"username":"alikemal",
      |	"email":"ali@alikemal.org",
      |	"password":"123456"
      |}
    """.stripMargin
    val userRoutes = path("user") {
      import com.github.alikemalocalan.model.UserProtocol._
      post {
        entity(as[User]) { user =>
          logger.info(s"User request: name: ${user.username}, id: ${user.id}")
          onComplete(userRepo.insertUser(user)) {
            case Success(_) => complete(StatusCodes.Created)
            case Failure(e) => complete(StatusCodes.InternalServerError, e.getStackTrace.toString)
          }
        }
      }
    }

    """
      |{
      |	"name":"is",
      |	"userid":11
      |}
    """.stripMargin
    val machineRoute = path("machine") {
      import com.github.alikemalocalan.model.MachineProtocol._
      post {
        entity(as[Machine]) { machine =>
          logger.info(s"Machine request: name: ${machine.name}, userid: ${machine.userid}")
          onComplete(machineRepo.insertMachine(machine)) {
            case Success(api_salt) => complete(StatusCodes.Created, api_salt)
            case Failure(e) => complete(StatusCodes.InternalServerError, e.getLocalizedMessage)
          }
        }
      }
    }

    """
      |{
      |	"code_at":"2019-03-25T12:13:20+03:00",
      |	"xps":[{"language":"Java","xp":15}]
      |}
    """.stripMargin
    val insertPulseRoutes = path("pulse") {
      import com.github.alikemalocalan.model.XpResponseProtocol._
      pathEndOrSingleSlash {
        post {
          headerValueByName("X-API-Token") { authHeader =>
            if (authHeader.nonEmpty) {
              entity(as[XpResponse]) { entity =>
                val token = authHeader.substring(6)

                logger.info(s"Request Pulse token : $token")

                onComplete(pulseActor ? InComingRequest(token, entity)) {
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

    val listPulseRoute = path("pulse") {
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

    val healthRoute = path("health") {
      pathEndOrSingleSlash {
        get {
          logger.info("Server OK")
          complete(StatusCodes.OK)
        }
      }
    }

    val login = post {
      entity(as[LoginRequest]) {
        case lr @ LoginRequest("admin", "admin") =>
          val token = JwtAuth.tryLogin(lr)
          respondWithHeader(token.toHeader){
            import com.github.alikemalocalan.service.TokenProtocol._
            complete(StatusCodes.OK,token)
          }
        case LoginRequest(_, _) => complete(StatusCodes.Unauthorized)
      }
    }

    val refleshToken = path("refleshtoken") {
      pathEndOrSingleSlash {
        get {
          headerValueByName("Access-Token") { tokenHeader =>
            if (tokenHeader.nonEmpty) {
              if (JwtAuth.isValidToken(tokenHeader) && JwtAuth.isTokenExpired(tokenHeader))
                complete(JwtAuth.getClaim(tokenHeader).toJson)
              else complete(StatusCodes.Unauthorized)
            } else {
              complete(StatusCodes.Unauthorized)
            }
          }
        }
      }
    }

    val routes = mapResponseEntity(_.withContentType(ContentTypes.`application/json`)) {
      userRoutes ~ healthRoute ~ insertPulseRoutes ~ machineRoute ~ listPulseRoute ~ login ~ refleshToken
    }

    Http().bindAndHandle(LoggingAdapter.clientRouteLogged(routes),interface = address,port = port).onComplete {
      case Success(b) => logger.info(s"application is up and running at ${b.localAddress.getHostName}:${b.localAddress.getPort}")
      case Failure(e) => logger.error(s"could not start application: {}", e.getMessage)
    }
  }

}
