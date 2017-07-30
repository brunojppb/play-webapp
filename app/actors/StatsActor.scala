package actors

import actors.StatsActor.{GetStats, Ping, RequestReceived}
import akka.actor.{Actor, Props}

class StatsActor extends Actor {
  var counter = 0

  override def receive: Receive = {
    case Ping => ()
    case RequestReceived => counter += 1
    case GetStats => sender() ! counter
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

