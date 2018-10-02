package models

import play.api.libs.json._
import play.api.libs.functional.syntax._


case class Input(mcc: Option[String],
                 cvv: Option[String])


object Input {

  def apply(mcc: Option[String], cvv: Option[String]): Input = {
    new Input(mcc, cvv)
  }

  implicit val inputReads: Reads[Input] = (
    (JsPath \ "mcc").readNullable[String] and
      (JsPath \ "cvv").readNullable[String]
    )(Input.apply _)

}
