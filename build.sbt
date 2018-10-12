name := "ce-optimize-api"
organization := "com.payvision.data"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.12"

unmanagedResourceDirectories in Compile += { baseDirectory.value / "mleap" }

libraryDependencies ++= Seq(
  guice,
  "ml.combust.mleap" %% "mleap-runtime" % "0.12.0",
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
)
