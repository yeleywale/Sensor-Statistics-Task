
import com.akinwalex.sensorcsvprocessor.SensorParser
import com.akinwalex.sensorcsvprocessor.entities.SensorAggregateData

import java.io.File
import java.net.URLDecoder
import scala.math.Ordering

object Main {

  def main(args: Array[String]): Unit = {

    val sensorParser = SensorParser(args(0))

    val count = sensorParser.noOfCsvFilesInDirectory(File(args(0)));

    lazy val groupData = sensorParser.parseCsvFiles.groupBy(_.sensorId)

    val measurementSize = sensorParser.parseCsvFiles.size

    val avgHumidity = groupData.mapValues(group => group.map(_.humidity).flatMap(_.toOption).sum / group.size)

    val minHumidity = groupData.mapValues(group => group.map(_.humidity).map(_.toOption).min).collect { case (k, Some(v)) => k -> v }

    val maxHumidity = groupData.mapValues(group => group.map(_.humidity).map(_.toOption).max).collect { case (k, Some(v)) => k -> v }

    val allNaNSensor = groupData.mapValues(group => group.filter(_.humidity.isLeft)).filter { case (_, v) => v.nonEmpty }

    val sensorGroupedById = (avgHumidity :: minHumidity.toList :: maxHumidity.toList :: Nil)
      .flatMap(_.toList).groupBy(_._1).mapValues(_.map(_._2)).toList

    val aggregateSensorData = sensorGroupedById.map {
      case (sensorId, data) => {
        if (data.head == 0) SensorAggregateData(sensorId, Left("NaN"), Left("NaN"), Left("NaN"))
        else SensorAggregateData(sensorId, Right(data.head), Right(data(1)), Right(data(2)))
      }
    }

    val sortedAggregateSensorData = aggregateSensorData.sortWith { (x, y) =>
      (x.averageHumidity, y.averageHumidity) match {
        case (Right(x), Right(y)) => x > y
        case (Right(_), Left(_)) => true
        case (Left(_), Right(_)) => false
        case (Left(x), Left(y)) => x < y
      }
    }

    val rs = sortedAggregateSensorData.map(x => x.toPrettyString)
    println("```")
    println(s"Number of processed file: ${count}")
    println(s"number of processed measurement: ${measurementSize} ")
    println(s"Number of failed measurement: ${allNaNSensor.size} ")
    println("sensor-id,min,avg,max")
    rs.foreach(println)
    println("```")
  }
}
