package org.assignment.sensors

import com.github.tototoshi.csv.{CSVReader, defaultCSVFormat}
import org.assignment.sensors.entities.Sensor

import java.io.File
import scala.io.Source
import scala.util.{Failure, Success, Try}

class SensorParser(directoryPath: String) {

  val count = countFiles(File(directoryPath));

  val groupData = processDirectory(directoryPath).groupBy(_.sensorId)

  // val listData = processDirectory(directoryPath).toList.view.groupBy(_.sensorId)

  val avgHumidity = groupData.mapValues(group => group.map(_.humidity).flatMap(_.right.toOption).sum / group.size)

  val minHumidity = groupData.mapValues(group => group.map(_.humidity).flatMap(_.right.toOption).reduceLeft((x, y) => x min y))

  val maxHumidity = groupData.mapValues(group => group.map(_.humidity).flatMap(_.right.toOption).reduceLeft((x, y) => x max y))

  //Handle the NaN Sensor edge case
  val allNaNSensor = groupData.mapValues(group => group.filter(_.humidity.isLeft)).filter{case (_, v) => v.nonEmpty}

  val sortedGroup = groupData.mapValues(_.sortWith((a, b) => (a.humidity, b.humidity)
    match {
      case (Right(aVal), Right(bVal)) => aVal > bVal
      case (Right(_), Left(bVal: String)) => false
      case (Left(aVal: String), Right(_)) => false
      case (Left(_), Left(_)) => true
    }))

  def countFiles(dir: File): Int = {
    val files = Try(dir.listFiles.filter(_.getName().endsWith(".csv"))).getOrElse(Array[File]())
    files.foldLeft(0) { (count, file) =>
        if (file.isFile) count + 1
        else if (file.isDirectory) count + countFiles(file)
        else count
    }
  }

  private def processDirectory(dir: String): List[Sensor] = {

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
    println("processing")

    val file = Source.fromResource(path, classOf[SensorParser].getClassLoader)

    val reader = CSVReader.open(file)
    val data = reader.allWithHeaders()

    println("number of records loaded")

    data
  }
}
