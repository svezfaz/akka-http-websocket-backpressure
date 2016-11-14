package eu.svez.backpressuredemo

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Sink, Source}
import akka.stream.{ActorMaterializer, ThrottleMode}
import eu.svez.backpressuredemo.Flows._
import kamon.Kamon

import scala.concurrent.duration._

object LocalDemo extends App {

  implicit val system = ActorSystem("backpressure-local-demo")
  implicit val executionContext = system.dispatcher
  implicit val materializer = ActorMaterializer()

  Kamon.start()

  Source.repeat("hello")
    .throttle(1, 1.milli, 1, ThrottleMode.Shaping)
    .via(checkpoint("source"))
    .throttle(1, 1.second, 1, ThrottleMode.Shaping)
    .via(checkpoint("sink"))
    .to(Sink.ignore)
    .run()

  scala.sys.addShutdownHook {
    Kamon.shutdown()
    system.terminate()
  }
}

