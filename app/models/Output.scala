package models

import play.api.libs.json._
import play.api.libs.functional.syntax._


case class Output(continue: Boolean,
                  mcc: Option[String],
                  cvv: Option[String])


object Output {

  implicit val outputWrites: Writes[Output] = (
    (JsPath \ "continue").write[Boolean] and
      (JsPath \ "mcc").writeNullable[String] and
      (JsPath \ "cvv").writeNullable[String]
    )(unlift(Output.unapply))

}
