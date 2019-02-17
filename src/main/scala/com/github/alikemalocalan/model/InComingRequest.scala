package com.github.alikemalocalan.model

import java.sql.Timestamp

import com.github.alikemalocalan.utils.{DateUtils, RandomUtil}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

case class Xp(language: String, xp: Int)

object XpProtocol extends DefaultJsonProtocol {
  implicit val incomingJsonFormat: RootJsonFormat[Xp] = jsonFormat2(Xp)
}

case class XpResponse(code_at: String, xps: Array[Xp]) {
  def toPulse(userId: Int, machineId: Int): Array[Pulse] = {

    val hashedOPulseId: String = RandomUtil.alphanumeric()

    xps.map(xp =>
      Pulse(
        user_id = userId,
        machine_id = machineId,
        pulse_id = hashedOPulseId,
        lang_name = xp.language,
        xp_amount = xp.xp,
        send_at = DateUtils.strToTimestamp(code_at),
        inserted_at = DateUtils.strToTimestamp(code_at),
        updated_at = new Timestamp(System.currentTimeMillis()),
        send_at_local = new Timestamp(System.currentTimeMillis()),
        tz_offset = 3
      )
    )
  }
}


object XpResponseProtocol extends DefaultJsonProtocol {

  import XpProtocol.incomingJsonFormat

  implicit val xpFormat: RootJsonFormat[XpResponse] = jsonFormat2(XpResponse)
}

case class InComingRequest(token: String, xpResponse: XpResponse)