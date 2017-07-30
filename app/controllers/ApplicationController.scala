package controllers

import javax.inject._

import play.api.mvc._
import service.{SunService, WeatherService}
import scala.concurrent.ExecutionContext


class ApplicationController @Inject()(components: ControllerComponents,
                                      sunService: SunService,
                                      weatherService: WeatherService)(implicit ec: ExecutionContext)
                              extends AbstractController(components) {

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
