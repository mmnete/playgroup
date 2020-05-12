package controllers

import javax.inject._

import play.api.mvc._
import model._
import db._
import util._
import scala.collection.mutable.ListBuffer
import scala.collection.mutable._
import play.api.libs.json.Json
import play.api.libs.json._

import java.io.File
import java.nio.file.attribute.PosixFilePermission._
import java.nio.file.attribute.PosixFilePermissions
import java.nio.file.{ Files => JFiles }
import java.nio.file.Path
import java.nio.file.Paths

@Singleton
class Application @Inject()(cc: ControllerComponents) extends AbstractController(cc) {


    // this allows us to do all the database stuff
    val tempDb = new myDbConnector();

  def isLoggedIn (req: RequestHeader): Boolean = {
        req.session.get("email").map { user =>
            return true;
        }.getOrElse{
         return false;
        }
  }

  def getCurrUserEmail (req: RequestHeader): String = {
      return req.session.get("email").get
  }

  def index = Action { req =>



        if(!isLoggedIn(req))
        {
            Redirect(routes.loginController.index)
        } else {

            // validate if the user exists
            tempDb.connectToDatabase()
            var myEmail = getCurrUserEmail(req)
            var me = tempDb.fetchUser(myEmail)

            if(me == null)
            {
               // this user has deleted an account or does not exists
               Redirect(routes.loginController.index)
            } else {
              var myEmail = getCurrUserEmail(req)
              tempDb.connectToDatabase()
              var me = tempDb.fetchUser(myEmail)
              if(me == null)
              {
                  Redirect(routes.loginController.index)
              } else {
                Ok(views.html.onlineRootPage(me))
              }
            }



        }
  }


  def converPostToJson(newPost: post): JsValue = Json.toJson(newPost)
  // view mystory
  def mystory = Action { req =>
      var myEmail = getCurrUserEmail(req)
      tempDb.connectToDatabase()
      var me = tempDb.fetchUser(myEmail)
      if(me == null)
      {
          // user not found
        Redirect(routes.loginController.index)
      } else {

              // returning your story
              Ok(views.html.myCurrentPostView(me))
      }
  }

  def convertPostsToJson(posts: Seq[post]): JsValue = Json.toJson(posts)

  // this function searches for all the users according to their email or fullname
  def searchPosts = Action { req =>
        val postValues = req.body.asFormUrlEncoded
        postValues.map { args  =>
            // the search can either be email or fullname
            val filter = args("query").head
            tempDb.connectToDatabase()
            var searchRes = tempDb.searchForPosts (filter)
            if(searchRes.isEmpty)
            {
                // no posts have been found!
                Ok("")
            } else {
                // could not log user in
                println(convertPostsToJson(searchRes.to[collection.mutable.Seq]))
                Ok(convertPostsToJson(searchRes.to[collection.mutable.Seq]))
            }
        }.getOrElse({
            // this request did not come from a form. Just take the user back to the normal page
            Redirect(routes.Application.index)
        })
  }


  def convertUsersToJson(users: Seq[user]): JsValue = Json.toJson(users)

  // this function searches for all the users according to their email or fullname
  def searchUsers = Action { req =>
        val postValues = req.body.asFormUrlEncoded
        postValues.map { args  =>
            // the search can either be email or fullname
            val email = args("query").head
            val fullname = args("query").head
            tempDb.connectToDatabase()
            var searchRes = tempDb.searchForUser (email, fullname)
            if(searchRes.isEmpty)
            {
                // user not found
                Ok("")
            } else {
                // could not log user in
                Ok(convertUsersToJson(searchRes.to[collection.mutable.Seq]))
            }
        }.getOrElse({
            // this request did not come from a form. Just take the user back to the normal page
            Redirect(routes.Application.index)
        })
  }

  // follow user given their email
  def followUser = Action { req =>
        var myEmail = getCurrUserEmail(req)
        val postValues = req.body.asFormUrlEncoded
        postValues.map { args  =>
            // get the email of the other user
            val otherUserEmail = args("email").head
            tempDb.connectToDatabase()
            var otherUser = tempDb.fetchUser(otherUserEmail)

            var me = tempDb.fetchUser(myEmail)
            if(otherUser == null || me == null)
            {
                // user not found
                Ok("")
            } else {
                // now follow
                tempDb.followUser(me, otherUser)
                Ok("SUCCESS")
            }
        }.getOrElse({
            // this request did not come from a form. Just take the user back to the normal page
            Redirect(routes.Application.index)
        })
  }

