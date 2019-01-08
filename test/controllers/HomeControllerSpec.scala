package controllers

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import ml.combust.bundle.BundleFile
import ml.combust.mleap.runtime.MleapSupport._
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.libs.json.Json
import play.api.test._
import play.api.test.Helpers._
import resource._

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

  private val mlModelPath = this.getClass.getClassLoader.getResource("ml-model.zip").getPath
  private val zipBundleM = (for(bundle <- managed(BundleFile(s"jar:file:$mlModelPath"))) yield {
    bundle.loadMleapBundle().get
  }).opt.get
  private val mleapPipeline = zipBundleM.root

  //will need to expand and update these tests
  private val testJson = Json.obj("Retry" -> Json.obj( "CurrentRank" ->  JsNumber(3.0) )
    //"OriginalTransaction" -> "InternalAmount" -> 55.0,
    //"OriginalTransaction" -> "Card" -> "AvsThere" -> "true",
    //"OriginalTransaction" -> "Card" -> "BinInfo" -> "IsCommercial" -> "true",
    //  "OriginalTransaction" -> "Merchant" ->  "CategoryCodeGroup" -> "7995"
  )
  //Json.obj("firstsixdigits" -> "123456", "cvvresponse" -> "123", "internalamount" -> 456, "mid" -> "7900" )

  @transient private val request = FakeRequest(POST, "/").withBody(testJson)


  "HomeController POST" should {

    "render the index page from a new instance of controller" in {
      val controller = new HomeController(stubControllerComponents(), mleapPipeline)
      val home = controller.index().apply(request)

      status(home) mustBe OK
      contentType(home) mustBe Some("application/json")
      contentAsString(home) must include ("IsApplicable")
    }

    "render the index page from the application" in {
      val controller = inject[HomeController]
      val home = controller.index().apply(request)

      status(home) mustBe OK
      contentType(home) mustBe Some("application/json")
      contentAsString(home) must include ("IsApplicable")
    }

    "render the index page from the router" in {
      val home = route(app, request).get

      status(home) mustBe OK
      contentType(home) mustBe Some("application/json")
      contentAsString(home) must include ("IsApplicable")
    }
  }
}
