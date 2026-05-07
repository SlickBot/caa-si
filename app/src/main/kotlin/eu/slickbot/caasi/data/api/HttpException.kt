package eu.slickbot.caasi.data.api

class HttpException(
  val code: Int,
  val url: String,
) : RuntimeException("HTTP $code for $url")
