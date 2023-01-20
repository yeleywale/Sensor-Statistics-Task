package org.assignment.sensors

import com.github.tototoshi.csv.{CSVReader, defaultCSVFormat}

import scala.io.Source
import scala.util.{Failure, Success}

class SensorParser(filePath: String) {
  val Sensors: List[Sensor] = {
    loadCSVFile(filePath).flatMap( rowData =>
      Sensor.parse(rowData) match {
        case Success(sensor) => Some(sensor)
        case Failure(ex) => println("log")

        None
      }
    )
  }

  private def loadCSVFile(path: String): List[Map[String, String]] = {
    println("processing")

    val file = Source.fromResource(path, classOf[SensorParser].getClassLoader)

    val reader = CSVReader.open(file)
    val data = reader.allWithHeaders()

    println("number of records loaded")

    data
  }
}
