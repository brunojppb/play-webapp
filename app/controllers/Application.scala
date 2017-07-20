package controllers

import javax.inject._

import play.api.libs.ws.WSClient
import play.api.mvc._
import service.{SunService, WeatherService}
import scala.concurrent.ExecutionContext


class Application @Inject() (components: ControllerComponents, ws: WSClient)(implicit ec: ExecutionContext)
    extends AbstractController(components) {

  val sunService = new SunService(ws)
  val weatherService = new WeatherService(ws)

  def index = Action.async {
    val lat = 33.8830
    val lng = 151.2167

    for {
      sunInfo <- sunService.getSunInfo(lat, lng)
      temperature <- weatherService.getTemperature(lat, lng)
    } yield {
      Ok(views.html.index(sunInfo, temperature))
    }

  }

}
