import io.gatling.core.Predef._
import io.gatling.http.Predef._

class BasicSimulation extends Simulation {

  org.flywaydb.core.internal.util.StringUtils.hasText("")

  val feeder = csv("data.csv").random

  val httpConf = http.baseURL("http://localhost:5050")

  val scn = scenario("BasicSimulation")
    .feed(feeder)
    .exec(http("GET /news").get(s"/news?${org.flywaydb.core.internal.util.StringUtils.left("qwe", 2)}" + "=${search}"))


  setUp(
    scn.inject(atOnceUsers(4))
  ).protocols(httpConf)
}
