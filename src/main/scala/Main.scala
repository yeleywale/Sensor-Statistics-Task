
import cats.effect._
import com.akinwalex.sensorcsvprocessor.entities.SensorReport

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    val sensorReport = SensorReport(args.head)
    for {
      _ <- IO.println(sensorReport.aggregateSensorData)
      _ <- IO.println("```")
      _ <- IO.println(s"Number of processed file: ${sensorReport.csvFiles.size}")
      _ <- IO.println(s"number of processed measurement: ${sensorReport.sensorReadings.size} ")
      _ <- IO.println(s"Number of failed measurement: ${sensorReport.failedSensors.size} ")
      _ <- IO.println("sensor-id,min,avg,max")
      _ <- IO.println("```")
      _ <- IO.println(sensorReport.report.foreach(println))

    } yield ExitCode.Success
}
