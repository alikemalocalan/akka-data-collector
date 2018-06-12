name := "akka-http-slick-example"

version := "0.1"

scalaVersion := "2.11.8"

libraryDependencies ++= {
  val akkaV = "2.4.8"
  val scalaTestV = "2.2.6"
  Seq(
    "com.typesafe"      %  "config"                             % "1.3.0",
    "com.typesafe.akka" %% "akka-http-core"                     % akkaV,
    "com.typesafe.akka" %% "akka-http-spray-json-experimental"  % akkaV,
    "com.typesafe.akka" %% "akka-http-testkit-experimental"     % "2.4.2-RC3",
    "org.scalatest"     %% "scalatest"                          % scalaTestV % "test",
    "com.typesafe.akka" %% "akka-slf4j"                       % akkaV,
    "org.postgresql"    % "postgresql"                        % "42.2.2",
    "com.typesafe.slick" %% "slick" % "3.2.1",
    "org.slf4j" % "slf4j-nop" % "1.6.4",
    "com.typesafe.slick" %% "slick-hikaricp" % "3.2.1",
    "com.typesafe.slick" %% "slick-codegen" % "3.2.1"

  )
}