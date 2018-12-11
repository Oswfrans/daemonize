package controllers

import javax.inject._
import ml.combust.mleap.runtime.frame.{DefaultLeapFrame, Transformer}
import models.{ModifiedTransaction, OriginalTransaction}
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

        //Feature Expansio

        //Candidate Generation

        //ETC

        val frame = DefaultLeapFrame(OriginalTransaction.schema, Seq(OriginalTransaction.toRow(input)))
        val transform = mleapPipeline.transform(frame).get
        val result = transform.dataset.head

        Ok(Json.toJson(ModifiedTransaction(result))).as(JSON)

      case JsError(_) => BadRequest("Invalid Input!")
    }
  }

  def healthz() = Action {
    Ok("Healthy")
  }

}
