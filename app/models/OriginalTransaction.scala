package models

import ml.combust.mleap.core.types.{ScalarType, StructField, StructType}
import ml.combust.mleap.runtime.frame.Row

import play.api.libs.json._
import play.api.libs.functional.syntax._

import scala.concurrent.Future
import scala.util.{Try,Success,Failure}

//import scala.concurrent.ExecutionContext.Implicits.global

//possible solution, probably not
//import ai.x.play.json._
//import ai.x.play.json.Jsonx
//import julienrf.json.derived

case class OriginalCard( expirymonth: Double,
                         expiryyear: Double,
                         cv2resulttype: String, //
                         cv2response: String, //
                         avsthere: String,

                         bin: String,
                         holderip : String,

                         cardbrandbindetail : String,
                         issuercountrycodebindetail : String,
                         issuercodebindetail : String,
                         cardsubtypeidbindetail : String,
                         issuertypeidbindetail : String,

                         cardschemaid: String,
                         servicetypeid: String,
                         cardcommercial: String,
                         cardprepaid: String,
                         issuercode: String,

                         productcode : String,
                         productsubcode : String,
                         brandcode : String,

                         issuercountrycode: String)

case class OriginalMerchant(merchantcountrycode: String,
                            processorid: String,
                            mcc: String,
                            mid: String ) //Memberid ??)

case class OriginalInfo(currentrank: Double,
                        previousresponsecode: String,
                        //previousretryoptimizations, as what should we represent this?
                        authorizationtype : String,
                        channeltype: String,
                        channelsubtype : String,
                        transactionoriginatorid : String,
                        credonfile: String,
                        dwo : String,
                        walletprovider: String,
                        optchannel : String,
                        optremovethreed: String,

                        internalamount: Double,
                        initialrecurring: String,
                        authdatetime: String,
                        transactiontypeid: String,
                        channel: String,
                        eci: String,
                        currencyid: String)

case class OriginalTransaction(card: OriginalCard,
                               merchant: OriginalMerchant,
                               info: OriginalInfo)

object OriginalTransaction {

  implicit val cardFormat: Format[OriginalCard] = //Json.format[OriginalCard]
    (
      (JsPath \ "OriginalTransaction" \ "Card" \ "ExpiryMonth").format[Double] and
        (JsPath \ "OriginalTransaction" \ "Card" \ "ExpiryYear").format[Double] and
        (JsPath \ "OriginalTransaction" \ "Card" \ "Cv2ResultType").format[String] and
        (JsPath \ "OriginalTransaction" \ "Card" \ "Cv2Reponse").format[String] and
        (JsPath \ "OriginalTransaction" \ "Card" \ "AvsThere").format[String] and

        (JsPath \ "OriginalTransaction" \ "Card" \ "Bin").format[String] and
        (JsPath \ "OriginalTransaction" \ "Card" \ "HolderIp").format[String] and

        (JsPath \ "OriginalTransaction" \ "Card" \ "BinDetail" \ "CardBrand").format[String] and
        (JsPath \ "OriginalTransaction" \ "Card" \ "BinDetail" \ "IssuerCountryCode").format[String] and
        (JsPath \ "OriginalTransaction" \ "Card" \ "BinDetail" \ "IssuerCode").format[String] and
        (JsPath \ "OriginalTransaction" \ "Card" \ "BinDetail" \ "CardSubtypeId").format[String] and //maybe need to change to SubtypeId
        (JsPath \ "OriginalTransaction" \ "Card" \ "BinDetail" \ "IssuerTypeId").format[String] and


        (JsPath \ "OriginalTransaction" \ "Card" \ "BinInfo" \ "CardSchemaId").format[String] and
        (JsPath \ "OriginalTransaction" \ "Card" \ "BinInfo" \ "ServiceTypeId").format[String] and
        (JsPath \ "OriginalTransaction" \ "Card" \ "BinInfo" \ "IsCommercial").format[String] and
        (JsPath \ "OriginalTransaction" \ "Card" \ "BinInfo" \ "IsPrepaid").format[String] and
        (JsPath \ "OriginalTransaction" \ "Card" \ "BinInfo" \ "IsPrivateLabel").format[String] and
        (JsPath \ "OriginalTransaction" \ "Card" \ "BinInfo" \ "IssuerCode").format[String] and

        (JsPath \ "OriginalTransaction" \ "Card" \ "BinInfo" \ "ProductCode").format[String] and
        (JsPath \ "OriginalTransaction" \ "Card" \ "BinInfo" \ "ProductSubCode").format[String] and
        (JsPath \ "OriginalTransaction" \ "Card" \ "BinInfo" \ "BrandCode").format[String] and

        (JsPath \ "OriginalTransaction" \ "Card" \ "BinInfo" \ "CountryCode").format[String]
      ) (OriginalCard.apply, unlift(OriginalCard.unapply))

