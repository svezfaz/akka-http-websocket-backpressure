name := "akka-backpressure-demo"

version := "1.0"

scalaVersion := "2.11.8"

val akkaV = "2.4.11"
val kamonV = "0.6.3"

val akkaDeps = Seq(
  "com.typesafe.akka" %% "akka-http-experimental" % akkaV
)

val kamonDeps = Seq(
  "io.kamon" %% "kamon-core"          % kamonV,
  "io.kamon" %% "kamon-log-reporter"  % kamonV,
  "io.kamon" %% "kamon-statsd"        % kamonV
)

libraryDependencies ++= akkaDeps ++ kamonDeps

aspectjSettings
javaOptions in run <++= AspectjKeys.weaverOptions in Aspectj
fork in run := true
connectInput in run := true