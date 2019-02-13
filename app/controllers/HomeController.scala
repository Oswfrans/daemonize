package controllers

import javax.inject._
import ml.combust.mleap.runtime.frame.{DefaultLeapFrame, Transformer}
import models.{ApiResponse, OriginalTransaction, ModifiedTransaction}
import play.api.libs.json._
import play.api.mvc._

import org.etcd4s.Etcd4sClientConfig
import org.etcd4s.Etcd4sClient
import org.etcd4s.services._
import org.etcd4s.implicits._
import org.etcd4s.formats.Formats._
import org.etcd4s.pb.etcdserverpb._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Try,Success,Failure}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.Future

import java.time.{LocalDateTime, ZoneId }
import java.time.format.DateTimeFormatter
import scala.util.control.Breaks._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents, mleapPipeline: Transformer) extends AbstractController(cc) {

  //SF client API's, due to the classes being private wee need to ugly repeat the client definition in all the methods
  //ugh, consider rewriting the lib? Joao would hate it lol
  def getVal( valKey: String) : Future[Option[ String ] ] = {
       val config = Etcd4sClientConfig(
      address = "127.0.0.1",
      port = 2379
    )
    val client = Etcd4sClient.newClient(config)
    //val hello = valKey //"55555"
    val currentValue =  client.kvService.getKey(valKey)
    return currentValue
  }

  def setKey(valKey: String, valSet: String) : Unit = {
    val config = Etcd4sClientConfig(
      address = "127.0.0.1",
      port = 2379
    )
    val client = Etcd4sClient.newClient(config)
    //val hello = "55555"
    client.kvService.setKey(valKey, valSet)
  }

  //this inserts a lot of keys in the docker instance for testing purposes
  def insertKeyz() : Unit = {
    val config = Etcd4sClientConfig(
      address = "127.0.0.1",
      port = 2379
    )
    val client = Etcd4sClient.newClient(config)
    for( i <- 1 to 25000) {
      for (x <- Array("a", "b", "c", "d")) {
        val key = i.toString+x
        val value = (i).toString
        client.kvService.setKey(key, value)
      }
    }
  }

  def index(): Action[JsValue] = Action.async(parse.tolerantJson) { implicit request: Request[JsValue] =>
    request.body.validate[OriginalTransaction] match {
      case success: JsSuccess[OriginalTransaction] =>
        val input = success.value

        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //VERYFY with Jorge that respcode is detailedcode !!!!!!!!!!!!!!!!!!!!!
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        //because of a succesful parse of the api call, we gather data from our KV store
        //for now testcase, later we will need to adapt the leapframe to include the actual kv data

        //generate different candidate transactions here
        //we generate the default transaction if the currentrank is not too high
        //current implentation assumes a certain schema, if this changes we need to change the code

        //the sessionvalues are assumed to be in the following order:
        //respcodeprevious cv2resultprevious issuerprevious  threedprevious  channelprevious channelsubtypeprevious  transactiontypeidprevious authorizationtypeidprevious processoridprevious categorycodegroupprevious channelsubtypefirst processoridfirst  avstherefirst threedtherefirst  respcodefirst firstdate issuerfirst threedfirst channelfirst  transactiontypeidfirst  authorizationtypeidfirst  categorycodegroupfirst  cv2resultfirst  avsthere  cv2there  expthere  threedthere threedtherechange cv2change authdatesecondsdiff issuerchange  threedchange  categorycodegroupchange avstherechange  authorizationtypeidchange processoridchange channelsubtypechange  channelchange

        val fraudCheck = 0
        //Check if rank is low enough
        //check if we do fraud check
        //if fraudCheck we do key stuff for that
        //we then check if we are rank1 or higher
        //if higher
        //if lower
        //if fraudCheck we pass to both models and use KV
        //if not we use default model (now with sessionvalues)

        //hmm, will need to adept method to take the value sfrom the input
        //probably pass some implicit value
        //need to create intermediate df object to not recompute everything
        val initFrame = OriginalTransaction.toRow(input, 0)

        if ( initFrame.getDouble(55) < 3.0 ) {

          //we need to get the values for the keys
          //needs to be changed to something extracted from input!!

          if (fraudCheck==1) {
            val testKey = "10001"
            val keyArray = for (a <- Array("a","b","c", "d")) yield testKey+a

            var keyValArray = for {
              result1 <- getVal( testKey + "a").map( value => value match { case Some(x) => x.toString case None => 0.toString } )
              result2 <- getVal( testKey + "b").map( value => value match { case Some(x) => x.toString case None => 0.toString } )
              result3 <- getVal( testKey + "c").map( value => value match { case Some(x) => x.toString case None => 0.toString } )
              result4 <- getVal( testKey + "d").map( value => value match { case Some(x) => x.toString case None => 0.toString } )
            } yield Array(result1, result2, result3, result4)

            // we set new values for the key
            // needs to be changed to be something derived from input!!!!
            for (x <- keyArray) {
              setKey(x, "7")
            }
          }


          if ( initFrame.getDouble(55) == 1.0 ) {

            //all the first values for the session
            //there check needs to be double checked
            val firstArray =  Array(  initFrame.getString(16), //channelsubtypefirst
              initFrame.getString(22), //processoridfirst
              initFrame.getString(79), //avstherefirst
              if (initFrame.getString(51)=="NULL" || initFrame.getString(51)=="" ) 0 else 1, //threedtherefirst
              initFrame.getString(11), //respcodefirst
              initFrame.getString(3), //firstdate
              initFrame.getString(26), //issuerfirst
              initFrame.getString(51), //threedfirst
              initFrame.getString(15), //channelfirst
              initFrame.getString(62), //transactiontypeidfirst
              initFrame.getString(14), //authorizationtypeidfirst
              initFrame.getString(23), //categorycodegroupfirst
              initFrame.getString(13), //cv2resultfirst
            )

            val thereArray = Array(initFrame.getString(79), //avsthere
              if (initFrame.getString(13)=="NULl" || initFrame.getString(13)=="" ) 0 else 1,, //cv2there
              if (initFrame.getString(52)=="NULL" || initFrame.getString(52)=="" ) 0 else 1,, //expthere
              if (initFrame.getString(51)=="NULL" || initFrame.getString(51)=="" ) 0 else 1, //threedthere
            )

            //all the previous values for the session, so the current session
            //these two will be either discarded or kept blank
            var previousArray = Array()
            //defaultvalues for initial
            //threedtherechange cv2change authdatesecondsdiff issuerchange  threedchange  categorycodegroupchange avstherechange  authorizationtypeidchange processoridchange channelsubtypechange  channelchange
            var changeArray = Array("0","0","0","0","0","0","0","0","0","0","0")

            //this is what we save and pass for follow up sessions

            //should the first entry be detailedcode? corresponding to respcode?????
            var previousArrayNew = Array(initFrame.getString(11), //respcodeprevious
              initFrame.getString(13), //cv2resultprevious
              initFrame.getString(26), //issuerprevious
              initFrame.getString(51), //threedprevious
              initFrame.getString(15), //channelprevious
              initFrame.getString(16), //channelsubtypeprevious
              initFrame.getString(62), //transactiontypeidprevious
              initFrame.getString(14), //authorizationtypeidprevious
              initFrame.getString(22), //processoridprevious
              initFrame.getString(23), //categorycodegroupprevious
            )

            //update KV store
            //change arrays to strings to save
            val valuesArray = previousArrayNew + firstArray + thereArray + changeArray
            var setString = (previousArrayNew + firstArray + thereArray + changeArray).mkString(",")

            //need to still deal with a future, because we need to update the current sessions value with the append
            //or do we even do this in this case???????????????????????????????????
            //I do not know , I think we do to ensure the shortness of the KV, because that is a concern
            //and the date pop still

            //get previous sessionString
            val prevSess = getkey("sessions").map(value => value match
            {case Some(x) => x.toString
              case None => "" } )

            //instantiate newString
            var newString = ""

            //change this !!!!!!!!!!!!!!!!!!!!!!!!
            //I think we need a case if it is empty or maybe do that before
            //combining side-effects with futures makes me slightly quesy!!!!!!!!!!!!!!!!!
            //!!!!!!!!!!!!!! Stackoverflow search needed!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            prevSess.map(ps =>
              breakable for (x <- ps.split("\\|")) {
              if (DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now.plusMinutes(10)) > x.split(",")(1) ) {
                //remove the session from string
                newString = ps.split("\\|").drop(1).mkString("\\|")

                //update the string in the KV store
                //Do we need to do the update here??????????
                //could be case of unnecessary updates!!!!!!!!!!!!!!!!!!!!!!!
                //SO side effects and futures
                setkey("session",  newString)
              }
              else {
                break
              }
            }

            //now Timestamp
            val now = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now)
            //sessionid
            //this needs to be extracted from input, probably will need to define a new class!!!!!!!
            val sessionID = IDvalues.toRow(input).getString(1)

            val newValueString = ps + "|" + sessionID + "," + now.toString + "," + setString
            setkey("session",  newValueString)
            )
            //so newValuesArray is what you pass to the model(s) and newValueString is what you store in the KV store
            val newValuesArray = valuesArray

          }
          else {
            //get values from KV store
            //get previous sessionString
            val prevSess = getkey("sessions").map(value => value match
            {case Some(x) => x.toString
              case None => "" } )

            //instantiate newString
            var newString = ""

            //define new values to save here??
            //or in future map?

            //!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            //can we do a side effect as a consequence of a future???
            //this seems very supsect!!!!!!

            //we need to break if we find a match that is not too late
            //bit ugly, but alternatives are meh https://stackoverflow.com/questions/2742719/how-do-i-break-out-of-a-loop-in-scala

            //parse string and check if we should pop stuff off
            prevSess.map(ps =>
              breakable for (x <- ps.split("\\|")) {
              if (DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now.plusMinutes(10)) > x.split(",")(1) ) {
                //remove the session from string
                newString = ps.split("\\|").drop(1).mkString("\\|")
                //update the string in the KV store
                setkey("session",  newString)
              }
              if else (testSessionID = x.split(",")(0) ) {
                //get values
                var valuesArray = x.split(",").drop(2) //what type is this? // I think array
                //remove the session from string
                newString = ps.split("\\|").drop(1).mkString("\\|")

                //so we get the values from the previous session that we will pass to create one or two frames
                //now we need to append a new string to value of session
                //that contains the data of what we need plus the id and the timestamp

                //so we need to create firstAndThereArray , previousArray and changeArray
                //first stay the same, for the there we need to check
                val firstAndThereArray = valuesArray.slice(0,12)

                val thereArray = Array(initFrame.getString(79), //avsthere
                  if (initFrame.getString(13)=="NULl" || initFrame.getString(13)=="" ) 0 else 1,, //cv2there
                  if (initFrame.getString(52)=="NULL" || initFrame.getString(52)=="" ) 0 else 1,, //expthere
                  if (initFrame.getString(51)=="NULL" || initFrame.getString(51)=="" ) 0 else 1, //threedthere)
                )

                //current values become previous
                var previousArrayNew = Array(initFrame.getString(11), //respcodeprevious
                  initFrame.getString(13), //cv2resultprevious
                  initFrame.getString(26), //issuerprevious
                  initFrame.getString(51), //threedprevious
                  initFrame.getString(15), //channelprevious
                  initFrame.getString(16), //channelsubtypeprevious
                  initFrame.getString(62), //transactiontypeidprevious
                  initFrame.getString(14), //authorizationtypeidprevious
                  initFrame.getString(22), //processoridprevious
                  initFrame.getString(23), //categorycodegroupprevious
                )

                //compare the old with the new
                //so it is the previous value vs current value taken from the initFrame
                // x.split(",")(1)  - System.currentTimeMillis / 1000
                val changeArray = Array( if (valuesArray(26) == initFrame.getString(82) ) 0 else 1,  //threedtherechange
                  if (valuesArray(1) == initFrame.getString(13) ) 0 else 1,  //cv2change
                  (System.currentTimeMillis / 1000 ) -  LocalDateTime.parse(x.split(",")(1), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).atZone(ZoneId.systemDefault()).toEpochSecond() ),  //authdatesecondsdiff
                if (valuesArray(2) == initFrame.getString(26) ) 0 else 1,  //issuerchange
                if (valuesArray(3) == initFrame.getString(51) ) 0 else 1,  //threedchange
                if (valuesArray(9) == initFrame.getString(23) ) 0 else 1,  //categorycodegroupchange
                if (valuesArray(23) == initFrame.getString(79) ) 0 else 1,  //avstherechange
                if (valuesArray(7) == initFrame.getString(14) ) 0 else 1,  //authorizationtypeidchange
                if (valuesArray(8) == initFrame.getString(22) ) 0 else 1,  //processoridchange
                if (valuesArray(4) == initFrame.getString(16) ) 0 else 1,  //channelsubtypechange
                if (valuesArray(5) == initFrame.getString(15) ) 0 else 1,  //channelchange
                )

                //correctly define what we pass to the model, seems okay?
                val newValuesArray = previousArrayNew + firstArray + thereArray + changeArray
                var setString = (previousArrayNew + firstArray + thereArray + changeArray).mkString(",")

                //now Timestamp
                val now = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now)
                //sessionid
                val sessionID = IDvalues.toRow(input).getString(1)

                //put these new values in front of the newString

                //create the new value to set
                val newValueString = ps + "|" + sessionID + "," + now.toString + "," + setString
                setkey("session",  newValueString)

                break
              }
              else {
                //ERROR
                //not sure what to do?
              }
            }
            )
            //update KV store
            //???
            //setkey("session",  newString)

          }

          // 0 normal txn
          // 1 channel moto (channel 2, channelchange 1 and threed 0 and threedchange 1 )
          // 2 remove threed (threed 0 and threedchange 1)

          //so this a oneliner that does all the stuff we do below, but is much less readable. I feel conflicted
          //we check which method we should use the fraud one or the normal one

          //need to pass valuesArray , is double future ?
          //how do we handle this?

          if fraudCheck==1 {
            //this takes both the sessionvalues and the riskvalues
            //maybe need to adept to take in to account that we would use a different model for fraud?
            //so maybe we return something like a tuple of which we then check both values in the ApiResponse class?
            val comprehensionArray = newValuesArray.flatMap ( vs => keyValArray.map(ls => for (x <- Seq.range(0,3) ) yield mleapPipeline.transform(DefaultLeapFrame(OriginalTransaction.schema, Seq(OriginalTransaction.toRow(input, x, vs,ls )))).get.dataset.head.getAs[Double](183) ) )

          }
          else {
            val comprehensionArray = newValuesArray.map(vs  => for (x <- Seq.range(0,3) ) yield mleapPipeline.transform(DefaultLeapFrame(OriginalTransaction.schema, Seq(OriginalTransaction.toRow(input, x, vs)))).get.dataset.head.getAs[Double](183) )

          }
          //if we do two models this will need to be adepted in the future
          comprehensionArray.map(ls =>
            Ok( Json.toJson(ApiResponse(ls.indexOf(ls.max) )) )
          )

        }
        else {
          val changes = 0
          Future ( Json.toJson(ApiResponse(changes)) ).map(ft => Ok(ft))
        }

      case e : JsError => Future( e ).map(ft => BadRequest("Errors: " + JsError.toJson( ft ) ) )

    }
  }

  /*
  def index(): Action[JsValue] = Action.async(parse.tolerantJson) { implicit request: Request[JsValue] =>
    request.body.validate[OriginalTransaction] match {
      case success: JsSuccess[OriginalTransaction] =>
        val input = success.value

        //because of a succesful parse of the api call, we gather data from our KV store
        //for now testcase, later we will need to adapt the leapframe to include the actual kv data

        //generate different candidate transactions here
        //we generate the default transaction if the currentrank is not too high
        //current implentation assumes a certain schema, if this changes we need to change the code

        if ( OriginalTransaction.toRow(input, 0).getDouble(55) < 3.0 ) {

          //we need to get the values for the keys
          //needs to be changed to something extracted from input!!

          val testKey = "10001"
          val keyArray = for (a <- Array("a","b","c", "d")) yield testKey+a

          //not sure that this value deals properly with the Option?!!!!!!!!!!!!!
          //need to change getVal?????? and toRowKV ?????????????????????????????
          //val keyValArray = for (x <- keyArray) yield getValResolve( getVal( x) )

          var keyValArray = for {
            result1 <- getVal( testKey + "a").map( value => value match { case Some(x) => x.toString case None => 0.toString } )
            result2 <- getVal( testKey + "b").map( value => value match { case Some(x) => x.toString case None => 0.toString } )
            result3 <- getVal( testKey + "c").map( value => value match { case Some(x) => x.toString case None => 0.toString } )
            result4 <- getVal( testKey + "d").map( value => value match { case Some(x) => x.toString case None => 0.toString } )
          } yield Array(result1, result2, result3, result4)

          //question is how do we deal with the new ogtxn given that the model does not accept it?
          //for now just pass as the values we will not have in the new contract?

          // we set new values for the key
          // needs to be changed to be something derived from input!!!!
          for (x <- keyArray) {
            setKey(x, "7")
          }

          // 0 normal txn
          // 1 channel moto (channel 2, channelchange 1 and threed 0 and threedchange 1 )
          // 2 remove threed (threed 0 and threedchange 1)

          //so this a oneliner that does all the stuff we do below, but is much less readable. I feel conflicted
          val comprehensionArray = for (x <- Seq.range(0,3) ) yield mleapPipeline.transform(DefaultLeapFrame(OriginalTransaction.schema, Seq(OriginalTransaction.toRow(input, x)))).get.dataset.head.getAs[Double](183)
          println(comprehensionArray)
          //KV version
          val comprehensionArrayKV = keyValArray.map(ls => for (x <- Seq.range(0,3) ) yield mleapPipeline.transform(DefaultLeapFrame(OriginalTransaction.schema, Seq(OriginalTransaction.toRowKV(input, x, ls )))).get.dataset.head.getAs[Double](183) )
          //val comprehensionArrayKV = for (x <- Seq.range(0,3) ) yield mleapPipeline.transform(DefaultLeapFrame(OriginalTransaction.schema, Seq(OriginalTransaction.toRowKV(input, x, keyValArray2 )))).get.dataset.head.getAs[Double](183)

          comprehensionArrayKV.map(ls =>
            Ok( Json.toJson(ApiResponse(ls.indexOf(ls.max) )) )
          )
          /*
          //generate the different versions of the Row object
          val frame = DefaultLeapFrame(OriginalTransaction.schema, Seq(OriginalTransaction.toRow(input, 0)))
          val frame2 = DefaultLeapFrame(OriginalTransaction.schema, Seq(OriginalTransaction.toRow(input, 1)))
          val frame3 = DefaultLeapFrame(OriginalTransaction.schema, Seq(OriginalTransaction.toRow(input, 2)))

          val transform = mleapPipeline.transform(frame).get
          val transform2 = mleapPipeline.transform(frame2).get
          val transform3 = mleapPipeline.transform(frame3).get

          val result = transform.dataset.head
          val result2 = transform2.dataset.head
          val result3 = transform3.dataset.head

          //using index of and max way to determine what to do due to it being both fast and concise
          val resultArray = List(result.getAs[Double](183), result2.getAs[Double](183), result3.getAs[Double](183) )
          val testArray = List(result.getAs[Double](183), 0.01, 0.02, 0.03)
          */
          /*
          comprehensionArrayKV.indexOf(comprehensionArrayKV.map(ls => ls.max) ) match {
            case 0 =>
              val changes = 0
              Future ( Json.toJson(ApiResponse(changes)) ).map(ft => Ok(ft))
               //.as(JSON)
              //Ok(Json.toJson(ApiResponse(changes))).as(JSON)
              //Ok( Future( Json.toJson(ApiResponse(changes)) ) ).as(JSON)
              //Ok(Json.toJson( Future( ApiResponse(changes)  ) )).as(JSON)

            case 1 =>
              val changes = 1
              Future ( Json.toJson(ApiResponse(changes)) ).map(ft => Ok(ft))
            //Ok(Json.toJson(ApiResponse(changes))).as(JSON)
            //Ok( Future( Json.toJson(ApiResponse(changes)) ) ).as(JSON)
            //Ok(Json.toJson( Future( ApiResponse(changes)  ) )).as(JSON)
            case 2 =>
              val changes = 2
              Future ( Json.toJson(ApiResponse(changes)) ).map(ft => Ok(ft))
            //Ok(Json.toJson(ApiResponse(changes))).as(JSON)
            //Ok( Future( Json.toJson(ApiResponse(changes)) ) ).as(JSON)
            //Ok(Json.toJson( Future( ApiResponse(changes)  ) )).as(JSON)
          }
          */
        }
        else {
          val changes = 0
          Future ( Json.toJson(ApiResponse(changes)) ).map(ft => Ok(ft))
          //Ok(Json.toJson(ApiResponse(changes))).as(JSON)
          //Ok( Future( Json.toJson(ApiResponse(changes)) ) ).as(JSON)
          //Ok(Json.toJson( Future( ApiResponse(changes)  ) )).as(JSON)
        }

        //val predictResultVal = result.getAs[Double](183) //getDouble(183) //current model has 183 values, change when we change schema

      //case e : JsError => BadRequest("Errors: " + JsError.toJson(e) ) //.toString())
      case e : JsError => Future( e ).map(ft => BadRequest("Errors: " + JsError.toJson( ft ) ) )
      //case JsError(_) => Future("Invalid Input!").map(ft => BadRequest(ft) )
      //case JsError(_) => BadRequest("Invalid Input!")
    }
  }
*/

  def healthz() = Action.async {
    //insertKeyz()

    getVal("10001a").map(  value =>
      value match {
        case Some(x)   => { //if x != "Future(<not completed>)"

          setKey("10001a" ,(x.toInt + 1).toString )
          Ok(( x.toInt + 1).toString )
          //setKey(x + "|")
          //Ok(x + "|")
        }
        case None => {
          setKey("10001a",0.toString )
          Ok( 0.toString )
          //setKey("|")
          //Ok("|")
        }

      }

    )
  }


}
