package controllers

import javax.inject._

import model.SunInfo.SunInfo
import play.api.libs.ws.WSClient
import play.api.mvc._

import scala.concurrent.ExecutionContext


class Application @Inject() (components: ControllerComponents, ws: WSClient)(implicit ec: ExecutionContext)
    extends AbstractController(components) {

  def index = Action.async {
    ws.url("http://api.sunrise-sunset.org/json")
      .withQueryStringParameters("lat" -> "33.8830", "lng" -> "151.2167", "formatted" -> "0")
      .get()
      .map {
        response =>
          val sunriseTime = (response.json \ "results" \ "sunrise").as[String]
          val sunsetTime = (response.json \ "results" \ "sunset").as[String]
          val sunInfo = SunInfo(sunriseTime, sunsetTime)
          Ok(views.html.index(sunInfo))
      }
  }

}
