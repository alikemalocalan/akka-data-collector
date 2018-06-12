package com.github.alikemalocalan

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.github.alikemalocalan.actor.UserInsertActor
import com.github.alikemalocalan.model.User
import org.slf4j.{Logger, LoggerFactory}
import slick.jdbc.PostgresProfile.api.Database

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object App {

  val db: Database = Database.forConfig("slick-postgres")

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  implicit val timeout: Timeout = Timeout(5 seconds)
  val masterActor: ActorRef = system.actorOf(Props(new UserInsertActor(db)), "userinsert-actor")

  def main(args: Array[String]): Unit = {
    import com.github.alikemalocalan.model.UserProtocol._

    val userRoutes = path("users") {
      post {
        entity(as[User]) { entity =>
          onComplete(masterActor ? entity) {
            case Success(_) => complete(StatusCodes.Created)
            case Failure(_) => complete(StatusCodes.InternalServerError)
          }
        }
      }
    }

    val healthRoute = path("health") {
      get {
        logger.debug(s"Server OK")
        complete(StatusCodes.OK)
      }
    }

    val routes = userRoutes ~ healthRoute


    Http().bindAndHandle(routes, "localhost", 9000).map(_ =>
      logger.info(s"Server started on port 9000"))
  }

  def logger: Logger = LoggerFactory.getLogger(App.this.getClass.getSimpleName)

}