  // follow user given their email
  def unfollowUser = Action { req =>
        val postValues = req.body.asFormUrlEncoded
        postValues.map { args  =>
            // get the email of the other user
            val otherUserEmail = args("email").head
            tempDb.connectToDatabase()
            var otherUser = tempDb.fetchUser(otherUserEmail)
            var myEmail = getCurrUserEmail(req)
            var me = tempDb.fetchUser(myEmail)
            if(otherUser == null || me == null)
            {
                // user not found
                Ok("")
            } else {
                // now follow
                tempDb.unFollowUser(me, otherUser)
                Ok("SUCCESS")
            }
        }.getOrElse({
            // this request did not come from a form. Just take the user back to the normal page
            Redirect(routes.Application.index)
        })
  }

  // follow user given their email
  def removeFollower = Action { req =>
        val postValues = req.body.asFormUrlEncoded
        postValues.map { args  =>
            // get the email of the other user
            val otherUserEmail = args("email").head
            tempDb.connectToDatabase()
            var otherUser = tempDb.fetchUser(otherUserEmail)
            var myEmail = getCurrUserEmail(req)
            var me = tempDb.fetchUser(myEmail)
            if(otherUser == null || me == null)
            {
                // user not found
                Ok("")
            } else {
                // now follow
                tempDb.removeFollower(me, otherUser)
                Ok("SUCCESS")
            }
        }.getOrElse({
            // this request did not come from a form. Just take the user back to the normal page
            Redirect(routes.Application.index)
        })
  }

  def getFollowList = Action {req =>
        var myEmail = getCurrUserEmail(req)
        tempDb.connectToDatabase()
        var me = tempDb.fetchUser(myEmail)
        var searchRes = tempDb.getFollowList(me)
        if(searchRes.isEmpty)
        {
            // user not found
            Ok("")
        } else {
            // could not log user in
            Ok(convertUsersToJson(searchRes.to[collection.mutable.Seq]))
        }
  }

  def getFollowMeList = Action {req =>
        var myEmail = getCurrUserEmail(req)
        tempDb.connectToDatabase()
        var me = tempDb.fetchUser(myEmail)
        var searchRes = tempDb.getFollowMeList(me)
        if(searchRes.isEmpty)
        {
            // user not found
            Ok("")
        } else {
            // could not log user in
            Ok(convertUsersToJson(searchRes.to[collection.mutable.Seq]))
        }
  }

  // this pops up a page that allows us to follow a user or just remove as follower
  def viewUserError = Action { req =>
          if(!isLoggedIn(req))
          {
              Redirect(routes.loginController.index)
          } else {
              Redirect(routes.Application.index)
          }
  }

  // this pops up a page that allows us to follow a user or just remove as follower
  def viewUser (email: String) = Action { req =>
          if(!isLoggedIn(req))
          {
              Redirect(routes.loginController.index)
          } else {

               var myEmail = getCurrUserEmail(req)

              tempDb.connectToDatabase()
              var otherUser = tempDb.fetchUser(email)
              var me = tempDb.fetchUser(myEmail)
              if(otherUser == null)
              {
                  // user not found
                  Redirect(routes.Application.index)
              } else {
                  // could not log user in
                  Ok(views.html.userViewer(otherUser, tempDb.isFollower(me, otherUser), tempDb.iFollow(me, otherUser), myEmail == otherUser.email))
              }

          }
  }

