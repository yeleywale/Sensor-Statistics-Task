package com.akinwalex.sensorcsvprocessor.entities


import com.akinwalex.sensorcsvprocessor.SensorParser
import com.akinwalex.sensorcsvprocessor.entities

class SensorReport(path: String) {
  val sensorParser = SensorParser()
  val csvFiles = sensorParser.getCsvFiles(path)

  lazy val sensorReadings = csvFiles.flatMap(f => sensorParser.parseCsvFileToSensorData(f)).to(LazyList)

  lazy val sensorsGroupedById = sensorReadings.groupBy(_.sensorId)
  val avgHumidity = sensorsGroupedById.view.mapValues(list => list.map(_.humidity.getOrElse(0)).sum / list.size)

  val minHumidity = sensorsGroupedById.view.mapValues(list => list.map(_.humidity.getOrElse(0)).min)

  val maxHumidity = sensorsGroupedById.view.mapValues(list => list.map(_.humidity.getOrElse(0)).max)

  val failedSensors = sensorsGroupedById.view.mapValues(list => list.filter(_.humidity.isLeft)).filter { case (_, v) => v.nonEmpty }

  val sensorResult = (avgHumidity :: minHumidity :: maxHumidity :: Nil)
    .flatMap(_.toList).groupBy(_._1).mapValues(_.map(_._2)).to(LazyList)


  val aggregateSensorData = sensorResult map {
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

  val report = sortedAggregateSensorData.map(x => x.toPrettyString)
}

