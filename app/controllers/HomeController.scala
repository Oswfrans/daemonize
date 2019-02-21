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

import java.time._
//import java.time.{LocalDateTime, ZoneId }
//import java.time.format.DateTimeFormatter._
import scala.util.control.Breaks._
import scala.collection.JavaConverters._

import ml.combust.mleap.runtime.frame.Row

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
    for( i <- 1 to 2000) {
      for (x <- Array("a", "b", "c", "d")) {
        val key = i.toString+x
        val value = (i).toString
        client.kvService.setKey(key, value)
      }
    }
  }

  def kvFunction(fraudFlag: Int) : scala.concurrent.Future[Array[String]] = {
    if (fraudFlag==1) {
      val testKey = "1001"
      val keyArray = for (a <- Array("a","b","c", "d")) yield testKey+a

      val keyValArray = for {
        result1 <- getVal( testKey + "a").map( value => value match { case Some(x) => x.toString case None => 0.toString } )
        result2 <- getVal( testKey + "b").map( value => value match { case Some(x) => x.toString case None => 0.toString } )
        result3 <- getVal( testKey + "c").map( value => value match { case Some(x) => x.toString case None => 0.toString } )
        result4 <- getVal( testKey + "d").map( value => value match { case Some(x) => x.toString case None => 0.toString } )
      } yield Array(result1, result2, result3, result4)

      // we set new values for the key
      // needs to be changed to be something derived from input!!!!
      //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
      for (x <- keyArray) {
        setKey(x, "7")
      }
      return keyValArray
    }
    else {
      return scala.concurrent.Future(Array("1", "1", "1", "1") )
    }
  }

  def renameLater(initFrame: Row, sessID : String) : Array[String] = {

    val timeForm = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    if ( initFrame.getDouble(55) == 1.0 ) {

      //all the first values for the session
      //there check needs to be double checked
      val firstArray =  Array(  initFrame.getString(16), //channelsubtypefirst
        initFrame.getString(22), //processoridfirst
        initFrame.getString(79), //avstherefirst
        if (initFrame.getString(51)=="NULL" || initFrame.getString(51)=="" ) "0" else "1", //threedtherefirst
        initFrame.getString(11), //respcodefirst
        initFrame.getString(3), //firstdate
        initFrame.getString(26), //issuerfirst
        initFrame.getString(51), //threedfirst
        initFrame.getString(15), //channelfirst
        initFrame.getString(62), //transactiontypeidfirst
        initFrame.getString(14), //authorizationtypeidfirst
        initFrame.getString(23), //categorycodegroupfirst
        initFrame.getString(13) //cv2resultfirst
      )

      val thereArray = Array(initFrame.getString(79), //avsthere
        if (initFrame.getString(13)=="NULl" || initFrame.getString(13)=="" ) "0" else "1", //cv2there
        if (initFrame.getString(52)=="NULL" || initFrame.getString(52)=="" ) "0" else "1", //expthere
        if (initFrame.getString(51)=="NULL" || initFrame.getString(51)=="" ) "0" else "1" //threedthere
      )

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
        initFrame.getString(23) //categorycodegroupprevious
      )

      val previousArray = Array("0", "0", "0", "0", "0","0","0","0","0","0" )

      //update KV store
      //change arrays to strings to save
      val valuesArray : Array[String] = previousArray ++ firstArray ++ thereArray ++ changeArray
      var setString : String = (previousArrayNew ++ firstArray ++ thereArray ++ changeArray).mkString(",")

      //get previous sessionString
      val prevSess = getVal("sessions").map(value => value match
      {case Some(x) => x.toString
        case None => "" } )

      //instantiate newString
      var newString = ""
      var trueSetString = ""
      var set=0

      //so await vs oncomplete
      //in principal we could do oncomplete?
      //lets test it

      prevSess.onComplete({
        case Success(value) => {
          if (value == "") {
            trueSetString = sessID + "," + java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(java.time.LocalDateTime.now).toString + "," + setString
            //assert(trueSetString != "")
            setKey("sessions", trueSetString + "|" + trueSetString)
          }
          else {
            breakable {
              for (x <- value.split("\\|")) {
                println("here")
                println(x)
                println(x.split(",")(1))

                val parseTime: String = x.split(",")(1)
                println(parseTime)
                println("here as well")
                println(java.time.LocalDateTime.now.isAfter(java.time.LocalDateTime.parse(parseTime, timeForm).plusMinutes(10)))
                if (java.time.LocalDateTime.now.isAfter(java.time.LocalDateTime.parse(parseTime, timeForm).plusMinutes(10))) { //timeForm.parse(x.split(",")(1)).plusMinutes(10)  ) {
                  println("here1")
                  //remove the session from string
                  newString = if (newString == "") value.split("\\|").drop(1).mkString("\\|") else newString.split("\\|").drop(1).mkString("\\|")
                }
                else {
                  println("here2")
                  trueSetString = if (newString == "") value + "|" + sessID + "," + java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(java.time.LocalDateTime.now).toString + "," + setString else newString + "|" + sessID + "," + java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(java.time.LocalDateTime.now).toString + "," + setString
                  set = 1
                  setKey("sessions", trueSetString)
                  break
                }
              }
            }
          }
        }
        case Failure(value) => {
          //!! think doing nothing here is fine

        }
      })

      return valuesArray

    }
    else {
      //get values from KV store
      //get previous sessionString
      val prevSess = getVal("sessions").map(value => value match
      {case Some(x) => x.toString
        case None => "" } )

      //instantiate newString
      var newString = ""
      var newValuesArray = Array("")
      var newValueString = ""

      //!!!!!!!!!!!!!!!!!!!
      //to debate with Joao the time we should wait at maximum
      //!!!!!!!!!!!!
      Await.ready(prevSess, 0.5.seconds).onComplete(result => {
        result match {
          case Success(value) => {
            breakable { for (x <- value.split("\\|")) {
              val parseTime : String = x.split(",")(1)
              if ( java.time.LocalDateTime.now.isAfter(java.time.LocalDateTime.parse(parseTime, timeForm).plusMinutes(10) ) ) {
                //remove the session from string
                newString = if (newString=="") value.split("\\|").drop(1).mkString("\\|") else newString.split("\\|").drop(1).mkString("\\|")

              }
              else if ( ( sessID == x.split(",")(0) ) ) {
                //get values
                var valuesArray = x.split(",").drop(2) //what type is this? // I think array
                //remove the session from string
                newString = if (newString=="") value.split("\\|").drop(1).mkString("\\|") else newString.split("\\|").drop(1).mkString("\\|")

                //so we get the values from the previous session that we will pass to create one or two frames
                //now we need to append a new string to value of session
                //that contains the data of what we need plus the id and the timestamp

                val firstArray : Array[String] = valuesArray.slice(0,13)

                val thereArray = Array(initFrame.getString(79), //avsthere
                  if (initFrame.getString(13)=="NULl" || initFrame.getString(13)=="" ) "0" else "1", //cv2there
                  if (initFrame.getString(52)=="NULL" || initFrame.getString(52)=="" ) "0" else "1", //expthere
                  if (initFrame.getString(51)=="NULL" || initFrame.getString(51)=="" ) "0" else "1" //threedthere)
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
                  initFrame.getString(23) //categorycodegroupprevious
                )

                //compare the old with the new
                //so it is the previous value vs current value taken from the initFrame
                val changeArray = Array( if (valuesArray(26) == initFrame.getString(82) ) "0" else "1",  //threedtherechange
                  if (valuesArray(1) == initFrame.getString(13) ) "0" else "1",  //cv2change
                  ( (System.currentTimeMillis / 1000 ) -  java.time.LocalDateTime.parse(x.split(",")(1), java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).atZone(ZoneId.systemDefault()).toEpochSecond() ).toString() ,  //authdatesecondsdiff
                  if (valuesArray(2) == initFrame.getString(26) ) "0" else "1",  //issuerchange
                  if (valuesArray(3) == initFrame.getString(51) ) "0" else "1",  //threedchange
                  if (valuesArray(9) == initFrame.getString(23) ) "0" else "1",  //categorycodegroupchange
                  if (valuesArray(23) == initFrame.getString(79) ) "0" else "1",  //avstherechange
                  if (valuesArray(7) == initFrame.getString(14) ) "0" else "1",  //authorizationtypeidchange
                  if (valuesArray(8) == initFrame.getString(22) ) "0" else "1",  //processoridchange
                  if (valuesArray(4) == initFrame.getString(16) ) "0" else "1",  //channelsubtypechange
                  if (valuesArray(5) == initFrame.getString(15) ) "0" else "1"  //channelchange
                )

                //correctly define what we pass to the model, seems okay?
                newValuesArray = previousArrayNew ++ firstArray ++ thereArray ++ changeArray
                var setString = (previousArrayNew ++ firstArray ++ thereArray ++ changeArray).mkString(",")

                //now Timestamp
                val now = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(java.time.LocalDateTime.now)

                //create the new value to set
                newValueString = newString + "|" + sessID + "," + now.toString + "," + setString

                setKey("sessions",  newValueString)

                break
              }
              else {
                //ERROR
                //should not be accessed not sure what to do?
              }
            }
            }
          }
          case Failure(e) => {
            newValuesArray= Array("1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1")

          }
        }
      }
      )

      //oncomplete do not think that works for this one, because we need to pass the array to the model
      //so let us try await !!!!!!!! to discuss the amount of time to wait
      //current setup is kinda like await hmm

      //see below await setup, to discuss with Joao if we should do await or the thread sleep if needed
      /*
      val awaited = Await.result(prevSess, 0.5.seconds)
      breakable { for (x <- awaited.split("\\|")) {
        val parseTime : String = x.split(",")(1)
        if ( java.time.LocalDateTime.now.isAfter(java.time.LocalDateTime.parse(parseTime, timeForm).plusMinutes(10) ) ) {
          //remove the session from string
          newString = if (newString=="") awaited.split("\\|").drop(1).mkString("\\|") else newString.split("\\|").drop(1).mkString("\\|")

        }
        else if ( ( sessID == x.split(",")(0) ) ) {
          //get values
          var valuesArray = x.split(",").drop(2) //what type is this? // I think array
          //remove the session from string
          newString = if (newString=="") awaited.split("\\|").drop(1).mkString("\\|") else newString.split("\\|").drop(1).mkString("\\|")

          //so we get the values from the previous session that we will pass to create one or two frames
          //now we need to append a new string to value of session
          //that contains the data of what we need plus the id and the timestamp

          val firstArray : Array[String] = valuesArray.slice(0,13)

          val thereArray = Array(initFrame.getString(79), //avsthere
            if (initFrame.getString(13)=="NULl" || initFrame.getString(13)=="" ) "0" else "1", //cv2there
            if (initFrame.getString(52)=="NULL" || initFrame.getString(52)=="" ) "0" else "1", //expthere
            if (initFrame.getString(51)=="NULL" || initFrame.getString(51)=="" ) "0" else "1" //threedthere)
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
            initFrame.getString(23) //categorycodegroupprevious
          )

          //compare the old with the new
          //so it is the previous value vs current value taken from the initFrame
          val changeArray = Array( if (valuesArray(26) == initFrame.getString(82) ) "0" else "1",  //threedtherechange
            if (valuesArray(1) == initFrame.getString(13) ) "0" else "1",  //cv2change
            ( (System.currentTimeMillis / 1000 ) -  java.time.LocalDateTime.parse(x.split(",")(1), java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).atZone(ZoneId.systemDefault()).toEpochSecond() ).toString() ,  //authdatesecondsdiff
            if (valuesArray(2) == initFrame.getString(26) ) "0" else "1",  //issuerchange
            if (valuesArray(3) == initFrame.getString(51) ) "0" else "1",  //threedchange
            if (valuesArray(9) == initFrame.getString(23) ) "0" else "1",  //categorycodegroupchange
            if (valuesArray(23) == initFrame.getString(79) ) "0" else "1",  //avstherechange
            if (valuesArray(7) == initFrame.getString(14) ) "0" else "1",  //authorizationtypeidchange
            if (valuesArray(8) == initFrame.getString(22) ) "0" else "1",  //processoridchange
            if (valuesArray(4) == initFrame.getString(16) ) "0" else "1",  //channelsubtypechange
            if (valuesArray(5) == initFrame.getString(15) ) "0" else "1"  //channelchange
          )

          //correctly define what we pass to the model, seems okay?
          newValuesArray = previousArrayNew ++ firstArray ++ thereArray ++ changeArray
          var setString = (previousArrayNew ++ firstArray ++ thereArray ++ changeArray).mkString(",")

          //now Timestamp
          val now = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(java.time.LocalDateTime.now)

          //create the new value to set
          newValueString = newString + "|" + sessID + "," + now.toString + "," + setString

          setKey("sessions",  newValueString)

          break
        }
        else {
          //ERROR
          //should not be accessed not sure what to do?
        }
      }
      }
      */

      //we need to break if we find a match that is not too late
      //bit ugly, but alternatives are meh https://stackoverflow.com/questions/2742719/how-do-i-break-out-of-a-loop-in-scala

      //can refactor this !
      //parse string and check if we should pop stuff off
      //thread.sleep version barf
      /*
      prevSess.map(ps =>
        breakable { for (x <- ps.split("\\|")) {
          val parseTime : String = x.split(",")(1)
          if ( java.time.LocalDateTime.now.isAfter(java.time.LocalDateTime.parse(parseTime, timeForm).plusMinutes(10) ) ) {
            //remove the session from string
            newString = if (newString=="") ps.split("\\|").drop(1).mkString("\\|") else newString.split("\\|").drop(1).mkString("\\|")

          }
          else if ( ( sessID == x.split(",")(0) ) ) {
            //get values
            var valuesArray = x.split(",").drop(2) //what type is this? // I think array
            //remove the session from string
            newString = if (newString=="") ps.split("\\|").drop(1).mkString("\\|") else newString.split("\\|").drop(1).mkString("\\|")

            //so we get the values from the previous session that we will pass to create one or two frames
            //now we need to append a new string to value of session
            //that contains the data of what we need plus the id and the timestamp

            val firstArray : Array[String] = valuesArray.slice(0,13)

            val thereArray = Array(initFrame.getString(79), //avsthere
              if (initFrame.getString(13)=="NULl" || initFrame.getString(13)=="" ) "0" else "1", //cv2there
              if (initFrame.getString(52)=="NULL" || initFrame.getString(52)=="" ) "0" else "1", //expthere
              if (initFrame.getString(51)=="NULL" || initFrame.getString(51)=="" ) "0" else "1" //threedthere)
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
              initFrame.getString(23) //categorycodegroupprevious
            )

            //compare the old with the new
            //so it is the previous value vs current value taken from the initFrame
            val changeArray = Array( if (valuesArray(26) == initFrame.getString(82) ) "0" else "1",  //threedtherechange
              if (valuesArray(1) == initFrame.getString(13) ) "0" else "1",  //cv2change
              ( (System.currentTimeMillis / 1000 ) -  java.time.LocalDateTime.parse(x.split(",")(1), java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).atZone(ZoneId.systemDefault()).toEpochSecond() ).toString() ,  //authdatesecondsdiff
              if (valuesArray(2) == initFrame.getString(26) ) "0" else "1",  //issuerchange
              if (valuesArray(3) == initFrame.getString(51) ) "0" else "1",  //threedchange
              if (valuesArray(9) == initFrame.getString(23) ) "0" else "1",  //categorycodegroupchange
              if (valuesArray(23) == initFrame.getString(79) ) "0" else "1",  //avstherechange
              if (valuesArray(7) == initFrame.getString(14) ) "0" else "1",  //authorizationtypeidchange
              if (valuesArray(8) == initFrame.getString(22) ) "0" else "1",  //processoridchange
              if (valuesArray(4) == initFrame.getString(16) ) "0" else "1",  //channelsubtypechange
              if (valuesArray(5) == initFrame.getString(15) ) "0" else "1"  //channelchange
            )

            //correctly define what we pass to the model, seems okay?
            newValuesArray = previousArrayNew ++ firstArray ++ thereArray ++ changeArray
            var setString = (previousArrayNew ++ firstArray ++ thereArray ++ changeArray).mkString(",")

            //now Timestamp
            val now = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(java.time.LocalDateTime.now)

            //create the new value to set
            newValueString = newString + "|" + sessID + "," + now.toString + "," + setString

            setKey("sessions",  newValueString)

            break
          }
          else {
            //ERROR
            //should not be accessed not sure what to do?
          }
        }
        }
      )
      */

      /*
      //assert is kept during testing
      assert(newValuesArray != Array("") )

      if (newValuesArray != Array("") ) {
        return newValuesArray
      }
      else {
        Thread.sleep(10)
        return newValuesArray
      }
      */
      return newValuesArray
    }

  }



  //func version
  def index(): Action[JsValue] = Action.async(parse.tolerantJson) { implicit request: Request[JsValue] =>
    request.body.validate[OriginalTransaction] match {
      case success: JsSuccess[OriginalTransaction] =>
        val input = success.value
        val sessionID = OriginalTransaction.toRowID(input).getString(1)
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //VERYFY with Jorge that respcode is detailedcode !!!!!!!!!!!!!!!!!!!!!

        //because of a successful parse of the api call, we gather data from our KV store

        //generate different candidate transactions here
        //we generate the default transaction if the currentrank is not too high
        //current implantation assumes a certain schema, if this changes we need to change the code

        //the sessionvalues are assumed to be in the following order:
        //respcodeprevious cv2resultprevious issuerprevious  threedprevious  channelprevious channelsubtypeprevious  transactiontypeidprevious authorizationtypeidprevious processoridprevious categorycodegroupprevious channelsubtypefirst processoridfirst  avstherefirst threedtherefirst  respcodefirst firstdate issuerfirst threedfirst channelfirst  transactiontypeidfirst  authorizationtypeidfirst  categorycodegroupfirst  cv2resultfirst  avsthere  cv2there  expthere  threedthere threedtherechange cv2change authdatesecondsdiff issuerchange  threedchange  categorycodegroupchange avstherechange  authorizationtypeidchange processoridchange channelsubtypechange  channelchange

        val fraudCheck = 0

        //38 values
        //because implicit vals are hard
        val sessionValuesDefault = Array("1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1")
        val kvArrayDefault = Array("1", "1", "1", "1")

        //need to create intermediate df object to not recompute everything
        val initFrame = OriginalTransaction.toRow(input, 0, sessionValuesDefault, kvArrayDefault)

        if ( initFrame.getDouble(55) < 3.0 ) {

          // 0 normal txn
          // 1 channel moto (channel 2, channelchange 1 and threed 0 and threedchange 1 )
          // 2 remove threed (threed 0 and threedchange 1)

          //function version
          val kvArray = kvFunction(fraudCheck)
          val iterArray = renameLater(initFrame, sessionID)

          val comprehensionArray : scala.concurrent.Future[List[Double]] = if (fraudCheck ==1) kvArray.map(ls => for (x <- List.range(0,3) ) yield mleapPipeline.transform(DefaultLeapFrame(OriginalTransaction.schema, Seq(OriginalTransaction.toRow(input, x, iterArray,ls  ) ) ) ).get.dataset.head.getAs[Double](183) ) else scala.concurrent.Future( for (x <- List.range(0,3) ) yield mleapPipeline.transform(DefaultLeapFrame(OriginalTransaction.schema, Seq(OriginalTransaction.toRow(input, x, iterArray, kvArrayDefault ) ) ) ).get.dataset.head.getAs[Double](183) )

          //if we do two models this will need to be adapted in the future

          comprehensionArray.map(ls =>
            //ls.foreach(println)
            Ok( Json.toJson(ApiResponse( (ls).indexOf((ls).max) )) )
          )

        }
        else {
          val changes = 0
          Future ( Json.toJson(ApiResponse(changes)) ).map(ft => Ok(ft))
        }

      case e : JsError => Future( e ).map(ft => BadRequest("Errors: " + JsError.toJson( ft ) ) )
    }
  }


  def dummyFunction()  : Unit = {
    setKey("ddd", "101")
  }

  def healthz() = Action.async {
    //insertKeyz()
    //setKey("aaa", "400")
    //dummyFunction()
    /*
    getVal("sessions").map(value =>
    value match {
      case Some(x) => {
      dummyFunction()
    }
    case None => {
      dummyFunction()
    }
    } )
    */
    getVal("sessions").map(  value =>
      value match {
        case Some(x)   => {
          Ok( x )
        }
        case None => {
          Ok( 2.toString )
        }
      }
    )
  }

  /*
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
  */

}
