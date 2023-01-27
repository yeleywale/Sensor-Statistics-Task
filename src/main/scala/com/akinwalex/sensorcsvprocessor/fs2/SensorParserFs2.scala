package com.akinwalex.sensorcsvprocessor.fs2

import cats.effect.{Blocker, Concurrent, ExitCode, IO, IOApp}
import fs2.*
import fs2.text.*
import fs2.io.*
import fs2.io.file.{Files, Path}

import java.io.File
import java.net.URLDecoder
import scala.util.{Try, Using}

object SensorStatistics extends IOApp {

  val directory = URLDecoder.decode(getClass.getResource("/").getPath, "UTF-8")

  case class SensorStat(sensorId: String, humidity: Either[String, Int])

  def toInt(s: String): Option[Int] = {
    try {
      Some(s.toInt)
    } catch {
      case e: Exception => None
    }
  }
  private def parseSensorStat(line: String): Option[SensorStat] = {
    val splitLines = line.split(",")
    Try(SensorStat(
      sensorId = line.split(",")(0),
      humidity = toInt(line.split(",")(1)) match
        case Some(i) => Right(i)
        case None => Left("NaN")
    )).toOption
  }

  private def csvLineCount[F[_] : Files : Concurrent](path: Path): F[Long] =
    Files[F].walk(path).filter(_.extName == ".csv").flatMap { p =>
      Files[F].readAll(p).through(text.utf8.decode).through(text.lines).as(1L)
        .csv()
    }.compile.foldMonoid


  def readSensorData(directory: Path): List[SensorStat] = {
    Files[IO].list(directory)
      .flatMap(file => file.extName("csv"))
      .through(text.utf8.decode)
      .through(text.lines)
      .map(parseSensorStat)
      .unNone
  }



  def csvParser[F[_]]: Pipe[F, Byte, List[String]] =
    _.through(text.utf8Decode)
      .through(text.lines)
      .drop(1) // remove headers
      .map(_.split(',').toList) // separate by comma

  val parser: Stream[IO, Unit] =
    csvLineCount(io.file.Path(directory)



  override def run(args: List[String]): IO[ExitCode] = IO.blocking(


  )

}
