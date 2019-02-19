enablePlugins(JavaAppPackaging)

name := "akka-data-collector"
version := "0.1"
scalaVersion := "2.11.11"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaV = "2.4.20"
  val scalaTestV = "3.0.1"
  val slickV = "3.3.0"
  Seq(
    "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.13",
    "com.typesafe.akka" %% "akka-slf4j" % akkaV,
    "com.typesafe.slick" %% "slick-hikaricp" % slickV,
    "com.typesafe.slick" %% "slick-codegen" % slickV,
    "org.postgresql" % "postgresql" % "42.2.5",
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "org.scalatest" %% "scalatest" % scalaTestV % Test

  )
}

Revolver.settings