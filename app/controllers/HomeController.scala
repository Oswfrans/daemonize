package controllers

import javax.inject._
import models.{Input, Output}
import play.api.libs.json._
import play.api.mvc._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def index(): Action[JsValue] = Action(parse.tolerantJson) { implicit request: Request[JsValue] =>
    request.body.validate[Input] match {
      case success: JsSuccess[Input] =>
        val input = success.value

        if (input.mcc.nonEmpty || input.cvv.nonEmpty) {
          val json = Json.toJson(Output(continue = true, input.mcc, input.cvv))
          Ok(json).as(JSON)
        } else {
          val json = Json.toJson(Output(continue = false, None, None))
          Ok(json).as(JSON)
        }

      case JsError(_) => BadRequest("Invalid Input!")
    }
  }
}
