
import com.akinwalex.sensorcsvprocessor.SensorParser
import com.akinwalex.sensorcsvprocessor.entities.SensorAggregateData

import java.net.URLDecoder
import scala.math.Ordering

object Main {

  val sensorParser = new SensorParser(args(0))

  val sensorGroupedById = (sensorParser.avgHumidity.toList :: sensorParser.minHumidity.toList :: sensorParser.maxHumidity.toList :: Nil)
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

  def main(args: Array[String]): Unit = {

    val sensorParser = new SensorParser(args(0))

    val sensorGroupedById = (sensorParser.avgHumidity.toList :: sensorParser.minHumidity.toList :: sensorParser.maxHumidity.toList :: Nil)
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
    println(s"Number of processed file: ${sensorParser.count}")
    println(s"number of processed measurement: ${sensorParser.measurementSize} ")
    println(s"Number of failed measurement: ${sensorParser.allNaNSensor.size} ")
    println("sensor-id,min,avg,max")
    rs.foreach(println)
    println("```")
  }

}
