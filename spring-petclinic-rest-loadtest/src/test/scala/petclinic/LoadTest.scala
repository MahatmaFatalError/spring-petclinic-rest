package petclinic

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.util.Random

class LoadTest extends Simulation {

  val r = new scala.util.Random
  def randomId() = 1 + r. nextInt(50000)

  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl("http://localhost:9966/petclinic/api")

  object PetsResource {
    val getAll: ChainBuilder = exec(http("Pets")
      .get("/pets")
      //.basicAuth("user", "24gh39ugh0")
    )

    val lock: ChainBuilder = exec(http("Pets")
      .get("/pets/lock")
      //.basicAuth("user", "24gh39ugh0")
    )

    val getRandom: ChainBuilder = exec(http("Pets")
      .get("/pets/" + randomId()) ///pets/${randomId}
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
    .exec(PetsResource.getRandom, PetTypeResource.get)

  setUp(petClinicScenario.inject(
    incrementUsersPerSec(20)
      .times(1)
      .eachLevelLasting(5 seconds)
      .separatedByRampsLasting(5 seconds)
      .startingFrom(20)
  )).protocols(httpProtocol)
    //.assertions(global.successfulRequests.percent.is(100))
}
