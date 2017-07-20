package service

import play.api.libs.ws.WSClient

import scala.concurrent.{ExecutionContext, Future}

class WeatherService(ws: WSClient)(implicit ec: ExecutionContext) {

  def getTemperature(lat: Double, lng: Double): Future[Double] = {
    ws.url("http://api.openweathermap.org/data/2.5/weather")
      .withQueryStringParameters(
        "lat" -> lat.toString,
        "lon" -> lng.toString,
        "units" -> "metric",
        "appId" -> "7d2b7bf00009f7e3371058073b7c7f76")
      .get()
      .map {
        response =>
          (response.json \ "main" \ "temp").as[Double]
      }
  }

}
