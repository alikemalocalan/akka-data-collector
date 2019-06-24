package com.github.alikemalocalan.controller

import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Route, _}


trait CorsSupport {
  def corsHandler(r: Route, pathMatcher: PathMatcher[Unit]): Route = addAccessControlHeaders {
    preflightRequestHandler(pathMatcher) ~ r
  }

  private def addAccessControlHeaders: Directive0 = {
    respondWithHeaders(
      `Access-Control-Allow-Origin`(HttpOriginRange.*),
      `Access-Control-Allow-Credentials`(true),
      `Access-Control-Allow-Headers`("Authorization", "Content-Type", "X-Requested-With")
    )
  }

  private def preflightRequestHandler(pathMatcher: PathMatcher[Unit]): Route = pathPrefix(pathMatcher) {
    pathEndOrSingleSlash {
      options {
        complete(HttpResponse(StatusCodes.OK).withHeaders(`Access-Control-Allow-Methods`(OPTIONS, POST, PUT, GET, DELETE)))
      }
    }
  }
}
