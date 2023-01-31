package com.akinwalex.sensorcsvprocessor

import com.akinwalex.sensorcsvprocessor.entities.Sensor
import com.github.tototoshi.csv.{CSVReader, defaultCSVFormat}

import java.io.File
import scala.io.Source
import scala.util.{Failure, Success, Try}

class SensorParser(directoryPath: String) {

  val count = countFiles(File(directoryPath));

  private lazy val groupData = processDirectory(directoryPath).groupBy(_.sensorId)

  val measurementSize = processDirectory(directoryPath).size

  val avgHumidity = groupData.mapValues(group => group.map(_.humidity).flatMap(_.toOption).sum / group.size)

  val minHumidity = groupData.mapValues(group => group.map(_.humidity).map(_.toOption).min).collect{case (k, Some(v)) => k -> v}

  val maxHumidity = groupData.mapValues(group => group.map(_.humidity).map(_.toOption).max).collect{case (k, Some(v)) => k -> v}

    // val sortedGroup = groupData.mapValues(_.sortWith((a, b) => (a.humidity, b.humidity)
  //   match {
  //     case (Right(aVal), Right(bVal)) => aVal > bVal
  //     case (Right(_), Left(bVal: String)) => false
  //     case (Left(aVal: String), Right(_)) => false
  //     case (Left(_), Left(_)) => true
  //   }))
  // def findHumidity(data: Map[String, List[Sensor]], f: (Seq[Option[Int]]) => Double) = {
  //   data.mapValues(group => {
  //       val humidityValues = group.map(_.humidity).flatMap(_.right.toOption)
  //       if(humidityValues.isEmpty) 0.0 else f(humidityValues)
  //   })
  // }
  // val avgHumidity = findHumidity(groupData, x => x.sum.toDouble / x.size)
  // val minHumidity = findHumidity(groupData, _.min)
  // val maxHumidity = findHumidity(groupData, _.max)

  //Handle the NaN Sensor edge casex`
  val allNaNSensor = groupData.mapValues(group => group.filter(_.humidity.isLeft)).filter{case (_, v) => v.nonEmpty}


  private def countFiles(dir: File): Int = {
    val files = Try(dir.listFiles.filter(_.getName().endsWith(".csv"))).getOrElse(Array[File]())
    files.foldLeft(0) { (count, file) =>
        if (file.isFile) count + 1
        else if (file.isDirectory) count + countFiles(file)
        else count
    }
  }

  def processDirectory(dir: String): List[Sensor] = {

    val files = new File(dir).listFiles().filter(_.isFile).toList

    for {
      
      file <- files if file.getName.endsWith(".csv")
      
      f <- getSensor(file.getName)

    } yield {
      f
    }

  }

  private def csvFileCount(file: File) = ???

  private def getSensor(filePath: String): List[Sensor] = {
    loadCSVFile(filePath).flatMap( rowData =>
      Sensor.parse(rowData) match {
        case Success(Sensor(id, rt @ Right(r))) =>  Some(Sensor(id, rt))
        case Success(Sensor(id, lt @ Left(l))) =>  Some(Sensor(id, lt))
        case Failure(ex) => println("Skipping book: Unable to parse row because " +
            s"of ${ex.getMessage} - row was $rowData")

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
