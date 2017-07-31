package controllers

import actors.StatsActor
import akka.actor.ActorSystem
import akka.pattern._
import akka.util.Timeout
import model.CombinedData
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc._
import service.{AuthService, SunService, WeatherService}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationLong

case class UserLoginData(username: String, password: String)

class ApplicationController (components: ControllerComponents,
                             sunService: SunService,
                             weatherService: WeatherService,
                             authService: AuthService,
                             actorSystem: ActorSystem)(implicit ec: ExecutionContext) extends AbstractController(components) {

  val userDataForm = Form {
    mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText
    )(UserLoginData.apply)(UserLoginData.unapply)
  }

  // To use the ? (ask) Akka pattern we need to keep in mind:
  // - the ask pattern is not there by default. we need to import from akka.pattern
  // - an implicit timeout must be in scope
  // - ? returns an Future[Any] that must be casted with mapTo

  def login = Action { Ok(views.html.login()) }

  def doLogin = Action {
    implicit request =>
      userDataForm.bindFromRequest.fold(
        formWithErrors => Ok(views.html.login(Some("Invalid credentials"))),
        userData => {
          val maybeCookie = authService.login(userData.username, userData.password)
          maybeCookie match {
            case Some(cookie) =>
              Redirect("/").withCookies(cookie)
            case None =>
              Ok(views.html.login(Some("Login failed")))
          }
        }
      )
  }

  def index = Action {
    Ok(views.html.index())
  }

  def data = Action.async {
    val lat = 33.8830
    val lng = 151.2167
    implicit val timeout = Timeout(5.seconds)

    for {
      sunInfo <- sunService.getSunInfo(lat, lng)
      temperature <- weatherService.getTemperature(lat, lng)
      requestCount <- (actorSystem.actorSelection(StatsActor.path) ? StatsActor.GetStats).mapTo[Int]
    } yield {
      Ok(Json.toJson(CombinedData(sunInfo, temperature, requestCount)))
    }
  }

}