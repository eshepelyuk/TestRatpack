package sub1.sub2

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import ua.eshepelyuk.ratpack.GatlingJavaTest.RESOURCE_URL

class BasicSimulation extends Simulation {

  val httpConf = http.baseURL(ua.eshepelyuk.ratpack.GatlingJava.BASIC_URL)

  val scn = scenario("BasicSimulation Two")
    .exec(http("Retrieve News Two")
      .get(s"$RESOURCE_URL"))


  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpConf)
}
