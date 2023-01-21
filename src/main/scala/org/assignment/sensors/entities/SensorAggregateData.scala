package org.assignment.sensors.entities

case class SensorAggregateData(sensorId: String,minHumidity: Int, averageHumidity: Int, maxHumidity: Int)


object SensorAggregateData {
    val Agg : Map[String, SensorAggregateData]  = Map.empty[String, SensorAggregateData]


}