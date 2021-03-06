package com.github.alikemalocalan.utils

import java.sql.Timestamp
import java.time.format.DateTimeFormatter
import java.time.{ZoneId, ZonedDateTime}
import java.util.TimeZone

import spray.json.{DefaultJsonProtocol, JsString, JsValue, RootJsonFormat}

object DateUtils {
  val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX").withZone(TimeZone.getDefault.toZoneId)

  def strToTimestamp(str: String): Timestamp = Timestamp.from(ZonedDateTime.parse(str, formatter).toInstant)

  def timestampToString(date: Timestamp): String = formatter.format(date.toInstant)

  def now(): Timestamp = Timestamp.from(ZonedDateTime.now(ZoneId.systemDefault()).toInstant)

  implicit object TimestampFormat extends RootJsonFormat[Timestamp] with DefaultJsonProtocol {

    override def write(obj: Timestamp): JsValue = JsString(DateUtils.timestampToString(obj))

    override def read(json: JsValue): Timestamp = {
      DateUtils.strToTimestamp(json.convertTo[String])
    }
  }

}
