package controllers

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.libs.json.Json
import play.api.test._
import play.api.test.Helpers._

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 *
 * For more information, see https://www.playframework.com/documentation/latest/ScalaTestingWithScalaTest
 */
class HomeControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {
  implicit val materializer: ActorMaterializer = ActorMaterializer()(ActorSystem())
  Helpers.stubControllerComponents(
    playBodyParsers = Helpers.stubPlayBodyParsers(materializer)
  )
  @transient private val request = FakeRequest(POST, "/").withBody(Json.obj())

  "HomeController GET" should {

    "render the index page from a new instance of controller" in {
      val controller = new HomeController(stubControllerComponents())
      val home = controller.index().apply(request)

      status(home) mustBe OK
      contentType(home) mustBe Some("application/json")
      contentAsString(home) must include ("continue")
    }

    "render the index page from the application" in {
      val controller = inject[HomeController]
      val home = controller.index().apply(request)

      status(home) mustBe OK
      contentType(home) mustBe Some("application/json")
      contentAsString(home) must include ("continue")
    }

    "render the index page from the router" in {
      val home = route(app, request).get

      status(home) mustBe OK
      contentType(home) mustBe Some("application/json")
      contentAsString(home) must include ("continue")
    }
  }
}
