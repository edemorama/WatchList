name := """watchlist"""
organization := "com.edemorama"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  guice,
  "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.1" % Test,
  "org.mockito" % "mockito-core" % "2.26.0" % Test
)


