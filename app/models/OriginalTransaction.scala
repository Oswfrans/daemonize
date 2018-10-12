package models

import ml.combust.mleap.core.types.{ScalarType, StructField, StructType}
import ml.combust.mleap.runtime.frame.Row
import play.api.libs.json._
import play.api.libs.functional.syntax._


case class OriginalTransaction(firstsixdigits: String,
                               cvvresponse: Option[String],
                               internalamount: Double,
                               mid: String)


object OriginalTransaction {

  implicit val inputReads: Reads[OriginalTransaction] = (
    (JsPath \ "firstsixdigits").read[String] and
      (JsPath \ "cvvresponse").readNullable[String] and
      (JsPath \ "internalamount").read[Double] and
      (JsPath \ "mid").read[String]
    )(OriginalTransaction.apply _)

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
    StructField("rank", ScalarType.String),
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

  def toRow(origTrx: OriginalTransaction): Row = {
    Row(
      "1",                        // approvalcode
      "1",                        // originalcurrencyid
      origTrx.internalamount,     // internalamount
      "1",                        // authdate
      "1",                        // authtimestamp
      "1",                        // authresult
      "1",                        // transactiontypeid
      "1",                        // cardid
      "1",                        // merchantaccountid
      "1",                        // memberid
      "1",                        // transactionoriginatorid
      "1",                        // detailedcode
      origTrx.firstsixdigits,     // firstsixdigits
      origTrx.cvvresponse,        // cvvresponse
      "1",                        // authorizationtypeid
      "1",                        // channel
      "1",                        // channelsubtype
      "1",                        // initialrecurring
      "1",                        // merchantcountrycode
      "1",                        // originalauthenticationindicator
      "1",                        // authenticationvalue
      origTrx.mid,                // mid
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      "1",
      1.0
    )
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
