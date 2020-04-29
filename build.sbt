name := "game-experience-collector"
version := "0.1"

lazy val akkaV = "2.5.26"
lazy val akkaHttpV = "10.1.11"
lazy val scalaTestV = "3.1.1"
lazy val slickV = "3.3.2"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.github.alikemalocalan",
      scalaVersion := "2.12.11"
    )),
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpV,
      "com.typesafe.akka" %% "akka-stream" % akkaV,
      "com.typesafe.akka" %% "akka-persistence" % akkaV,
      "com.typesafe.akka" %% "akka-slf4j" % akkaV,
      "com.typesafe.slick" %% "slick-hikaricp" % slickV,
      "com.typesafe.slick" %% "slick-codegen" % slickV,
      "org.scalaz" %% "scalaz-core" % "7.2.30",
      "org.postgresql" % "postgresql" % "42.2.12",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "org.scalatest" %% "scalatest" % scalaTestV % Test,
      "com.pauldijou" %% "jwt-spray-json" % "4.3.0"
    )
  )

enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)
enablePlugins(AshScriptPlugin)

mainClass in Compile := Some("com.github.alikemalocalan.App")

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

dockerBaseImage := "openjdk:jre-alpine"
dockerExposedPorts ++= Seq(8080, 9000)

dockerUsername := Some("alikemalocalan")

Revolver.settings

parallelExecution in Test := false

fork := true