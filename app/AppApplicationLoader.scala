
import controllers.ApplicationController
import play.api.ApplicationLoader.Context
import play.api._
import play.api.libs.ws.ahc.AhcWSComponents
import play.api.mvc._
import router.Routes
import com.softwaremill.macwire._
import _root_.controllers.AssetsComponents
import actors.StatsActor
import actors.StatsActor.Ping
import akka.actor.Props
import filters.StatsFilter
import play.api.cache.ehcache.EhCacheComponents
import play.api.db.{DBComponents, HikariCPComponents}
import play.api.db.evolutions.{DynamicEvolutions, EvolutionsComponents}
import play.api.routing.Router
import play.filters.HttpFiltersComponents
import service.{AuthService, SunService, WeatherService}
import scalikejdbc.config.DBs

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

class AppComponents(context: Context) extends BuiltInComponentsFromContext(context)
  with AhcWSComponents with HttpFiltersComponents with EvolutionsComponents
  with DBComponents with HikariCPComponents with AssetsComponents with EhCacheComponents {

  override lazy val controllerComponents = wire[DefaultControllerComponents]
  lazy val prefix: String = "/"
  lazy val router: Router = wire[Routes]
  lazy val applicationController = wire[ApplicationController]
  lazy val sunService = wire[SunService]
  lazy val weatherService = wire[WeatherService]
  lazy val statsFilter = wire[StatsFilter]
  lazy val authService = new AuthService(defaultCacheApi.sync)
  lazy val statsActor = actorSystem.actorOf(Props(wire[StatsActor]), StatsActor.name)

  override def httpFilters: Seq[EssentialFilter] = Seq(statsFilter)

  override lazy val dynamicEvolutions = new DynamicEvolutions

  val onStart = {
    statsActor ! Ping
    applicationEvolutions
    DBs.setupAll()
    Logger.info("The app is about to start")
  }

  applicationLifecycle.addStopHook { () =>
    Logger.info("The app is about to stop")
    DBs.closeAll()
    Future.successful(Unit)
  }

}