  def newPostFileUpload = Action(parse.multipartFormData) { request =>
       request.body.file("fileUpload").map { newFile =>

       tempDb.connectToDatabase()
       var myEmail = getCurrUserEmail(request)
       var me = tempDb.fetchUser(myEmail)

         if(me != null)
         {

         val filename = newFile.filename
          val contentType = newFile.contentType.get

          var fileUrl = "userUploads/"+me.id+"/"+newFile.filename



          // create a new directory
          /*var dir = new File("server/public/userUploads/"+me.id+"/");
          dir.mkdir();

          // delete everything in directory
          for(currFile <- dir.listFiles())
          {
            if (!currFile.isDirectory())
            {
               currFile.delete();
            }
          }
          */

          var uploadFileObject = new fileUploader()

          var fileSavedAs = newFile.filename.split("\\.")(1)

          println(fileSavedAs)


          if(uploadFileObject.upload(newFile.ref, me.id+"."+fileSavedAs)){
              Ok("File has been uploaded")
          } else
          {
             Ok("ERROR")
          }

        //  newFile.ref.moveTo(new File("../../web/digest/userUploads/"+me.id+"/"+newFile.filename))

        } else {
               Ok("")
        }
      }.getOrElse {
         Redirect(routes.Application.index)
      }
 }

def uploadCaption =  Action { req =>
      val postValues = req.body.asFormUrlEncoded
      postValues.map { args  =>
          // get the email of the other user
          val caption = args("caption").head

          tempDb.connectToDatabase()
          var myEmail = getCurrUserEmail(req)
          var me = tempDb.fetchUser(myEmail)

          val url =  "http://playgroup12345.000webhostapp.com/"+me.id+"."+ args("filename").head.split("\\.")(1)
          val filetype = args("filetype").head
          if(me == null)
          {
              // user not found
              Ok("")
          } else {
              // now follow
              tempDb.newPostUpload(me, caption, url, filetype)
              Ok("SUCCESS")
          }
      }.getOrElse({
          // this request did not come from a form. Just take the user back to the normal page
          Redirect(routes.Application.index)
      })
}


    def getMyPost = Action {req =>
          var myEmail = getCurrUserEmail(req)
          tempDb.connectToDatabase()
          var me = tempDb.fetchUser(myEmail)
          if(me != null)
          {
            var myPost = tempDb.getPost(me)
            if(myPost == null)
            {
                // user not found
                Ok("")
            } else {
                // could not log user in
                Ok(converPostToJson(myPost))
            }
          } else {
             Ok("")
          }
    }

    def convertPostsToJson(posts: scala.collection.Seq[post]): JsValue = Json.toJson(posts)
    def getPosts = Action {req =>
          var myEmail = getCurrUserEmail(req)
          tempDb.connectToDatabase()
          var me = tempDb.fetchUser(myEmail)
          if(me != null)
          {
            var followedPosts = tempDb.getPosts(me)
            if(followedPosts == null)
            {
                // user not found
                Ok("")
            } else {
                // returns post of users i follow
                Ok(convertPostsToJson(followedPosts))
            }
          } else {
             Ok("")
          }
    }

    // when user clicks like
    def likePost (postId: String) = Action {req =>
            var myEmail = getCurrUserEmail(req)
            tempDb.connectToDatabase()
            var me = tempDb.fetchUser(myEmail)

            if(me != null)
            {
              tempDb.likePost(postId, me)
              Ok(Json.toJson("Unlike"))
            } else {
               Ok(Json.toJson(""))
            }

    }

    // when user clicks like
    def unlikePost (postId: String) = Action {req =>
            var myEmail = getCurrUserEmail(req)
            tempDb.connectToDatabase()
            var me = tempDb.fetchUser(myEmail)

            if(me != null)
            {
              tempDb.unlikePost(postId, me)
              Ok(Json.toJson("Like"))
            } else {
               Ok(Json.toJson(""))
            }

    }

    def isPostLiked (postId: String) = Action {req =>
            var myEmail = getCurrUserEmail(req)
            tempDb.connectToDatabase()
            var me = tempDb.fetchUser(myEmail)

            if(me != null)
            {
              if(tempDb.isPostLiked(postId, me)){
               Ok(Json.toJson("YES"))

              } else
              {
                Ok(Json.toJson("NO"))
              }
            } else {
                Ok(Json.toJson("NO"))
            }

    }

    // a list of users who liked a specific post
    def getPostLikesUserList (postId: String) = Action {req =>
            tempDb.connectToDatabase()
            var searchRes = tempDb.getListOfUserLikes(postId)


            Ok(convertUsersToJson(searchRes.to[collection.mutable.Seq]))

    }



    // list of users who liked my post
    def getListOfUserLikes = Action {req =>
        var myEmail = getCurrUserEmail(req)
        tempDb.connectToDatabase()
        var me = tempDb.fetchUser(myEmail)
        if(me != null)
        {
            var myPost = tempDb.getPost(me)
            var searchRes = tempDb.getListOfUserLikes(myPost)
            if(searchRes.isEmpty)
            {
                // user not found
                Ok("")
            } else {
                // return user likes for my post
                Ok(convertUsersToJson(searchRes.to[collection.mutable.Seq]))
            }
        } else {
             Ok("")
        }
    }

