package com.akinwalex.sensorcsvprocessor.entities


import com.akinwalex.sensorcsvprocessor.SensorParser
import com.akinwalex.sensorcsvprocessor.entities

import scala.collection.MapView

class SensorReport(path: String) {
  val sensorParser: SensorParser = SensorParser()
  val csvFiles: List[String] = sensorParser.getCsvFiles(path)

  lazy val sensorReadings: LazyList[Sensor] = csvFiles.flatMap(f => sensorParser.parseCsvFileToSensorData(f)).to(LazyList)

  private lazy val sensorsGroupedById = sensorReadings.groupBy(_.sensorId)
  
  val avgHumidity: MapView[String, Int] = sensorsGroupedById.view.mapValues(list => list.map(_.humidity.getOrElse(0)).sum / list.size)

  val minHumidity: MapView[String, Int] = sensorsGroupedById.view.mapValues(list => list.map(_.humidity.getOrElse(0)).min)

  val maxHumidity: MapView[String, Int] = sensorsGroupedById.view.mapValues(list => list.map(_.humidity.getOrElse(0)).max)

  val failedSensors: MapView[String, LazyList[Sensor]] = sensorsGroupedById.view.mapValues(list => list.filter(_.humidity.isLeft)).filter { case (_, v) => v.nonEmpty }

  private val sensorResult = (avgHumidity :: minHumidity :: maxHumidity :: Nil)
    .flatMap(_.toList).groupBy(_._1).mapValues(_.map(_._2)).to(LazyList)


  val aggregateSensorData: LazyList[SensorAggregateData] = sensorResult map {
    case (sensorId, data) => {
      if (data.head == 0) SensorAggregateData(sensorId, Left("NaN"), Left("NaN"), Left("NaN"))
      else SensorAggregateData(sensorId, Right(data.head), Right(data(1)), Right(data(2)))
    }
  }

  private val sortedAggregateSensorData = aggregateSensorData.sortWith { (x, y) =>
    (x.averageHumidity, y.averageHumidity) match {
      case (Right(x), Right(y)) => x > y
      case (Right(_), Left(_)) => true
      case (Left(_), Right(_)) => false
      case (Left(x), Left(y)) => x < y
    }
  }

  val report: Seq[String] = sortedAggregateSensorData.map(x => x.toPrettyString)
}