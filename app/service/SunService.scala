package service

import java.time.{ZoneId, ZonedDateTime}
import java.time.format.DateTimeFormatter

import model.SunInfo
import play.api.libs.ws.WSClient

import scala.concurrent.{ExecutionContext, Future}

class SunService(ws: WSClient)(implicit ec: ExecutionContext) {

  def getSunInfo(lat: Double, lng: Double): Future[SunInfo] = {
    ws.url("http://api.sunrise-sunset.org/json")
      .withQueryStringParameters(
        "lat" -> lat.toString,
        "lng" -> lng.toString,
        "formatted" -> "0")
      .get()
      .map {
        response =>
          val sunriseTime = (response.json \ "results" \ "sunrise").as[String]
          val sunsetTime = (response.json \ "results" \ "sunset").as[String]
          val sunr = ZonedDateTime.parse(sunriseTime)
          val suns = ZonedDateTime.parse(sunsetTime)
          val formatter = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.of("Australia/Sydney"))
          SunInfo(sunr.format(formatter), suns.format(formatter))
      }
  }
}
