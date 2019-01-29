package controllers

import javax.inject._
import ml.combust.mleap.runtime.frame.{DefaultLeapFrame, Transformer}
import models.{ApiResponse, OriginalTransaction, ModifiedTransaction}
import play.api.libs.json._
import play.api.mvc._

import org.etcd4s.{Etcd4sClientConfig, Etcd4sClient}
import org.etcd4s.implicits._
import org.etcd4s.formats.Formats._
import org.etcd4s.pb.etcdserverpb._

import scala.concurrent.ExecutionContext.Implicits.global


/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents, mleapPipeline: Transformer) extends AbstractController(cc) {

  def index(): Action[JsValue] = Action(parse.tolerantJson) { implicit request: Request[JsValue] =>
    request.body.validate[OriginalTransaction] match {
      case success: JsSuccess[OriginalTransaction] =>
        val input = success.value



        //generate different candidate transactions here
        //we generate the default transaction if the currentrank is not too high
        //current implentation assumes a certain schema, if this changes we need to change the code
        if ( OriginalTransaction.toRow(input, 0).getDouble(55) < 3.0 ) {

          // 0 normal txn
          // 1 channel moto (channel 2, channelchange 1 and hreed 0 and threedchange 1 )
          // 2 remove threed (threed 0 and threedchange 1)

          //if needed I can just ugly create different toRow methods that do the candidate generation

          val frame = DefaultLeapFrame(OriginalTransaction.schema, Seq(OriginalTransaction.toRow(input, 0)))

          //think of a way to encapsulate this. Currently quite ugly, boilerplate

          //edit the row object
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

          resultArray.indexOf(resultArray.max) match {
            case 0 =>
              val changes = 0
              Ok(Json.toJson(ApiResponse(changes))).as(JSON)

            case 1 =>
              val changes = 1
              Ok(Json.toJson(ApiResponse(changes))).as(JSON)

            case 2 =>
              val changes = 2
              Ok(Json.toJson(ApiResponse(changes))).as(JSON)

            //case 3 =>
            // val changes = 3
            //  Ok(Json.toJson(ApiResponse(changes))).as(JSON)
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

  def healthz() = Action {
    Ok("Healthy")
  }

  /*
  def etcd() = Action {
    Ok(currentValue)
  }
*/
}
