package actors

import actors.StatsActor.{GetStats, Ping, RequestReceived}
import akka.actor.{Actor, Props}
import play.api.Logger

class StatsActor extends Actor {
  var counter = 0

  override def receive: Receive = {
    case Ping =>
      Logger.info("Ping!")
      ()
    case RequestReceived =>
      Logger.info("Incremented")
      counter += 1
    case GetStats =>
      Logger.info("GetStats")
      sender() ! counter
  }
}


object StatsActor {
  def props = Props[StatsActor]
  val name = "statsActor"
  val path = s"/user/$name"

  case object Ping
  case object RequestReceived
  case object GetStats
}

