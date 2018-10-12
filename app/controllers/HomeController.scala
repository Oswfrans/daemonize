package controllers

import javax.inject._
import ml.combust.bundle.BundleFile
import ml.combust.mleap.runtime.frame.DefaultLeapFrame
import ml.combust.mleap.runtime.MleapSupport._
import models.{OriginalTransaction, ModifiedTransaction}
import play.api.libs.json._
import play.api.mvc._
import resource._


/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def index(): Action[JsValue] = Action(parse.tolerantJson) { implicit request: Request[JsValue] =>
    request.body.validate[OriginalTransaction] match {
      case success: JsSuccess[OriginalTransaction] =>
        val mlModelPath = this.getClass.getClassLoader.getResource("ml-model.zip").getPath
        val zipBundleM = (for(bundle <- managed(BundleFile(s"jar:file:$mlModelPath"))) yield {
          bundle.loadMleapBundle().get
        }).opt.get
        val mleapPipeline = zipBundleM.root

        val input = success.value
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
