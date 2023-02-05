ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.0"

val ScalaCsvVersion = "1.3.10"

lazy val root = (project in file("."))
  .settings(
    name := "Sensor Statistics Task",
    libraryDependencies += "com.github.tototoshi" %% "scala-csv" % ScalaCsvVersion,
    libraryDependencies += "co.fs2" %% "fs2-core" % "3.5.0",
    libraryDependencies += "co.fs2" %% "fs2-io" % "3.5.0",
    libraryDependencies += "org.typelevel" %% "cats-effect" % "3.4.5",
    libraryDependencies += "org.scalatestplus" %% "scalacheck-1-17" % "3.2.15.0" % "test",
    libraryDependencies += "org.scalatest" %% "scalatest-flatspec" % "3.2.15" % "test"
  )
