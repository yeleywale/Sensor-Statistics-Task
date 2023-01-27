package com.akinwalex.entities

case class SensorAggregateData(sensorId: String,minHumidity: Either[String, Int], averageHumidity: Either[String,Int], maxHumidity: Either[String,Int]){
    def toPrettyString: String =  s"$sensorId,${minHumidity.getOrElse("NaN")},${averageHumidity.getOrElse("NaN")},${maxHumidity.getOrElse("NaN")}"
}