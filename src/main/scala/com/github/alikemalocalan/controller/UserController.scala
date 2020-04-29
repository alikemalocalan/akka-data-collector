package com.github.alikemalocalan.controller

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, PathMatcher, Route}
import com.github.alikemalocalan.model.User
import com.github.alikemalocalan.repo.UserRepo
import com.github.alikemalocalan.service.LoginRequestProtocol._
import com.github.alikemalocalan.service.{BasicAuth, JwtAuth, LoginRequest}
import com.typesafe.scalalogging.LazyLogging
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}


class UserController(db: Database)(implicit dbExecutor: ExecutionContext, eh: ExceptionHandler)
  extends CorsSupport
    with LazyLogging {

  private val basePath: PathMatcher[Unit] = "api" / "v1" / "user"
  private val userRepo = new UserRepo(db)

  val insertUser: Route = path(basePath) {
    import com.github.alikemalocalan.model.UserProtocol._
    post {
      entity(as[User]) { user =>

        onComplete(userRepo.insertUser(user)) {
          case Success(_) => complete(StatusCodes.Created)
          case Failure(e) => complete(StatusCodes.InternalServerError, e.getStackTrace.toString)
        }
      }
    }
  }
  /*
    {
    	"username":"alikemal",
    	"email":"ali@alikemal.org",
    	"password":"123456"
    }
  */
  private val getUser: Route =
    pathPrefix(basePath) {
      pathEndOrSingleSlash {
        get {
          authenticateBasicAsync(realm = "profile", new BasicAuth(userRepo.getUserbyName).authenticator) { username =>
            import com.github.alikemalocalan.model.UserProtocol._
            onSuccess(userRepo.getUserbyName(username)) { result =>
              complete(result)
            }
          }
        }
      }
    }

  private val login = post {
    entity(as[LoginRequest]) {
      case lr@LoginRequest("admin", "admin") =>
        val token = JwtAuth.tryLogin(lr)
        respondWithHeader(token.toHeader) {
          import com.github.alikemalocalan.service.TokenProtocol._
          complete(StatusCodes.OK, token)
        }
      case LoginRequest(_, _) => complete(StatusCodes.Unauthorized)
    }
  }

  private val refleshToken = path("refleshtoken") {
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

  private val userRoutes: Route = insertUser ~ getUser ~ login ~ refleshToken

  val corsSupportedRoutes: Route = corsHandler(userRoutes, basePath)
}
