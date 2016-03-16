package data

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class BasicSimulation2 extends Simulation {

  org.flywaydb.core.internal.util.StringUtils.hasText("")

  val httpConf = http.baseURL(ua.eshepelyuk.ratpack.GatlingJava.BASIC_URL)

  val scn = scenario("data.BasicSimulation2")
    .exec(http("Retrieve Qwe2")
      .get(s"${ua.eshepelyuk.ratpack.GatlingJavaTest.RESOURCE_URL}?${org.flywaydb.core.internal.util.StringUtils.left("qwe", 2)}" + "=${search}"))


  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpConf)
}
