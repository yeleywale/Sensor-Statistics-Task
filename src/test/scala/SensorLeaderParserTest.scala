package com.akinwalex.sensorcsvprocessor.test

import com.akinwalex.sensorcsvprocessor.SensorParser
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.BeforeAndAfter
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

import java.net.URLDecoder


class SensorLeaderParserTest extends AnyFlatSpec with BeforeAndAfter with ScalaCheckPropertyChecks {
   var sensorParser: SensorParser = _
   var pathToResourceDirectory: String  = _
   before {
      pathToResourceDirectory = URLDecoder.decode(getClass.getResource("/").getPath, "UTF-8")
      sensorParser = new SensorParser(pathToResourceDirectory)
   }

   "noOfFiles Sensor Parser object" should "return 4" in {
       assert(sensorParser.noOfFiles == 4)
   }

   "The MeasurementSize(No of lines processed) excluding headers" should "be 429" in {
      assert(sensorParser.measurementSize == 429)
   }


   "get a list of sensors from a single csv file" should "list of sensors" in {
      assert(sensorParser.getSensorsFromCSV("MOCK_DATA-3.csv").size == 104)
   }
}
