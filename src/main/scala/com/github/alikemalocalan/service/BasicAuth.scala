package com.github.alikemalocalan.service

import akka.http.scaladsl.server.directives.Credentials
import com.github.alikemalocalan.model.User

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

class BasicAuth(getUserbyName:String=> Future[User]) {

  def authenticator(credentials: Credentials): Future[Some[String]] =
    credentials match {
      case p @ Credentials.Provided(id)=> getUserbyName(id).map{ user=>
        if(p.verify(user.password)) Some(user.username)
        else throw new Exception("username or pass error")
      }
        case _ =>Future.failed(new Exception("Auth Error") )
    }
}
