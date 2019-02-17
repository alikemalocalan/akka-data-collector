package com.github.alikemalocalan

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.routing.RoundRobinPool
import akka.stream.ActorMaterializer
import akka.util.Timeout
import ch.qos.logback.classic.{Level, Logger}
import com.github.alikemalocalan.actor.MasterActor
import com.github.alikemalocalan.model._
import com.github.alikemalocalan.repo.{MachineRepo, UserRepo}
import org.slf4j.LoggerFactory
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object App extends Config {
    private val logger = LoggerFactory.getLogger(this.getClass.getSimpleName)

  val db: Database = Database.forConfig("slick-postgres")
  val machineRepo = new MachineRepo(db)
  val userRepo = new UserRepo(db)

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  implicit val timeout: Timeout = Timeout(25 seconds)

  val masterProps: Props = Props(new MasterActor(db, workerCount))
    .withRouter(new RoundRobinPool(masterCount))
  val pulseActor: ActorRef = system.actorOf(masterProps, "pulseinsert-actor")

  def main(args: Array[String]): Unit = {
    logger.asInstanceOf[Logger].setLevel(Level.DEBUG)

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
      |	"code_at":"2019-02-17 23:00:03",
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
                val token = authHeader.substring(6)

                logger.debug(s"Request Pulse: ${entity.code_at} , token : $token")

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

    //DebuggingDirectives.logRequest("get-user")
    Http().bindAndHandle(routes,interface = "0.0.0.0").map { r =>
      logger.info("Server started on port 9000")
    }
  }

}