    def deleteMyPost = Action {req =>
          var myEmail = getCurrUserEmail(req)
          tempDb.connectToDatabase()
          var me = tempDb.fetchUser(myEmail)


          tempDb.deletePost(me)

          // create a new directory
          var dir = new File("server/public/userUploads/"+me.id+"/");
          dir.mkdir();

          // delete everything in directory
          for(currFile <- dir.listFiles())
          {
            if (!currFile.isDirectory())
            {
               currFile.delete();
            }
          }

          Ok("")


    }

    def changeProfilePhoto =  Action(parse.multipartFormData) { request =>
         request.body.file("fileUpload").map { newFile =>

         tempDb.connectToDatabase()
         var myEmail = getCurrUserEmail(request)
         var me = tempDb.fetchUser(myEmail)

          if(me != null)
          {

           val filename = newFile.filename
            val contentType = newFile.contentType.get

            // create a new directory
          /*  var dir = new File("server/public/userProfilePhotos/"+me.id+"/");
            dir.mkdir();

            // delete everything in directory
            for(currFile <- dir.listFiles())
            {
              if (!currFile.isDirectory())
              {
                 currFile.delete();
              }
            }
            */


              var uploadFileObject = new fileUploader()

          val url =  "http://playgroup12345.000webhostapp.com/profile/"+me.id+"."+ newFile.filename.split("\\.")(1)


            uploadFileObject.upload(newFile.ref,"profile/"+me.id+"."+ newFile.filename.split("\\.")(1))

            tempDb.changeProfilePhoto(me, url)

            println("Changed profile")

            Ok(url)
          } else {
              Ok("FAIL")
          }
        }.getOrElse {
           Ok("FAIL")
        }

   }

    def deleteProfilePhoto = Action {req =>
          var myEmail = getCurrUserEmail(req)
          tempDb.connectToDatabase()
          var me = tempDb.fetchUser(myEmail)

          if(me == null)
          {
            Ok("")
          } else {

              // create a new directory
            /*  var dir = new File("server/public/userProfilePhotos/"+me.id+"/");
              dir.mkdir();

              // delete everything in directory
              for(currFile <- dir.listFiles())
              {
                if (!currFile.isDirectory())
                {
                   currFile.delete();
                }
              }
              */

              tempDb.changeProfilePhoto(me, "default")

              Ok("SUCCESS")
          }


    }

    def newFullName = Action {req =>
      val postValues = req.body.asFormUrlEncoded
      postValues.map { args  =>
          // get the email of the other user
          val newName = args("name").head

          tempDb.connectToDatabase()
          var myEmail = getCurrUserEmail(req)
          var me = tempDb.fetchUser(myEmail)

          if(me == null)
          {
              // user not found
              Ok("")
          } else {
              // now follow
              tempDb.newFullName(me, newName)
              Ok("SUCCESS")
          }
      }.getOrElse({
          // this request did not come from a form. Just take the user back to the normal page
          Redirect(routes.Application.index)
      })
    }


    def updatePassword = Action {req =>
      val postValues = req.body.asFormUrlEncoded
      postValues.map { args  =>
          // get the email of the other user
          val currPassword = args("currPassword").head
          val newPassword = args("newPassword").head
          val confPassword = args("confPassword").head

          tempDb.connectToDatabase()
          var myEmail = getCurrUserEmail(req)
          var me = tempDb.fetchUser(myEmail)

          if(me == null)
          {
            Ok("ERROR")
          }
          else if(me.password != currPassword)
          {
            Ok("!PASSWORD")
          } else if (newPassword != confPassword)
          {
            Ok("!CONFPASSWORD")
          } else {
            tempDb.updatePassword(me, newPassword)
            Ok("SUCCESS")
          }
      }.getOrElse({
          // this request did not come from a form. Just take the user back to the normal page
          Redirect(routes.Application.index)
      })
    }

  def deleteAccount = Action {req =>
        var myEmail = getCurrUserEmail(req)
        tempDb.connectToDatabase()
        var me = tempDb.fetchUser(myEmail)
        if(me != null)
        {
          tempDb.deleteAccount(me)
          Ok("SUCCESS").withNewSession
        } else {
          Ok("SUCCESS").withNewSession
        }
  }

  def logout = Action { req =>
        Redirect(routes.loginController.index).withNewSession
  }

}
