package com.akinwalex.sensorcsvprocessor.test

import com.akinwalex.sensorcsvprocessor.SensorParser
import com.akinwalex.sensorcsvprocessor.entities.SensorReport
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.BeforeAndAfter
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

import java.net.URLDecoder


class SensorParserTest extends AnyFlatSpec with BeforeAndAfter with ScalaCheckPropertyChecks {
   var sensorReport: SensorReport = _
   var pathToResourceDirectory: String  = _
   before {
      pathToResourceDirectory = "/Users/akinwalealex/Scala-Personal/Sensor Statistics Task/src/main/resources/"
      sensorReport = new SensorReport(path = pathToResourceDirectory)
   }

//   It reports how many files it processed
   "noOfFiles Sensor Parser object" should "return 4" in {
       assert(sensorReport.csvFiles.size == 4)
   }

//   It reports how many files it processed
   "The MeasurementSize(No of lines processed) excluding headers" should "be 429" in {
      assert(sensorReport.sensorReadings.size == 431)
   }

//   It reports how many files it processed
   "get a list of sensors from a single csv file" should "list of sensors" in {
      assert(sensorReport.failedSensors.size == 4)
   }

   //Sensors with only `NaN` measurements have min/avg/max as `NaN/NaN/NaN`
   "get a list of sensors from a single csv file" should "list of sensors" in {
      assert(sensorReport.aggregateSensorData.collect(x => x.maxHumidity).contains(Left()))
   }
}
