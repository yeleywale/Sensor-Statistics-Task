package com.akinwalex.sensorcsvprocessor

import com.akinwalex.sensorcsvprocessor.entities.Sensor
import com.github.tototoshi.csv.{CSVReader, defaultCSVFormat}

import java.io.File
import scala.io.Source
import scala.util.{Failure, Success, Try}

class SensorParser(directoryPath: String) {
  def noOfCsvFilesInDirectory(dir: File): Int = {
    val files = Try(dir.listFiles.filter(_.getName().endsWith(".csv"))).getOrElse(Array[File]())
    files.foldLeft(0) { (count, file) =>
        if (file.isFile) count + 1
        else if (file.isDirectory) count + noOfCsvFilesInDirectory(file)
        else count
    }
  }

  def parseCsvFiles: List[Sensor] = {
    val files = new File(directoryPath).listFiles().filter(_.isFile).toList
    for {
      file <- files if file.getName.endsWith(".csv")
      f <- parseCsvToSensor(file.getName)
    } yield {
      f
    }
  }
  private def parseCsvToSensor(filePath: String): List[Sensor] = {
    loadCSVFile(filePath).flatMap( rowData =>
      Sensor.parse(rowData) match {
        case Success(Sensor(id, rt @ Right(r))) =>  Some(Sensor(id, rt))
        case Success(Sensor(id, lt @ Left(l))) =>  Some(Sensor(id, lt))
        case Failure(ex) => println("Unable to pass sensor" + s"of ${ex.getMessage} - row was $rowData")

        None
      }
    )
  }

  private def loadCSVFile(path: String): List[Map[String, String]] = {
    val file = Source.fromResource(path, classOf[SensorParser].getClassLoader)
    val reader = CSVReader.open(file)
    val data = reader.allWithHeaders()
    data
  }
}
