

class BasicSimulation extends Simulation {

  val feeder = csv("data.csv").random

  val httpConf = http.baseURL("http://localhost:5050")

  val scn = scenario("BasicSimulation")
    .feed(feeder)
    .exec(http("GET /news").get("/news?query=${search}"))

  setUp(
    scn.inject(atOnceUsers(4))
  ).protocols(httpConf)
}
