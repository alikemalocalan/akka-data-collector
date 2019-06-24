package com.github.alikemalocalan.controller

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, PathMatcher, Route}
import com.github.alikemalocalan.App.logger
import com.github.alikemalocalan.model.Machine
import com.github.alikemalocalan.repo.MachineRepo
import org.slf4j.{Logger, LoggerFactory}
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}


class MachineController(db: Database)(implicit dbExecutor: ExecutionContext, eh: ExceptionHandler) extends CorsSupport {


  private val log: Logger = LoggerFactory.getLogger(classOf[MachineController])
  private val basePath: PathMatcher[Unit] = "api" / "v1" / "machine"

  private val machineRepo= new MachineRepo(db)

  """
    |{
    |	"name":"is",
    |	"userid":11
    |}
  """.stripMargin
  private val insertMachine = path(basePath) {
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
  private val machineRoutes: Route = insertMachine

  val corsSupportedRoutes: Route = corsHandler(machineRoutes, basePath)
}
