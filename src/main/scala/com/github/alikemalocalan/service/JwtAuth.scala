package com.github.alikemalocalan.service

import java.time.Instant

import akka.http.scaladsl.model.headers.RawHeader
import pdi.jwt.{JwtAlgorithm, JwtClaim, JwtSprayJson}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

final case class LoginRequest(username: String, password: String)

final case class Token(`Access-Token`: String, expiredAt: Option[Long]) {
  def toHeader=RawHeader("Access-Token",`Access-Token`)
}

object LoginRequestProtocol extends DefaultJsonProtocol {
  implicit val incomingJsonFormat: RootJsonFormat[LoginRequest] = jsonFormat2(LoginRequest)
}

object TokenProtocol extends DefaultJsonProtocol {
  implicit val incomingJsonFormat: RootJsonFormat[Token] = jsonFormat2(Token)
}

object JwtAuth {
  private val tokenExpiryPeriodInDays = 1
  private val secondToadd = 157784760
  private val secretKey = "super_secret_key"
  private val algorithm = JwtAlgorithm.HS256

  private val claim = JwtClaim(
    expiration = Some(Instant.now.plusSeconds(secondToadd).getEpochSecond),
    issuedAt = Some(Instant.now.getEpochSecond)
  )

  private def token: String = JwtSprayJson.encode(claim, secretKey, algorithm)

  def tryLogin(lr: LoginRequest): Token = Token(token, claim.expiration)

  def isTokenExpired(jwt: String): Boolean = getClaim(jwt).expiration.get < System.currentTimeMillis

  def getClaim(token: String) = JwtSprayJson.decode(token, secretKey, Seq(algorithm)).get

  def isValidToken(token: String): Boolean = JwtSprayJson.isValid(token, secretKey, Seq(algorithm))
}
