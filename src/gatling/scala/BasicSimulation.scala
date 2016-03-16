import io.gatling.core.Predef._
import io.gatling.http.Predef._

class BasicSimulation extends Simulation {

  val feeder = csv("data.csv").circular

  val httpConf = http.baseURL("http://localhost:5050")

  val scn = scenario("BasicSimulation")
    .feed(feeder)
    .exec(http("GET /news").get("/news?query=${search}"))

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpConf)
}
