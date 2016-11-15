package eu.svez.backpressuredemo

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{Message, TextMessage, WebSocketRequest}
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.stream.{ActorMaterializer, ThrottleMode}
import eu.svez.backpressuredemo.Flows._
import kamon.Kamon

import scala.concurrent.duration._
import scala.util.Random

object RemoteDemoClient extends App{

  implicit val system = ActorSystem("backpressure-remote-demo-client")
  implicit val executionContext = system.dispatcher
  implicit val materializer = ActorMaterializer()

  Kamon.start()

  val source = Source.fromIterator(() => Iterator.continually(() => Random.nextDouble()))
//    .throttle(1, 1.milli, 1, ThrottleMode.Shaping)
    .via(checkpoint("source"))
    .map(p => TextMessage(p.toString))

  val host = "0.0.0.0"
  val port = 8080

  val clientFlow = Flow.fromSinkAndSource(Sink.ignore, source)

  Http().singleWebSocketRequest(WebSocketRequest(s"ws://$host:$port/prices"), clientFlow)._1.foreach { _ =>
    println(s"Websocket server started on $host:$port")
  }

  scala.sys.addShutdownHook {
    Kamon.shutdown()
    system.terminate()
  }
}
