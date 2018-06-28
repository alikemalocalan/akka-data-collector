package com.github.alikemalocalan.utils

import java.sql.Timestamp
import java.time.format.DateTimeFormatter
import java.time.{ZoneId, ZonedDateTime}

object DateUtils {
  val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SS'Z'")

  def strToTimestamp(str: String): Timestamp = Timestamp.from(ZonedDateTime.parse(str, formatter.withZone(ZoneId.systemDefault())).toInstant)

  def timestampToString(date: Timestamp): String = formatter.format(date.toInstant)

  def now(): Timestamp = Timestamp.from(ZonedDateTime.now(ZoneId.systemDefault()).toInstant)
}
