
import controllers.ApplicationController
import play.api.ApplicationLoader.Context
import play.api._
import play.api.libs.ws.ahc.AhcWSComponents
import play.api.mvc._
import router.Routes
import com.softwaremill.macwire._
import _root_.controllers.AssetsComponents
import play.api.routing.Router
import play.filters.HttpFiltersComponents
import service.{SunService, WeatherService}

import scala.concurrent.Future

class AppApplicationLoader extends ApplicationLoader {

  override def load(context: ApplicationLoader.Context): Application = {
    LoggerConfigurator(context.environment.classLoader).foreach {
      config =>
        config.configure(context.environment)
    }
    new AppComponents(context).application
  }

}

class AppComponents(context: Context) extends BuiltInComponentsFromContext(context) with AhcWSComponents with AssetsComponents with HttpFiltersComponents {

  override lazy val controllerComponents = wire[DefaultControllerComponents]
  lazy val prefix: String = "/"
  lazy val router: Router = wire[Routes]
  lazy val applicationController = wire[ApplicationController]
  lazy val sunService = wire[SunService]
  lazy val weatherService = wire[WeatherService]

  val onStart = {
    Logger.info("The app is about to start")
  }

  applicationLifecycle.addStopHook { () =>
    Logger.info("The is about to stop")
    Future.successful(Unit)
  }

}