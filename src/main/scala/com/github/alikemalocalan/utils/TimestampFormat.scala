package com.github.alikemalocalan.utils

import java.sql.Timestamp
import java.text.SimpleDateFormat

import spray.json.{JsString, JsValue, RootJsonFormat}

object TimestampFormat extends RootJsonFormat[Timestamp] {
  val format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS'Z'")

  override def write(obj: Timestamp): JsValue = JsString(format.format(obj))

  override def read(json: JsValue): Timestamp = {
    val str = json.toString()
    new Timestamp(format.parse(str).getTime)
  }
}
