package com.akinwalex.sensorcsvprocessor

import com.akinwalex.sensorcsvprocessor.entities.SensorReport

object Main {
  def main(args: Array[String]): Unit = {

    val sensorReport = SensorReport(args(0))

    println(sensorReport.aggregateSensorData)
    println("```")
    println(s"Number of processed file: ${sensorReport.csvFiles.size}")
    println(s"number of processed measurement: ${sensorReport.sensorReadings.size} ")
    println(s"Number of failed measurement: ${sensorReport.failedSensors.size} ")
    println("sensor-id,min,avg,max")
    sensorReport.report.foreach(println)
    println("```")
  }
}
  