package model


import play.api.libs.json.Json
import play.api.libs.json._

case class post (id: String, caption: String, url: String, userid: String, filetype: String)


object post {

    implicit object postformat extends Format[post] {

        // convert from post object to JSON (serializing to JSON)
        def writes(currPost: post): JsValue = {
            //  postSeq == Seq[(String, play.api.libs.json.JsString)]
            val postSeq = Seq(
                "id" -> JsString(currPost.id),
                "caption" -> JsString(currPost.caption),
                "url" -> JsString(currPost.url),
                "userid" -> JsString(currPost.userid),
                "filetype" -> JsString(currPost.filetype)
            )
            JsObject(postSeq)
        }

        // convert from JSON string to a user object (de-serializing from JSON)
        // (i don't need this method; just here to satisfy the api)
        def reads(json: JsValue): JsResult[post] = {
            JsSuccess(post("", "", "", "",""))
        }

    }

}
