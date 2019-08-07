package petclinic

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

class LoadTest extends Simulation {

  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl("http://localhost:9966/petclinic/api/")

  object PetsResource {
    val get: ChainBuilder = exec(http("Pets")
      .get("/pets")
      //.basicAuth("user", "24gh39ugh0")
    )
  }

  object PetTypeResource {
    val get: ChainBuilder = exec(http("Pet Types")
      .get("/pettypes")
      //.basicAuth("user", "24gh39ugh0")
    )
  }

  object VetsResource {
    val get: ChainBuilder = exec(http("Vets")
      .get("/vets")
      //.basicAuth("user", "24gh39ugh0")
    )
  }

  val petClinicScenario: ScenarioBuilder = scenario("RampUpUsers")
    .exec(PetTypeResource.get)

  setUp(petClinicScenario.inject(
    incrementUsersPerSec(20)
      .times(1)
      .eachLevelLasting(5 seconds)
      .separatedByRampsLasting(5 seconds)
      .startingFrom(20)
  )).protocols(httpProtocol)
    .assertions(global.successfulRequests.percent.is(100))
}
