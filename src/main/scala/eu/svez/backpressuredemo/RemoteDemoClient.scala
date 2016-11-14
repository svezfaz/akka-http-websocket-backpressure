package eu.svez.backpressuredemo

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{Message, WebSocketRequest}
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.stream.{ActorMaterializer, ThrottleMode}
import eu.svez.backpressuredemo.Flows._
import kamon.Kamon

import scala.concurrent.duration._

object RemoteDemoClient extends App{

  implicit val system = ActorSystem("backpressure-remote-demo-client")
  implicit val executionContext = system.dispatcher
  implicit val materializer = ActorMaterializer()

  Kamon.start()

  val sink = Flow[Message]
    .throttle(1, 1.second, 1, ThrottleMode.Shaping)
    .via(checkpoint("sink"))
    .to(Sink.ignore)

  val host = "0.0.0.0"
  val port = 8080

  val clientFlow = Http().webSocketClientFlow(WebSocketRequest(s"ws://$host:$port/prices"))

  Source.maybe.via(clientFlow).to(sink).run()

  scala.sys.addShutdownHook {
    Kamon.shutdown()
    system.terminate()
  }
}
