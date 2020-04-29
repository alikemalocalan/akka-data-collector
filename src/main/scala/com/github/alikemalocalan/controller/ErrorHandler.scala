package com.github.alikemalocalan.controller

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.ExceptionHandler
import com.typesafe.scalalogging.LazyLogging


object ErrorHandler extends LazyLogging {

  implicit def exceptionHandler: ExceptionHandler = ExceptionHandler {
    case e: Throwable =>
      logger.error(s"ERROR : InternalServerError = ${e.getMessage}")
      complete(StatusCodes.InternalServerError, e.getMessage)
  }

}