  implicit val merchantFormat: Format[OriginalMerchant] = //Json.format[OriginalMerchant]
    (
      (JsPath \ "OriginalTransaction" \ "Merchant" \ "CountryCode").format[String] and
        (JsPath \ "OriginalTransaction" \ "Merchant" \ "ProcesssorId").format[String] and
        (JsPath \ "OriginalTransaction" \ "Merchant" \ "CategoryCodeGroup").format[String] and
        (JsPath \ "OriginalTransaction" \ "Merchant" \ "MemberId").format[String]
      ) (OriginalMerchant.apply, unlift(OriginalMerchant.unapply))

  implicit val infoFormat: Format[OriginalInfo] = //Json.format[OriginalInfo]
    (
      (JsPath \ "Retry" \ "CurrentRank").format[Double] and
        (JsPath \ "Retry" \ "PreviousResponseCode").format[String] and

        (JsPath \ "EffectiveValues" \ "AuthorizationType").format[String] and
        (JsPath \ "EffectiveValues" \ "Channeltype").format[String] and
        (JsPath \ "EffectiveValues" \ "ChannelSubtype").format[String] and

        (JsPath \ "TransactionOriginatorId" ).format[String] and

        (JsPath \ "EffectiveValues" \ "CredentialOnFileType").format[String] and
        (JsPath \ "EffectiveValues" \ "DwoIndicator").format[String] and
        (JsPath \ "EffectiveValues" \ "WalletProvider").format[String] and

        (JsPath \ "Retry" \ "PreviousRetryOptimization" \ "Optimizations" \ "channel" ).format[String] and  //need to think what to do if this is not here?
        (JsPath \ "Retry" \ "PreviousRetryOptimization" \  "Optimizations" \ "removeThreeD" ).format[String] and //need to think what to do if this is not here?

        (JsPath \ "OriginalTransaction" \ "InternalAmount").format[Double] and
        (JsPath \ "OriginalTransaction" \ "InitialRecurring").format[String] and
        (JsPath \ "OriginalTransaction" \ "AuthDateTime").format[String] and
        (JsPath \ "OriginalTransaction" \ "TransactionTypeId").format[String] and
        (JsPath \ "OriginalTransaction" \ "Channel").format[String] and
        (JsPath \ "OriginalTransaction" \ "Eci").format[String] and
        (JsPath \ "OriginalTransaction" \ "CurrencyId").format[String]
      ) (OriginalInfo.apply, unlift(OriginalInfo.unapply))

  implicit val inputForm: Format[OriginalTransaction] = (
    (JsPath).format[OriginalCard] and
      (JsPath).format[OriginalMerchant] and
      (JsPath).format[OriginalInfo]
    ) (OriginalTransaction.apply, unlift(OriginalTransaction.unapply))

