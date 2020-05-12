package model


import play.api.libs.json.Json
import play.api.libs.json._

case class like (id: String, postid: String, userid: String)


object like {

    implicit object likeformat extends Format[like] {

        // convert from like object to JSON (serializing to JSON)
        def writes(currLike: like): JsValue = {
            //  likeSeq == Seq[(String, play.api.libs.json.JsString)]
            val likeSeq = Seq(
                "id" -> JsString(currLike.id),
                "postid" -> JsString(currLike.postid),
                "userid" -> JsString(currLike.userid)
            )
            JsObject(likeSeq)
        }

        // convert from JSON string to a user object (de-serializing from JSON)
        // (i don't need this method; just here to satisfy the api)
        def reads(json: JsValue): JsResult[like] = {
            JsSuccess(like("", "", ""))
        }

    }

}
