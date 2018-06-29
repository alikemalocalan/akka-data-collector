name := "akka-data-collector"

version := "0.1"

scalaVersion := "2.11.11"

libraryDependencies ++= {
  val akkaV = "2.4.20"
  val scalaTestV = "3.0.1"
  val slickV = "3.2.3"
  Seq(
    "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.13",
    "com.typesafe.akka" %% "akka-slf4j" % akkaV,
    "org.scalatest" %% "scalatest" % scalaTestV % Test,
    "com.typesafe" % "config" % "1.3.0",
    "org.postgresql" % "postgresql" % "42.2.2",
    "com.typesafe.slick" %% "slick-hikaricp" % slickV,
    "com.typesafe.slick" %% "slick-codegen" % slickV,
    "ch.qos.logback" % "logback-classic" % "1.2.3"

  )
}