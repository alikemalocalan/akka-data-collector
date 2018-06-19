name := "akka-http-slick-example"

version := "0.1"

scalaVersion := "2.11.8"

libraryDependencies ++= {
  val akkaV = "2.4.8"
  val scalaTestV = "2.2.6"
  val slickV = "3.2.1"
  Seq(
    "com.typesafe.akka" %% "akka-http-core"                     % akkaV,
    "com.typesafe.akka" %% "akka-http-spray-json-experimental"  % akkaV,
    "com.typesafe.akka" %% "akka-http-testkit-experimental"     % "2.4.2-RC3",
    "com.typesafe" % "config" % "1.3.0",
    "org.scalatest"     %% "scalatest"                          % scalaTestV % "test",
    "org.postgresql" % "postgresql" % "42.2.2",
    "com.typesafe.slick" %% "slick" % slickV,
    "com.typesafe.slick" %% "slick-hikaricp" % slickV,
    "com.typesafe.slick" %% "slick-codegen" % slickV,
    "com.typesafe.akka" %% "akka-slf4j"                       % akkaV,
    "ch.qos.logback" % "logback-classic" % "1.2.3"

  )
}