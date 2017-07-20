package controllers

import java.time.{ZoneId, ZonedDateTime}
import java.time.format.DateTimeFormatter
import javax.inject._

import model.SunInfo.SunInfo
import play.api.libs.ws.WSClient
import play.api.mvc._

import scala.concurrent.ExecutionContext


class Application @Inject() (components: ControllerComponents, ws: WSClient)(implicit ec: ExecutionContext)
    extends AbstractController(components) {

  def index = Action.async {
    val dayTimeData = ws.url("http://api.sunrise-sunset.org/json")
      .withQueryStringParameters("lat" -> "33.8830", "lng" -> "151.2167", "formatted" -> "0")
      .get()

    val weatherData = ws.url("http://api.openweathermap.org/data/2.5/weather")
      .withQueryStringParameters("lat" -> "33.8830", "lon" -> "151.2167", "units" -> "metric", "appId" -> "7d2b7bf00009f7e3371058073b7c7f76")
      .get()

    for {
      dayTime <- dayTimeData
      weather <- weatherData
    } yield {
      val sunriseTime = (dayTime.json \ "results" \ "sunrise").as[String]
      val sunsetTime = (dayTime.json \ "results" \ "sunset").as[String]
      val sunr = ZonedDateTime.parse(sunriseTime)
      val suns = ZonedDateTime.parse(sunsetTime)
      val formatter = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.of("Australia/Sydney"))
      val sunInfo = SunInfo(sunr.format(formatter), suns.format(formatter))
      val temperature = (weather.json \ "main" \ "temp").as[Double]
      Ok(views.html.index(sunInfo, temperature))
    }

  }

}
