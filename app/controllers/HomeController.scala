package controllers

import javax.inject._
import ml.combust.mleap.runtime.frame.{DefaultLeapFrame, Transformer}
import models.{ApiResponse, OriginalTransaction, ModifiedTransaction}
import play.api.libs.json._
import play.api.mvc._


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

        //generate different candidate transactions here (to write)
        //we also look at currentrank, this cannot be higher than 3
        //we generate threed remove & channelchange // ? if threed is on and channel is not moto (1 )
        //we generate threed remove                 // ? if threed is on and channel is Moto (2)
        //we generate channelchane                  // ? if threed is off and channel is not moto (1)
        //we generate the default transaction if the currentrank is not too high
        //current implentation assumes a certain schema, if this changes we need to change the code
        //if OriginalTransaction.toRow(input).getInt(55) < 3


        val frame = DefaultLeapFrame(OriginalTransaction.schema, Seq(OriginalTransaction.toRow(input)))

        val transform = mleapPipeline.transform(frame).get

        val result = transform.dataset.head
        val predictResultVal = result.getDouble(183) //current model has 183 values, change when we change schema

        //based on the result we get we pass something to modifiedTransaction

        //for now we hardcode
        //1 no change , 2 change channel , 3 remove threed, 4 change channel & change threed
        // if mcc changes happen you will pass 2 parameters
        val changes = 2

          Ok(Json.toJson(ApiResponse(changes))).as(JSON)
        //Ok(Json.toJson(ModifiedTransaction(result))).as(JSON)

      case JsError(_) => BadRequest("Invalid Input!")
    }
  }

  def healthz() = Action {
    Ok("Healthy")
  }

}
