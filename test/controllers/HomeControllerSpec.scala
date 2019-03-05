package controllers

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import ml.combust.bundle.BundleFile
import ml.combust.mleap.runtime.MleapSupport._
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.libs.json.{Json, JsNumber}
import play.api.test._
import play.api.test.Helpers._
import resource._

import play.api.libs.json._


/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 *
 * For more information, see https://www.playframework.com/documentation/latest/ScalaTestingWithScalaTest
 */

//Currently tests are not working

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

  //create proper testJsons (multiple)!

  private val testJson = Json.obj("Retry" -> Json.obj( "CurrentRank" ->  JsNumber(3.0),
    "PreviousResponseCode" -> "04",
    "PreviousRetryOptimization" -> Json.obj("IsApplicable" -> true, "Optimizations"-> Json.obj("channel" -> "", "removeThreeD" -> "") ) ),
    "OriginalTransaction" -> Json.obj("RequestCorrelationId" -> "{{$guid}}", "SessionCorrelationId" -> "1111", "TransactionOriginatorId" -> "18", "InternalAmount" -> JsNumber(58.0), "InitialRecurring" -> "false"
      , "AuthDateTime" -> "2018-11-20T12:21:21Z", "TransactionTypeId" -> "1", "Channel" -> "2", "Eci" -> "4", "CurrencyId" -> "978"
      , "EffectiveValues" -> Json.obj("WalletProvider" -> "1", "AuthorizationType" -> "1", "ChannelType" -> "1", "DwoIndicator" -> "1", "ChannelSubtype" -> "1", "CredentialOnFileType" -> "1"),
    "Card" -> Json.obj("Bin" -> "451233", "ExpiryMonth" -> JsonNumber(11.0), "ExpiryYear" -> JsonNumber(2020.0), "Cv2ResultType" -> "3", "Cv2Response" -> "U", "HolderIp" -> "NULL", "AvsThere" -> "true"
    , "BinDetail" -> Json.obj("CardBrand" -> "1", "CardSubtypeId" -> "1", "CardCommercial" -> "true", "CardPrepaid" -> "true", "IssuerCode" -> "978", "IssuerCountryCode" -> "840", "IssuerTypeId" -> "1")
      , "BinInfo" -> Json.obj("CardSchemaId" -> "2", "ServiceTypeId" -> "1", "IssuerCode" -> "400555", "CountryCode" -> "840", "ProductCode" -> "F", "ProductSubCode" -> "", "IsCommercial" -> "false"
      , "IsPrivateLabel" -> "false", "BrandCode" -> "005", "IsPrepaid" -> "false")  ),
    "Merchant" -> Json.obj("CountryCode" -> "840", "ProcessorId" -> "12", "CategoryCodeGroup" -> "7995", "MemberId" -> "12345", "Ip" -> "192.168.0.1")
  )
  )
    //"OriginalTransaction" -> "InternalAmount" -> 55.0,
    //"OriginalTransaction" -> "Card" -> "AvsThere" -> "true",
    //"OriginalTransaction" -> "Card" -> "BinInfo" -> "IsCommercial" -> "true",
    //  "OriginalTransaction" -> "Merchant" ->  "CategoryCodeGroup" -> "7995"
  //Json.obj("firstsixdigits" -> "123456", "cvvresponse" -> "123", "internalamount" -> 456, "mid" -> "7900" )

  @transient private val request = FakeRequest(POST, "/").withBody(testJson)

  //create a fake request that will get the session string
  @transient private val healthRequest = FakeRequest(GET, "/healthz")

  //POST test maybe just what Ramin did


  //GET test
  //contentAsString(home) must include ("!!!!!!!!!!!!!")

  ///*
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
  //*/

}
