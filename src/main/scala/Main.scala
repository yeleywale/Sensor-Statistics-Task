import org.assignment.sensors.SensorParser

object Main {

  val ex = new SensorParser("/Users/akinwalealex/Scala-Personal/Sensor Statistics Task/src/main/resources")




  val combinedMap = ex.minHumidity
  def main(args: Array[String]): Unit = {
    println("Hello world!")
  }
}