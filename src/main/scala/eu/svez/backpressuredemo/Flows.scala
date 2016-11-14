package eu.svez.backpressuredemo

import akka.NotUsed
import akka.stream.scaladsl.Flow
import kamon.Kamon

object Flows {

  def checkpoint[T](name: String): Flow[T, T, NotUsed] = {
    val msgCounter = Kamon.metrics.counter(name)

    Flow[T].map { x =>
      msgCounter.increment()
      x
    }
  }

}
