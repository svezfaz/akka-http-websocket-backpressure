package eu.svez.backpressuredemo

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.TextMessage
import akka.http.scaladsl.server.Directives._
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.stream.{ActorMaterializer, ThrottleMode}
import eu.svez.backpressuredemo.Flows._
import kamon.Kamon

import scala.concurrent.duration._

object RemoteDemoServer extends App{

  implicit val system = ActorSystem("backpressure-remote-demo-server")
  implicit val executionContext = system.dispatcher
  implicit val materializer = ActorMaterializer()

  Kamon.start()

  val source = Source.repeat("hello")
    .throttle(1, 1.milli, 1, ThrottleMode.Shaping)
    .via(checkpoint("source"))
    .map(TextMessage(_))

  val handlerFlow = Flow.fromSinkAndSource(Sink.ignore, source)

  val route = get {
    path("prices") {
      extractUpgradeToWebSocket{ wsUpgrade =>
        complete(wsUpgrade.handleMessages(handlerFlow))
      }
    }
  }

  val host = "0.0.0.0"
  val port = 8080

  Http().bindAndHandle(route, host, port).map { _ =>
    println(s"Websocket server started on $host:$port")
  }

  scala.sys.addShutdownHook {
    Kamon.shutdown()
    system.terminate()
  }

}
