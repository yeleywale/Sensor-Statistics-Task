package com.akinwalex.sensorcsvprocessor

import com.akinwalex.sensorcsvprocessor.entities.SensorReport

object Main {
  def main(args: Array[String]): Unit = {

    val sensorCalculation = SensorReport(args(0))
    println("```")
    println(s"Number of processed file: ${sensorCalculation.csvFiles.size}")
    println(s"number of processed measurement: ${sensorCalculation.sensorReadings.size} ")
    println(s"Number of failed measurement: ${sensorCalculation.failedSensors.size} ")
    println("sensor-id,min,avg,max")
    sensorCalculation.report.foreach(println)
    println("```")
  }
}
