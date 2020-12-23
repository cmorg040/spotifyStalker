package spotifyStalker

import spray.json._
import DefaultJsonProtocol._

class Song(var endTime: String, var artistName: String, var trackName: String, var msPlayed: Int)

object SongJsonProfile extends DefaultJsonProtocol {
    implicit object SongJsonFormat extends RootJsonFormat[Song] {
        def write(s: Song) = JsArray(JsString(s.endTime), JsString(s.artistName), JsString(s.trackName), JsNumber(s.msPlayed))

        def read(value: JsValue) = value match {
            case JsArray(Vector(JsString(endTime), JsString(artistName), JsString(trackName), JsNumber(msPlayed))) => new Song(endTime, artistName, trackName, msPlayed.toInt)
            case _ => deserializationError("Song expected")
        }
    }
}