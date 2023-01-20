
package org.assignment.sensors.entities

import org.assignment.sensors.Sensor

import scala.util.{Failure, Success, Try}

case class Sensor( sensorId: String, humidity: Int) {

}

object Sensor {
  def parse(row: Map[String, String]): Try[Sensor] =
    for {
      sensorId <- parseString(row, "Sensor_Id")
      humidity <-  parseInt(row, "Humidity")
    } yield {
      Sensor(sensorId, humidity)
    }

  private def parseInt(row: Map[String, String], key: String): Try[Int] = parseAs(row, key, _.toInt)

  private def parseString(row: Map[String, String], key: String): Try[String] = parseAs(row, key, x => x)

  private def parseAs[T](row: Map[String, String], key: String, parser: String => T): Try[T] =
    for {
      value <- getValue(row, key)

      t <- Try(parser(value))

    } yield t

  private def getValue(row: Map[String, String], key: String): Try[String] =
    row.get(key) match {
      case Some(value) => Success(value)
      case None => Failure(new IllegalArgumentException(s"Couldn't find column $key in row - $row "))
    }
}
