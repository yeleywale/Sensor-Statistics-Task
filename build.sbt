ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.1.0"

val ScalaCsvVersion = "1.3.10"

lazy val root = (project in file("."))
  .settings(
    name := "Sensor Statistics Task",
    libraryDependencies += "com.github.tototoshi" %% "scala-csv" % ScalaCsvVersion
  )

