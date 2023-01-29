ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.1.0"

val ScalaCsvVersion = "1.3.10"

lazy val root = (project in file("."))
  .settings(
    name := "Sensor Statistics Task",
    libraryDependencies += "com.github.tototoshi" %% "scala-csv" % ScalaCsvVersion,
    libraryDependencies += "org.scalatestplus" %% "scalacheck-1-17" % "3.2.15.0" % "test",
    libraryDependencies += "org.scalatest" %% "scalatest-flatspec" % "3.2.15" % "test"
  )
