package com.github.alikemalocalan.model

import spray.json.{DefaultJsonProtocol, RootJsonFormat}

case class Xp(language: String, xp: Int)

object XpProtocol extends DefaultJsonProtocol {
  implicit val incomingJsonFormat: RootJsonFormat[Xp] = jsonFormat2(Xp)
}

case class XpResponse(code_at: String, xps: Array[Xp])


object XpResponseProtocol extends DefaultJsonProtocol {

  import XpProtocol.incomingJsonFormat

  implicit val xpFormat: RootJsonFormat[XpResponse] = jsonFormat2(XpResponse)
}



