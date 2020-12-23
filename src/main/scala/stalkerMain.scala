package spotifyStalker

import spray.json._
import DefaultJsonProtocol._
import SongJsonProfile._

import scala.collection.mutable.Map
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

import org.mongodb.scala._
import org.mongodb.scala.model.Aggregates._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Projections._
import org.mongodb.scala.model.Sorts._
import org.mongodb.scala.model.Updates._
import org.mongodb.scala.model._

import scala.collection.JavaConverters._

import java.util.concurrent.TimeUnit

object Main {
  def main(args: Array[String]): Unit = {

    if (args.length == 0) {
      println(
        "Spotify Stalker Usage: \n" +
          "sbt run <filename.json> <number_of_songs_to_parse(Integer)>" +
          "Please input two valid command line arguments."
      )
      return
    } else {
      println("These are your command line argument: ")
      args.foreach(println(_))
    }

    val file = io.Source.fromFile(args(0))

    val numSongs = args(1).toInt

    val lines = file.getLines().drop(1).take(numSongs * 6)

    var lineCount = 0

    var currentSongString = ""

    val client = MongoClient()
    val db = client.getDatabase("test")
    val songCollection = db.getCollection("songs")
    songCollection.drop()

    for (line <- lines) {
      lineCount += 1

      currentSongString = currentSongString.concat(line)

      if (lineCount == 6) {
        lineCount = 0
        println("Song JSON to insert:")
        println(currentSongString)
        val song = currentSongString.stripSuffix(",").parseJson

        currentSongString = ""

        val songRecord = new Song(
          song.asJsObject.fields("endTime").toString,
          song.asJsObject.fields("artistName").toString,
          song.asJsObject.fields("trackName").toString,
          song.asJsObject.fields("msPlayed").toString.toInt
        )

        val doc: Document = Document(
          "endTime" -> songRecord.endTime,
          "artistName" -> songRecord.artistName,
          "trackName" -> songRecord.trackName,
          "msPlayed" -> songRecord.msPlayed
        )
        Await.result(
          db.getCollection("songs").insertOne(doc).head(),
          Duration(10, TimeUnit.SECONDS)
        )
      }
    }

    // println("songIterator:")
    // for ( songIterator <- 0 to numSongs) {
    //   val lines = file.getLines().drop(songIterator*6).take(6) //Skips the first character + the number of songs that have been processed already(tracked by the iterator)
    //   var currentSongString = ""
    //   for (line <- lines) {
    //     // println(line)
    //     currentSongString = currentSongString.concat(line)
    //   }
    //   println("Inner loop end")
    //   println(currentSongString)
    // }

    // println("These are my file lines: " + lines)

    // val jTest =
    //   """{ "endTime" : "2019-12-16 22:52","artistName" : "Smile Empty Soul","trackName" : "Bottom of a Bottle","msPlayed" : 116920}""".parseJson
    // val song1 =
    //   """{ "endTime" : "2019-12-16 12:51","artistName" : "Tonic","trackName" : "If You Could Only See","msPlayed" : 251228}""".parseJson
    // val song2 =
    //   """{ "endTime" : "2019-12-16 12:53","artistName" : "Machine Gun Kelly","trackName" : "I Think I'm OKAY (with YUNGBLUD & Travis Barker)","msPlayed" : 169397}""".parseJson
    // val song3 =
    //   """{ "endTime" : "2019-12-16 12:54","artistName" : "Mumford & Sons","trackName" : "Little Lion Man","msPlayed" : 13163}""".parseJson
    // val song4 =
    //   """{ "endTime" : "2019-12-16 23:15","artistName" : "Envoi","trackName" : "Paper Tigers","msPlayed" : 86977}""".parseJson
    // val song5 =
    //   """{ "endTime" : "2019-12-16 23:22","artistName" : "DALES","trackName" : "Chateau","msPlayed" : 187555}""".parseJson

    // println(jTest)

    // val songArray = Array(jTest, song1, song2, song3, song4, song5)

    // val client = MongoClient()
    // val db = client.getDatabase("test")
    // val songCollection = db.getCollection("songs")
    // songCollection.drop()

    // for (song <- songArray) {

    //   val songRecord = new Song(
    //     song.asJsObject.fields("endTime").toString,
    //     song.asJsObject.fields("artistName").toString,
    //     song.asJsObject.fields("trackName").toString,
    //     song.asJsObject.fields("msPlayed").toString.toInt
    //   )

    //   val doc: Document = Document(
    //     "endTime" -> songRecord.endTime,
    //     "artistName" -> songRecord.artistName,
    //     "trackName" -> songRecord.trackName,
    //     "msPlayed" -> songRecord.msPlayed
    //   )
    //   Await.result(
    //     db.getCollection("songs").insertOne(doc).head(),
    //     Duration(10, TimeUnit.SECONDS)
    //   )

      // db.getCollection("songs").find().printResults()
      // Await.result(res, Duration(10, TimeUnit.SECONDS)).foreach(println)
    // }

    // println(songRecord.artistName + songRecord.msPlayed)

    // val doc: Document = Document(
    //   "endTime" -> songRecord.endTime,
    //   "artistName" -> songRecord.artistName,
    //   "trackName" -> songRecord.trackName,
    //   "msPlayed" -> songRecord.msPlayed
    // )
    // Await.result(
    //   db.getCollection("songs").insertOne(doc).head(),
    //   Duration(10, TimeUnit.SECONDS)
    // )

    // val res = db.getCollection("songs").find().head()
    // Await.result(res, Duration(10, TimeUnit.SECONDS)).foreach(println)
    // val testSong = new Song

    // case class Song(endTime: String, artistName: String, trackName: String, msPlayed: Int)
    // implicit val songFormat = jsonFormat2(Song)

    // Song("testDate", "aName", "songTitle", 20000)
    // val songJsonString = song.toJson.prettyPrint

    // val jTest = """{ "endTime" : "2019-12-16 22:52","artistName" : "Smile Empty Soul","trackName" : "Bottom of a Bottle","msPlayed" : 116920}"""

    // val songRead = jTest.parseJson.convertTo[Song]

    // jTest.convertTo[Map[String, String]]
    // jsonAst.convertTo[Map[String, String]]
    // println(jsonAst)
    // println("Testing jsonAST:")
    // println(jsonAst)

    // val nameTest = jsonAst.toString

    // println(nameTest)

    // val splitjson = nameTest.split(",")

    // println("Testing split: ")
    // splitjson.foreach(println)

    // val json = jsonAst.prettyPrint

    // println(json)
  }
}
