package com.github.alikemalocalan

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.directives.DebuggingDirectives
import akka.pattern.ask
import akka.routing.RoundRobinPool
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.github.alikemalocalan.actor.MasterActor
import com.github.alikemalocalan.model._
import com.github.alikemalocalan.repo.{MachineRepo, UserRepo}
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object App extends Config with DebuggingDirectives {

  val db: Database = Database.forConfig("slick-postgres")
  val machineRepo = new MachineRepo(db)
  val userRepo = new UserRepo(db)

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
          logger.debug(s"User request: name: ${user.username}, id: ${user.id}")
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
          logger.debug(s"Machine request: name: ${machine.name}, userid: ${machine.userid}")
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
    val pulseRoutes = path("pulse") {
      import com.github.alikemalocalan.model.XpResponseProtocol._
      pathEndOrSingleSlash {
        post {
          headerValueByName("X-API-Token") { authHeader =>
            if (authHeader.nonEmpty) {
              entity(as[XpResponse]) { entity =>
                val token = authHeader.substring(0)

                logger.debug(s"Request Pulse: ${entity.coded_at} , token : $token")

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

    val healthRoute = path("health") {
      pathEndOrSingleSlash {
        get {
          logger.debug("Server OK")
          complete(StatusCodes.OK)
        }
      }
    }


    val routes = userRoutes ~ healthRoute ~ pulseRoutes ~ machineRoute

    Http().bindAndHandle(handler = logRequestResult("logger")(routes),interface = address,port = port).onComplete {
      case Success(b) => logger.info(s"application is up and running at ${b.localAddress.getHostName}:${b.localAddress.getPort}")
      case Failure(e) => logger.error(s"could not start application: {}", e.getMessage)
    }
  }

}
