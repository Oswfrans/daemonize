package controllers

import javax.inject._
import ml.combust.mleap.runtime.frame.{DefaultLeapFrame, Transformer}
import models.{ApiResponse, OriginalTransaction, ModifiedTransaction}
import play.api.libs.json._
import play.api.mvc._

import org.etcd4s.Etcd4sClientConfig
import org.etcd4s.Etcd4sClient
import org.etcd4s.services._
import org.etcd4s.implicits._
import org.etcd4s.formats.Formats._
import org.etcd4s.pb.etcdserverpb._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Try,Success,Failure}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.Future

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents, mleapPipeline: Transformer) extends AbstractController(cc) {

  //SF client API's, due to the classes being private wee need to ugly repeat the client definition in all the methods
  //ugh, consider rewriting the lib? Joao would hate it lol
  def getVal( valKey: String) : Future[Option[ String ] ] = {
       val config = Etcd4sClientConfig(
      address = "127.0.0.1",
      port = 2379
    )
    val client = Etcd4sClient.newClient(config)
    //val hello = valKey //"55555"
    val currentValue =  client.kvService.getKey(valKey)
    return currentValue
  }

  def setKey(valKey: String, valSet: String) : Unit = {
    val config = Etcd4sClientConfig(
      address = "127.0.0.1",
      port = 2379
    )
    val client = Etcd4sClient.newClient(config)
    //val hello = "55555"
    client.kvService.setKey(valKey, valSet)
  }

  //this inserts a lot of keys in the docker instance for testing purposes
  def insertKeyz() : Unit = {
    val config = Etcd4sClientConfig(
      address = "127.0.0.1",
      port = 2379
    )
    val client = Etcd4sClient.newClient(config)
    for( i <- 1 to 25000) {
      for (x <- Array("a", "b", "c", "d")) {
        val key = i.toString+x
        val value = (i).toString
        client.kvService.setKey(key, value)
      }
    }
  }


  def index(): Action[JsValue] = Action.async(parse.tolerantJson) { implicit request: Request[JsValue] =>
    request.body.validate[OriginalTransaction] match {
      case success: JsSuccess[OriginalTransaction] =>
        val input = success.value

        //because of a succesful parse of the api call, we gather data from our KV store
        //for now testcase, later we will need to adapt the leapframe to include the actual kv data

        //generate different candidate transactions here
        //we generate the default transaction if the currentrank is not too high
        //current implentation assumes a certain schema, if this changes we need to change the code
        if ( OriginalTransaction.toRow(input, 0).getDouble(55) < 3.0 ) {

          //will probably need to change the action to async???!!!!!!!!

          //we need to get the values for the keys
          //needs to be changed to something extracted from input!!

          val testKey = "10000"
          val keyArray = for (a <- Array("a","b","c", "d")) yield testKey+a

          //test both option and future option version !!!!!
          def getValResolve(fut : Option[String] ) : String = {
            fut.map(  value =>
              value match {
                case Some(x)   => {
                  return x.toString
                }
                case None => {
                  return 0.toString
                }
              }
            )
          }

          //not sure that this value deals properly with the Option?!!!!!!!!!!!!!
          //need to change getVal?????? and toRowKV ?????????????????????????????
          val keyValArray = for (x <- keyArray) yield getValResolve( getVal( x) )

          //based on this we pass these values to the adapted toRow method, create new toRow method

          //question is how do we deal with the new ogtxn given that the model does not accept it?
          //for now just pass as the values we will not have in the new contract?

          // we set new values for the key
          // needs to be changed to be something derived from input!!!!
          for (x <- keyArray) {
            setKey(x, "-1")
          }

          // 0 normal txn
          // 1 channel moto (channel 2, channelchange 1 and threed 0 and threedchange 1 )
          // 2 remove threed (threed 0 and threedchange 1)

          //so this a oneliner that does all the stuff we do below, but is much less readable. I feel conflicted
          val comprehensionArray = for (x <- Seq.range(0,3) ) yield mleapPipeline.transform(DefaultLeapFrame(OriginalTransaction.schema, Seq(OriginalTransaction.toRow(input, x)))).get.dataset.head.getAs[Double](183)
          //KV version
          val comprehensionArrayKV = for (x <- Seq.range(0,3) ) yield mleapPipeline.transform(DefaultLeapFrame(OriginalTransaction.schema, Seq(OriginalTransaction.toRowKV(input, x, keyValArray)))).get.dataset.head.getAs[Double](183)

          /*
          //generate the different versions of the Row object
          val frame = DefaultLeapFrame(OriginalTransaction.schema, Seq(OriginalTransaction.toRow(input, 0)))
          val frame2 = DefaultLeapFrame(OriginalTransaction.schema, Seq(OriginalTransaction.toRow(input, 1)))
          val frame3 = DefaultLeapFrame(OriginalTransaction.schema, Seq(OriginalTransaction.toRow(input, 2)))

          val transform = mleapPipeline.transform(frame).get
          val transform2 = mleapPipeline.transform(frame2).get
          val transform3 = mleapPipeline.transform(frame3).get

          val result = transform.dataset.head
          val result2 = transform2.dataset.head
          val result3 = transform3.dataset.head

          //using index of and max way to determine what to do due to it being both fast and concise
          val resultArray = List(result.getAs[Double](183), result2.getAs[Double](183), result3.getAs[Double](183) )
          val testArray = List(result.getAs[Double](183), 0.01, 0.02, 0.03)
          */

          comprehensionArray.indexOf(comprehensionArray.max) match {
            case 0 =>
              val changes = 0
              Ok(Json.toJson(ApiResponse(changes))).as(JSON)

            case 1 =>
              val changes = 1
              Ok(Json.toJson(ApiResponse(changes))).as(JSON)

            case 2 =>
              val changes = 2
              Ok(Json.toJson(ApiResponse(changes))).as(JSON)

          }
        }
        else {
          val changes = 0
          Ok(Json.toJson(ApiResponse(changes))).as(JSON)
        }

        //val predictResultVal = result.getAs[Double](183) //getDouble(183) //current model has 183 values, change when we change schema

        //based on the result we get we pass something to modifiedTransaction

        //for now we hardcode
        //1 no change , 2 change channel , 3 remove threed, 4 change channel & change threed
        // if mcc changes happen you will pass 2 parameters
        //val changes = 2

        //Ok(Json.toJson(ApiResponse(changes))).as(JSON)
        //Ok(Json.toJson(ModifiedTransaction(result))).as(JSON)

      case e : JsError => BadRequest("Errors: " + JsError.toJson(e) ) //.toString())
      //case JsError(_) => BadRequest("Invalid Input!")
    }
  }


  //Array(0,1,2,3,4,5)
  //Array int is the most efficient, still need to determine which approach is better
  //new object or more keys
  def healthz() = Action.async {
    //insertKeyz()


    getVal("55555").map(  value =>
      value match {
        case Some(x)   => { //if x != "Future(<not completed>)"

          setKey("55555" ,(x.toInt + 1).toString )
          Ok(( x.toInt + 1).toString )
          //setKey(x + "|")
          //Ok(x + "|")
        }
        case None => {
          setKey("55555",0.toString )
          Ok( 0.toString )
          //setKey("|")
          //Ok("|")
        }

      }

    )
  }


}
