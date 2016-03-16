import io.gatling.core.Predef._
import io.gatling.http.Predef._

class BasicSimulation extends Simulation {

  val feeder = csv("data.csv").random

  val httpConf = http.baseURL(ua.eshepelyuk.ratpack.GatlingJava.BASIC_URL)

  val scn = scenario("BasicSimulation")
    .feed(feeder)
    .exec(http("Retrieve News").get(s"${ua.eshepelyuk.ratpack.GatlingJavaTest.RESOURCE_URL}?${org.flywaydb.core.internal.util.StringUtils.left("qwe", 2)}" + "=${search}"))


  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpConf)
}
