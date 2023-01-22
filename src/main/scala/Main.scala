import org.assignment.sensors.SensorParser
import org.assignment.sensors.entities.SensorAggregateData

import java.net.URLDecoder
import scala.math.Ordering

object Main {

  //pass the path to the resource here
  val ex = new SensorParser(URLDecoder.decode(getClass.getResource("/").getPath, "UTF-8"))

  private val groupedMap = (ex.avgHumidity.toList :: ex.minHumidity.toList :: ex.maxHumidity.toList :: Nil)
    .flatMap(_.toList).groupBy(_._1).mapValues(_.map(_._2)).toList

  private val AggregateData = groupedMap.map {
    case (sensorId, data) => {
      if (data(0) == 0) SensorAggregateData(sensorId, Left("NaN"), Left("NaN"), Left("NaN"))
      else SensorAggregateData(sensorId, Right(data(0)), Right(data(1)), Right(data(2)))
    }
  }

//  def printAggregateData(sensorAggregateData: SensorAggregateData): String =


  private val SortedAggregateData = AggregateData.sortWith { (x, y) =>
    (x.averageHumidity, y.averageHumidity) match {
      case (Right(x), Right(y)) => x > y
      case (Right(_), Left(_)) => true
      case (Left(_), Right(_)) => false
      case (Left(x), Left(y)) => x < y
    }
  }
  def main(args: Array[String]): Unit = {
    val rs = SortedAggregateData.map(x => x.toPrettyString)
    println("```")
    println(s"Number of processed file: ${ex.count}")
    println(s"number of processed measurement: ${ex.measurementSize} ")
    println(s"Number of failed measurement: ${ex.allNaNSensor.size} ")
    println("sensor-id,min,avg,max")
    rs.foreach(println)
    println("```")
  }

}