  val schema: StructType = StructType(
    StructField("approvalcode", ScalarType.String),
    StructField("originalcurrencyid", ScalarType.String),
    StructField("internalamount", ScalarType.Double),
    StructField("authdate", ScalarType.String),
    StructField("authtimestamp", ScalarType.String),
    StructField("authresult", ScalarType.String),
    StructField("transactiontypeid", ScalarType.String),
    StructField("cardid", ScalarType.String),
    StructField("merchantaccountid", ScalarType.String),
    StructField("memberid", ScalarType.String),
    StructField("transactionoriginatorid", ScalarType.String),
    StructField("detailedcode", ScalarType.String),
    StructField("firstsixdigits", ScalarType.String),
    StructField("cvvresponse", ScalarType.String),
    StructField("authorizationtypeid", ScalarType.String),
    StructField("channel", ScalarType.String),
    StructField("channelsubtype", ScalarType.String),
    StructField("initialrecurring", ScalarType.String),
    StructField("merchantcountrycode", ScalarType.String),
    StructField("originalauthenticationindicator", ScalarType.String),
    StructField("authenticationvalue", ScalarType.String),
    StructField("mid", ScalarType.String),
    StructField("processorid", ScalarType.String),
    StructField("categorycodegroup", ScalarType.String),
    StructField("cardbrand", ScalarType.String),
    StructField("issuercountrycode", ScalarType.String),
    StructField("issuercode", ScalarType.String),
    StructField("cardusageid", ScalarType.String),
    StructField("cardsubtypeid", ScalarType.String),
    StructField("issuertypeid", ScalarType.String),
    StructField("cardcommercial", ScalarType.String),
    StructField("cardprepaid", ScalarType.String),
    StructField("originalbin", ScalarType.String),
    StructField("authweekofyear", ScalarType.String),
    StructField("authhour", ScalarType.String),
    StructField("retryoptimization", ScalarType.String),
    StructField("cardschemaname", ScalarType.String),
    StructField("servicetypename", ScalarType.String),
    StructField("bininfoissuercode", ScalarType.String),
    StructField("countryname", ScalarType.String),
    StructField("bininfoproductcode", ScalarType.String),
    StructField("bininfoproductsubcode", ScalarType.String),
    StructField("productdescription", ScalarType.String),
    StructField("bininfobrandcode", ScalarType.String),
    StructField("bininfocardschemaid", ScalarType.String),
    StructField("bininfoservicetypeid", ScalarType.String),
    StructField("bininfocountrycode", ScalarType.String),
    StructField("vertical", ScalarType.String),
    StructField("authyear", ScalarType.String),
    StructField("authmonth", ScalarType.String),
    StructField("authday", ScalarType.String),
    StructField("threeD", ScalarType.String),
    StructField("cardexpirydate", ScalarType.String),
    StructField("succeeded", ScalarType.String),
    StructField("succeededrank", ScalarType.String),
    StructField("rank", ScalarType.Double), //ScalarType.String),
    StructField("respcodeprevious", ScalarType.String),
    StructField("cv2resultprevious", ScalarType.String),
    StructField("issuerprevious", ScalarType.String),
    StructField("threedprevious", ScalarType.String),
    StructField("channelprevious", ScalarType.String),
    StructField("channelsubtypeprevious", ScalarType.String),
    StructField("transactiontypeidprevious", ScalarType.String),
    StructField("authorizationtypeidprevious", ScalarType.String),
    StructField("processoridprevious", ScalarType.String),
    StructField("categorycodegroupprevious", ScalarType.String),
    StructField("channelsubtypefirst", ScalarType.String),
    StructField("processoridfirst", ScalarType.String),
    StructField("avstherefirst", ScalarType.String),
    StructField("threedtherefirst", ScalarType.String),
    StructField("respcodefirst", ScalarType.String),
    StructField("firstdate", ScalarType.String),
    StructField("issuerfirst", ScalarType.String),
    StructField("threedfirst", ScalarType.String),
    StructField("channelfirst", ScalarType.String),
    StructField("transactiontypeidfirst", ScalarType.String),
    StructField("authorizationtypeidfirst", ScalarType.String),
    StructField("categorycodegroupfirst", ScalarType.String),
    StructField("cv2resultfirst", ScalarType.String),
    StructField("avsthere", ScalarType.String),
    StructField("cv2there", ScalarType.String),
    StructField("expthere", ScalarType.String),
    StructField("threedthere", ScalarType.String),
    StructField("threedtherechange", ScalarType.String),
    StructField("cv2change", ScalarType.String),
    StructField("authdatesecondsdiff", ScalarType.String),
    StructField("issuerchange", ScalarType.String),
    StructField("threedchange", ScalarType.String),
    StructField("categorycodegroupchange", ScalarType.String),
    StructField("avstherechange", ScalarType.String),
    StructField("authorizationtypeidchange", ScalarType.String),
    StructField("processoridchange", ScalarType.String),
    StructField("channelsubtypechange", ScalarType.String),
    StructField("channelchange", ScalarType.String),
    StructField("doublelabel", ScalarType.Double)
  ).get


