
package com.akinwalex.sensorcsvprocessor.entities

import scala.util.{Failure, Success, Try}

case class Sensor( sensorId: String, humidity: Either[String, Int]) {

  def toPrettyString: String =
    s"[$sensorId] $humidity}"

}

object Sensor {


  def parse(row: Map[String, String]): Try[Sensor] =
    for {
      sensorId <- parseString(row, "sensor-id")
      humidity <- parseEither(row, "humidity")
      //Write to CSV. Create CSv if not exist
    } yield {
      Sensor(sensorId, humidity)
      // process Sensor Aggregate Data
    }

  private def parseInt(row: Map[String, String], key: String): Try[Int] = parseAs(row, key, _.toInt)

  private def parseString(row: Map[String, String], key: String): Try[String] = parseAs(row, key, x => x)

  private def parseEither(row: Map[String, String], key: String): Try[Either[String, Int]] = 
    parseAs(row, key, x => if(x == "NaN") Left(x) else Right(x.toInt))

    
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
