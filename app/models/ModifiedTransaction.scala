package models

import ml.combust.mleap.runtime.frame.Row
import play.api.libs.json._
import play.api.libs.functional.syntax._


//should change the name of this to something like apiResponse

case class ModifiedTransaction(continue: Boolean,
                               mcccode: String,
                               channel: String,
                               channelname: String,
                               probability: Double,
                               prediction: Double)


object ModifiedTransaction {

  implicit val outputWrites: Writes[ModifiedTransaction] = (
    (JsPath \ "continue").write[Boolean] and
      (JsPath \ "mcccode").write[String] and
      (JsPath \ "channel").write[String] and
      (JsPath \ "channelname").write[String] and
      (JsPath \ "probability").write[Double] and
      (JsPath \ "prediction").write[Double]
    )(unlift(ModifiedTransaction.unapply))

  def apply(row: Row): ModifiedTransaction = {
    val continue = true
    val mccCode = "7995" // row.getString(23)
    val channel = "2"//row.getString(15)
    val channelname = "MOTO"//row.getString(15)
    val probability = row.getTensor[Double](182).rawValues.head
    val prediction = row.getDouble(183)
    new ModifiedTransaction(continue, mccCode, channel,channelname, probability, prediction)
  }
}



/*
Output model in MLeap

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
95 StructField(threedthereindex,DoubleType,false)
96 StructField(threedtherechangeindex,DoubleType,false)
97 StructField(transactiontypeidindex,DoubleType,false)
98 StructField(transactionoriginatoridindex,DoubleType,false)
99 StructField(channelindex,DoubleType,false)
100 StructField(channelsubtypeindex,DoubleType,false)
101 StructField(initialrecurringindex,DoubleType,false)
102 StructField(originalauthenticationindicatorindex,DoubleType,false)
103 StructField(cardbrandindex,DoubleType,false)
104 StructField(issuercountrycodeindex,DoubleType,false)
105 StructField(issuertypeidindex,DoubleType,false)
106 StructField(respcodepreviousindex,DoubleType,false)
107 StructField(cv2resultpreviousindex,DoubleType,false)
108 StructField(channelpreviousindex,DoubleType,false)
109 StructField(channelsubtypepreviousindex,DoubleType,false)
110 StructField(categorycodegrouppreviousindex,DoubleType,false)
111 StructField(processoridpreviousindex,DoubleType,false)
112 StructField(channelsubtypefirstindex,DoubleType,false)
113 StructField(processoridfirstindex,DoubleType,false)
114 StructField(avstherefirstindex,DoubleType,false)
115 StructField(cv2resultfirstindex,DoubleType,false)
116 StructField(respcodefirstindex,DoubleType,false)
117 StructField(channelfirstindex,DoubleType,false)
118 StructField(authorizationtypeidfirstindex,DoubleType,false)
119 StructField(categorycodegroupfirstindex,DoubleType,false)
120 StructField(avsthereindex,DoubleType,false)
121 StructField(cv2thereindex,DoubleType,false)
122 StructField(cv2changeindex,DoubleType,false)
123 StructField(avstherechangeindex,DoubleType,false)
124 StructField(processoridchangeindex,DoubleType,false)
125 StructField(categorycodegroupchangeindex,DoubleType,false)
126 StructField(channelsubtypechangeindex,DoubleType,false)
127 StructField(channelchangeindex,DoubleType,false)
128 StructField(retryoptimizationindex,DoubleType,false)
129 StructField(merchantcountrycodeindex,DoubleType,false)
130 StructField(bininfocardschemaidindex,DoubleType,false)
131 StructField(bininfoservicetypeidindex,DoubleType,false)
132 StructField(bininfoissuercodeindex,DoubleType,false)
133 StructField(bininfocountrycodeindex,DoubleType,false)
134 StructField(bininfoproductcodeindex,DoubleType,false)
135 StructField(bininfoproductsubcodeindex,DoubleType,false)
136 StructField(bininfobrandcodeindex,DoubleType,false)
137 StructField(originalthreedthere,StringType,true)
138 StructField(originalthreedtherechange,StringType,true)
139 StructField(originaltransactiontypeid,StringType,true)
140 StructField(originaltransactionoriginatorid,StringType,true)
141 StructField(originalchannel,StringType,true)
142 StructField(originalchannelsubtype,StringType,true)
143 StructField(originalinitialrecurring,StringType,true)
144 StructField(originaloriginalauthenticationindicator,StringType,true)
145 StructField(originalcardbrand,StringType,true)
146 StructField(originalissuercountrycode,StringType,true)
147 StructField(originalissuertypeid,StringType,true)
148 StructField(originalrespcodeprevious,StringType,true)
149 StructField(originalcv2resultprevious,StringType,true)
150 StructField(originalchannelprevious,StringType,true)
151 StructField(originalchannelsubtypeprevious,StringType,true)
152 StructField(originalcategorycodegroupprevious,StringType,true)
153 StructField(originalprocessoridprevious,StringType,true)
154 StructField(originalchannelsubtypefirst,StringType,true)
155 StructField(originalprocessoridfirst,StringType,true)
156 StructField(originalavstherefirst,StringType,true)
157 StructField(originalcv2resultfirst,StringType,true)
158 StructField(originalrespcodefirst,StringType,true)
159 StructField(originalchannelfirst,StringType,true)
160 StructField(originalauthorizationtypeidfirst,StringType,true)
161 StructField(originalcategorycodegroupfirst,StringType,true)
162 StructField(originalavsthere,StringType,true)
163 StructField(originalcv2there,StringType,true)
164 StructField(originalcv2change,StringType,true)
165 StructField(originalavstherechange,StringType,true)
166 StructField(originalprocessoridchange,StringType,true)
167 StructField(originalcategorycodegroupchange,StringType,true)
168 StructField(originalchannelsubtypechange,StringType,true)
169 StructField(originalchannelchange,StringType,true)
170 StructField(originalretryoptimization,StringType,true)
171 StructField(originalmerchantcountrycode,StringType,true)
172 StructField(originalbininfocardschemaid,StringType,true)
173 StructField(originalbininfoservicetypeid,StringType,true)
174 StructField(originalbininfoissuercode,StringType,true)
175 StructField(originalbininfocountrycode,StringType,true)
176 StructField(originalbininfoproductcode,StringType,true)
177 StructField(originalbininfoproductsubcode,StringType,true)
178 StructField(originalbininfobrandcode,StringType,true)
179 StructField(label,DoubleType,true)
180 StructField(features,org.apache.spark.ml.linalg.VectorUDT@3bfc3ba7,true)
181 StructField(rawPrediction,org.apache.spark.ml.linalg.VectorUDT@3bfc3ba7,true)
182 StructField(probability,org.apache.spark.ml.linalg.VectorUDT@3bfc3ba7,true)
183 StructField(prediction,DoubleType,false)
 */