  //very happy with the current implementation that takes one Seq and then adepts it :)
  //will need to adept this code after talking to Jorge

  def toRow(origTrx: OriginalTransaction, frameType: Int): Row = {
    var ogRowSeq = Seq("1", // approvalcode
      origTrx.info.currencyid, // originalcurrencyid
      origTrx.info.internalamount, // internalamount
      origTrx.info.authdatetime.substring(0, 10), // authdate
      "1", // authtimestamp
      "1", // authresult
      origTrx.info.transactiontypeid, // transactiontypeid
      "1", // cardid
      "1", // merchantaccountid
      "1", // memberid
      "1", // transactionoriginatorid
      "1", // detailedcode
      "1", // firstsixdigits
      origTrx.card.cv2response, // cvvresponse
      "1", // authorizationtypeid
      origTrx.info.channel, // channel
      "1", // channelsubtype
      origTrx.info.initialrecurring, // initialrecurring
      origTrx.merchant.merchantcountrycode, // merchantcountrycode
      "1", // originalauthenticationindicator
      "1", // authenticationvalue
      origTrx.merchant.mid, // mid
      "1", // processorid
      origTrx.merchant.mcc, // categorycodegroup
      origTrx.card.cardbrandbindetail, // cardbrand
      origTrx.card.issuercodebindetail, // issuercountrycode , is this the bin detail info?
      origTrx.card.issuercodebindetail, // issuercode
      "1", // cardusageid
      origTrx.card.cardsubtypeidbindetail, // cardsubtypeid
      origTrx.card.issuertypeidbindetail, // issuertypeid
      origTrx.card.cardcommercial, // cardcommercial
      origTrx.card.cardprepaid, // cardprepaid
      "1", // originalbin
      "1", // authweekofyear
      origTrx.info.authdatetime.substring(11, 13), // authhour
      "1", // retryoptimization , need to parse the JSON better
      "1", // cardschemaname
      "1", // servicetypename
      origTrx.card.issuercode, // bininfoissuercode
      "1", // countryname
      origTrx.card.productcode, // bininfoproductcode
      origTrx.card.productsubcode, // bininfoproductsubcode
      "1", // productdescription
      origTrx.card.brandcode, // bininfobrandcode
      origTrx.card.cardschemaid, // bininfocardschemaid
      origTrx.card.servicetypeid, // bininfoservicetypeid
      origTrx.card.issuercountrycode, // bininfocountrycode
      "1", // vertical
      origTrx.info.authdatetime.substring(0, 4), // authyear
      origTrx.info.authdatetime.substring(5, 7), // authmonth
      origTrx.info.authdatetime.substring(8, 10), // authday
      "1", // threeD
      "1", //origTrx.card.expiryyear.toString.concat("-").concat(origTrx.card.expirymonth.toString) ,   // cardexpirydate NEED to asses proper format
      "1", // succeeded
      "1", // succeededrank
      origTrx.info.currentrank, // rank
      origTrx.info.previousresponsecode, // respcodeprevious
      "1", // cv2resultprevious
      "1", // issuerprevious
      "1", // threedprevious
      "1", // channelprevious
      "1", // channelsubtypeprevious
      "1", // transactiontypeidprevious
      "1", // authorizationtypeidprevious
      "1", // processoridprevious
      "1", // categorycodegroupprevious
      "1", // channelsubtypefirst
      "1", // processoridfirst
      "1", // avstherefirst
      "1", // threedtherefirst
      "1", // respcodefirst
      "1", // firstdate
      "1", // issuerfirst
      "1", // threedfirst
      "1", // channelfirst
      "1", // transactiontypeidfirst
      "1", // authorizationtypeidfirst
      "1", // categorycodegroupfirst
      "1", // cv2resultfirst
      origTrx.card.avsthere, // avsthere
      "1", // cv2there
      "1", // expthere
      "1", // threedthere
      "1", // threedtherechange
      "1", // cv2change
      "1", // authdatesecondsdiff
      "1", // issuerchange
      "1", // threedchange
      "1", // categorycodegroupchange
      "1", // avstherechange
      "1", // authorizationtypeidchange
      "1", // processoridchange
      "1", // channelsubtypechange
      "1", // channelchange
      1.0
    )
    frameType match {
      case 0 =>
        Row(ogRowSeq: _*)
      // 1 channel moto (channel 2, channelchange 1 and threed 0 and threedchange 1 )
      // 2 remove threed (threed 0 and threedchange 1)
      case 1 =>
        //side effects hmmm
        ogRowSeq = ogRowSeq.updated(15, "2")  //ogRowSeq(15) = 2 //channel
        ogRowSeq = ogRowSeq.updated(93, "1")  //ogRowSeq(93) = 1 //channelchange
        ogRowSeq = ogRowSeq.updated(51, "0")  //ogRowSeq(51) = 0 //threed
        ogRowSeq = ogRowSeq.updated(87, "1") //ogRowSeq(87) = 1 //threedchange
        Row(ogRowSeq: _*)

      case 2 =>
        ogRowSeq = ogRowSeq.updated(51, "0")  //ogRowSeq(51) = 0 //threed
        ogRowSeq = ogRowSeq.updated(87, "1") //ogRowSeq(87) = 1 //threedchange
        Row(ogRowSeq: _*)
    }
  }

