package com.akinwalex.sensorcsvprocessor

import com.akinwalex.sensorcsvprocessor.entities.Sensor
import com.github.tototoshi.csv.{CSVReader, defaultCSVFormat}

import java.io.File
import scala.io.Source
import scala.util.{Failure, Success, Try}

class SensorParser {
  def getCsvFiles(directoryPath: String): List[String] = {
    val files = new File(directoryPath).listFiles().filter(_.isFile).toList
    for {
      file <- files if file.getName.endsWith(".csv")
    } yield {
      file.getAbsolutePath
    }
  }
  def parseCsvFileToSensorData(path: String): LazyList[Sensor] = {
    val file = Source.fromFile(path, "UTF-8")
    val reader = CSVReader.open(file)
    val data = reader.allWithHeaders().to(LazyList).flatMap { rowData =>
      Sensor.parse(rowData) match
        case Success(Sensor(id, rt @ Right(r) ) ) => Some(Sensor(id, rt))
        case Success(Sensor(id, lt @Left(l) ) ) => Some(Sensor(id, lt))
        case Failure(ex) => throw new Exception("Unable to parse sensor" + s"of ${ex.getMessage} - row was $rowData")
    }
    data
  }
}
