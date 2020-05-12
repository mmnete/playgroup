package model


import play.api.libs.json.Json
import play.api.libs.json._

case class user (id: String, email: String, password: String, fullname: String, pictureurl: String)


object user {

    implicit object userformat extends Format[user] {

        // convert from user object to JSON (serializing to JSON)
        def writes(currUser: user): JsValue = {
            //  userSeq == Seq[(String, play.api.libs.json.JsString)]
            val userSeq = Seq(
                "id" -> JsString(currUser.id),
                "email" -> JsString(currUser.email),
                "password" -> JsString(currUser.password),
                "fullname" -> JsString(currUser.fullname),
                "pictureurl" -> JsString(currUser.pictureurl)
            )
            JsObject(userSeq)
        }

        // convert from JSON string to a user object (de-serializing from JSON)
        // (i don't need this method; just here to satisfy the api)
        def reads(json: JsValue): JsResult[user] = {
            JsSuccess(user("", "", "", "", ""))
        }

    }

}