  //we define the KV method here
  //for now this a testMethod, will need to be adapted afte we have a new API contract and new models

  def toRowKV(origTrx: OriginalTransaction, frameType: Int, kvArray: Array[String] ): Row = { //Array[String]
    var ogRowSeq = Seq("1", // approvalcode
      origTrx.info.currencyid, // originalcurrencyid
      origTrx.info.internalamount, // internalamount
      origTrx.info.authdatetime.substring(0, 10), // authdate
      "1", // authtimestamp
      "1", // authresult
      origTrx.info.transactiontypeid, // transactiontypeid
      "1", // cardid
      "1", // merchantaccountid
      "1", // memberid
      "1", // transactionoriginatorid
      "1", // detailedcode
      "1", // firstsixdigits
      origTrx.card.cv2response, // cvvresponse
      "1", // authorizationtypeid
      origTrx.info.channel, // channel
      "1", // channelsubtype
      origTrx.info.initialrecurring, // initialrecurring
      origTrx.merchant.merchantcountrycode, // merchantcountrycode
      "1", // originalauthenticationindicator
      "1", // authenticationvalue
      origTrx.merchant.mid, // mid
      "1", // processorid
      origTrx.merchant.mcc, // categorycodegroup
      origTrx.card.cardbrandbindetail, // cardbrand
      origTrx.card.issuercodebindetail, // issuercountrycode , is this the bin detail info?
      origTrx.card.issuercodebindetail, // issuercode
      "1", // cardusageid
      origTrx.card.cardsubtypeidbindetail, // cardsubtypeid
      origTrx.card.issuertypeidbindetail, // issuertypeid
      origTrx.card.cardcommercial, // cardcommercial
      origTrx.card.cardprepaid, // cardprepaid
      "1", // originalbin
      "1", // authweekofyear
      origTrx.info.authdatetime.substring(11, 13), // authhour
      "1", // retryoptimization , need to parse the JSON better
      "1", // cardschemaname
      "1", // servicetypename
      origTrx.card.issuercode, // bininfoissuercode
      "1", // countryname
      origTrx.card.productcode, // bininfoproductcode
      origTrx.card.productsubcode, // bininfoproductsubcode
      "1", // productdescription
      origTrx.card.brandcode, // bininfobrandcode
      origTrx.card.cardschemaid, // bininfocardschemaid
      origTrx.card.servicetypeid, // bininfoservicetypeid
      origTrx.card.issuercountrycode, // bininfocountrycode
      "1", // vertical
      origTrx.info.authdatetime.substring(0, 4), // authyear
      origTrx.info.authdatetime.substring(5, 7), // authmonth
      origTrx.info.authdatetime.substring(8, 10), // authday
      "1", // threeD
      "1", //origTrx.card.expiryyear.toString.concat("-").concat(origTrx.card.expirymonth.toString) ,   // cardexpirydate NEED to asses proper format
      "1", // succeeded
      "1", // succeededrank
      origTrx.info.currentrank, // rank
      origTrx.info.previousresponsecode, // respcodeprevious
      "1", // cv2resultprevious
      kvArray(0), //kvArray(0) onComplete {case Success(x) => x case Failure(z) => "1"}, //kvArray(0), // issuerprevious
      kvArray(1),//kvArray(1) onComplete {case Success(x) => x case Failure(z) => "1"}, //kvArray(1), // threedprevious
      kvArray(2),//kvArray(2) onComplete {case Success(x) => x case Failure(z) => "1"}, //kvArray(2), // channelprevious
      kvArray(3),//kvArray(3) onComplete {case Success(x) => x case Failure(z) => "1"}, //kvArray(3), // channelsubtypeprevious
      "1", // transactiontypeidprevious
      "1", // authorizationtypeidprevious
      "1", // processoridprevious
      "1", // categorycodegroupprevious
      "1", // channelsubtypefirst
      "1", // processoridfirst
      "1", // avstherefirst
      "1", // threedtherefirst
      "1", // respcodefirst
      "1", // firstdate
      "1", // issuerfirst
      "1", // threedfirst
      "1", // channelfirst
      "1", // transactiontypeidfirst
      "1", // authorizationtypeidfirst
      "1", // categorycodegroupfirst
      "1", // cv2resultfirst
      origTrx.card.avsthere, // avsthere
      "1", // cv2there
      "1", // expthere
      "1", // threedthere
      "1", // threedtherechange
      "1", // cv2change
      "1", // authdatesecondsdiff
      "1", // issuerchange
      "1", // threedchange
      "1", // categorycodegroupchange
      "1", // avstherechange
      "1", // authorizationtypeidchange
      "1", // processoridchange
      "1", // channelsubtypechange
      "1", // channelchange
      1.0
    )
    frameType match {
      case 0 =>
        Row(ogRowSeq: _*)
      // 1 channel moto (channel 2, channelchange 1 and threed 0 and threedchange 1 )
      // 2 remove threed (threed 0 and threedchange 1)
      case 1 =>
        //side effects hmmm
        ogRowSeq = ogRowSeq.updated(15, "2")  //ogRowSeq(15) = 2 //channel
        ogRowSeq = ogRowSeq.updated(93, "1")  //ogRowSeq(93) = 1 //channelchange
        ogRowSeq = ogRowSeq.updated(51, "0")  //ogRowSeq(51) = 0 //threed
        ogRowSeq = ogRowSeq.updated(87, "1") //ogRowSeq(87) = 1 //threedchange
        Row(ogRowSeq: _*)

      case 2 =>
        ogRowSeq = ogRowSeq.updated(51, "0")  //ogRowSeq(51) = 0 //threed
        ogRowSeq = ogRowSeq.updated(87, "1") //ogRowSeq(87) = 1 //threedchange
        Row(ogRowSeq: _*)
    }
  }
}
/*
Input model in MLeap

0 StructField(approvalcode,StringType,true)
1 StructField(originalcurrencyid,StringType,true)
2 StructField(internalamount,DecimalType(19,6),true)
3 StructField(authdate,StringType,true)
4 StructField(authtimestamp,StringType,true)
5 StructField(authresult,IntegerType,true)
6 StructField(transactiontypeid,StringType,true)
7 StructField(cardid,StringType,true)
8 StructField(merchantaccountid,StringType,true)
9 StructField(memberid,StringType,true)
10 StructField(transactionoriginatorid,StringType,true)
11 StructField(detailedcode,StringType,true)
12 StructField(firstsixdigits,StringType,true)
13 StructField(cvvresponse,StringType,true)
14 StructField(authorizationtypeid,StringType,true)
15 StructField(channel,StringType,true)
16 StructField(channelsubtype,StringType,true)
17 StructField(initialrecurring,StringType,true)
18 StructField(merchantcountrycode,StringType,true)
19 StructField(originalauthenticationindicator,StringType,true)
20 StructField(authenticationvalue,StringType,true)
21 StructField(mid,StringType,true)
22 StructField(processorid,StringType,true)
23 StructField(categorycodegroup,StringType,true)
24 StructField(cardbrand,StringType,true)
25 StructField(issuercountrycode,StringType,true)
26 StructField(issuercode,StringType,true)
27 StructField(cardusageid,StringType,true)
28 StructField(cardsubtypeid,StringType,true)
29 StructField(issuertypeid,StringType,true)
30 StructField(cardcommercial,StringType,true)
31 StructField(cardprepaid,StringType,true)
32 StructField(originalbin,StringType,true)
33 StructField(authweekofyear,IntegerType,true)
34 StructField(authhour,IntegerType,true)
35 StructField(retryoptimization,StringType,true)
36 StructField(cardschemaname,StringType,true)
37 StructField(servicetypename,StringType,true)
38 StructField(bininfoissuercode,StringType,true)
39 StructField(countryname,StringType,true)
40 StructField(bininfoproductcode,StringType,true)
41 StructField(bininfoproductsubcode,StringType,true)
42 StructField(productdescription,StringType,true)
43 StructField(bininfobrandcode,StringType,true)
44 StructField(bininfocardschemaid,StringType,true)
45 StructField(bininfoservicetypeid,StringType,true)
46 StructField(bininfocountrycode,StringType,true)
47 StructField(vertical,StringType,true)
48 StructField(authyear,IntegerType,true)
49 StructField(authmonth,IntegerType,true)
50 StructField(authday,IntegerType,true)
51 StructField(threeD,StringType,true)
52 StructField(cardexpirydate,StringType,true)
53 StructField(succeeded,StringType,true)
54 StructField(succeededrank,StringType,true)
55 StructField(rank,IntegerType,true)
56 StructField(respcodeprevious,StringType,true)
57 StructField(cv2resultprevious,StringType,true)
58 StructField(issuerprevious,StringType,true)
59 StructField(threedprevious,StringType,true)
60 StructField(channelprevious,StringType,true)
61 StructField(channelsubtypeprevious,StringType,true)
62 StructField(transactiontypeidprevious,StringType,true)
63 StructField(authorizationtypeidprevious,StringType,true)
64 StructField(processoridprevious,StringType,true)
65 StructField(categorycodegroupprevious,StringType,true)
66 StructField(channelsubtypefirst,StringType,true)
67 StructField(processoridfirst,StringType,true)
68 StructField(avstherefirst,StringType,true)
69 StructField(threedtherefirst,StringType,true)
70 StructField(respcodefirst,StringType,true)
71 StructField(firstdate,StringType,true)
72 StructField(issuerfirst,StringType,true)
73 StructField(threedfirst,StringType,true)
74 StructField(channelfirst,StringType,true)
75 StructField(transactiontypeidfirst,StringType,true)
76 StructField(authorizationtypeidfirst,StringType,true)
77 StructField(categorycodegroupfirst,StringType,true)
78 StructField(cv2resultfirst,StringType,true)
79 StructField(avsthere,StringType,true)
80 StructField(cv2there,StringType,true)
81 StructField(expthere,StringType,true)
82 StructField(threedthere,StringType,true)
83 StructField(threedtherechange,StringType,true)
84 StructField(cv2change,StringType,true)
85 StructField(authdatesecondsdiff,IntegerType,true)
86 StructField(issuerchange,StringType,true)
87 StructField(threedchange,StringType,true)
88 StructField(categorycodegroupchange,StringType,true)
89 StructField(avstherechange,StringType,true)
90 StructField(authorizationtypeidchange,StringType,true)
91 StructField(processoridchange,StringType,true)
92 StructField(channelsubtypechange,StringType,true)
93 StructField(channelchange,StringType,true)
94 StructField(doublelabel,DoubleType,false)
 */
