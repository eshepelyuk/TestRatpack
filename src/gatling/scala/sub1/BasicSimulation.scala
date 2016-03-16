package sub1

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import ua.eshepelyuk.ratpack.GatlingJavaTest.RESOURCE_URL

class BasicSimulation extends Simulation {

  val httpConf = http.baseURL(ua.eshepelyuk.ratpack.GatlingJava.BASIC_URL)

  val scn = scenario("BasicSimulation One")
    .exec(http("Retrieve News One")
      .get(s"$RESOURCE_URL"))


  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpConf)
}
