package models

import ml.combust.mleap.core.types.{ScalarType, StructField, StructType}
import ml.combust.mleap.runtime.frame.Row

import play.api.libs.json._
import play.api.libs.functional.syntax._

import scala.concurrent.Future
import scala.util.{Try,Success,Failure}

case class IDInformation(requestid: String,
                         sessionid: String)

object IDvalues {

  implicit val idFormat: Format[IDInformation] =
    (
      (JsPath \ "OriginalTransaction" \ "RequestCorrelationId" ).format[String] and
        (JsPath \ "OriginalTransaction" \ "SessionCorrelationId").format[String]
      ) (IDInformation.apply, unlift(IDInformation.unapply))


  def toRow(idV: IDvalues): Row = {
    var ogRowSeq = Seq(
      idV.requestid , // originalcurrencyid
      idV.sessionid // internalamount
    )
      Row(ogRowSeq: _*)
  }
}
