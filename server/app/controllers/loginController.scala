package controllers

import javax.inject._

import play.api.mvc._
import model._
import db._
import util._

@Singleton
class loginController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {


  // this allows us to do all the database stuff
  var tempDb = new myDbConnector();
  var sendEmailObject = new emailSender()

    def isLoggedIn (req: RequestHeader): Boolean = {
          req.session.get("email").map { user =>
              return true;
          }.getOrElse{
           return false;
          }
    }

  // this is the login/register/forgotPassword logic
  def index = Action { req =>
    if(isLoggedIn(req))
    {
        Redirect(routes.Application.index)
    } else {
        Ok(views.html.loginForm(""))
    }
  }

  def register = Action { req =>
      if(isLoggedIn(req))
      {
          Redirect(routes.Application.index)
      } else
      {
        Ok(views.html.registerForm(""))
      }

  }

  def forgotPassword = Action { req =>
      if(isLoggedIn(req))
      {
          Redirect(routes.Application.index)
      } else {
         Ok(views.html.forgotPasswordForm(""))
      }

  }

  def validateLogIn = Action { req =>
      if(isLoggedIn(req))
      {
          Redirect(routes.Application.index)
      }

      val postValues = req.body.asFormUrlEncoded
      postValues.map { args  =>
          val email = args("email").head
          val password = args("password").head
          tempDb.connectToDatabase()
          if(tempDb.validateLogIn(email, password))
          {
              // log user in
              Redirect(routes.Application.index()).withSession("email" -> email)
          } else {
              // could not log user in
              Ok(views.html.loginForm("Please try again, wrong email password combo!"))
          }
      }.getOrElse({
          // this request did not come from a form. Just take the user back to the normal page
          Redirect(routes.loginController.index)
      })
  }

  def registerUser = Action { req =>
      if(isLoggedIn(req))
      {
          Redirect(routes.Application.index)
      }

      val postValues = req.body.asFormUrlEncoded
      postValues.map { args  =>
          val fullname = args("fullname").head
          val email = args("email").head
          val password = args("password").head
          val confpassword = args("confpassword").head

          if(password != confpassword)
          {
              Ok(views.html.registerForm("Passwords do not match"))
          } else
          {
              tempDb.connectToDatabase()
              if(tempDb.createAccount(email, password, fullname))
              {
                  // log user in
                  Redirect(routes.Application.index()).withSession("email" -> email)
              } else {
                  // could not log user in
                  Ok(views.html.registerForm("The user already exists!"))
              }
          }
      }.getOrElse({
          // this request did not come from a form. Just take the user back to the normal page
          Redirect(routes.loginController.register)
      })
  }

  def sendForgottenPassword =  Action { req =>
      if(isLoggedIn(req))
      {
          Redirect(routes.Application.index)
      }

      val postValues = req.body.asFormUrlEncoded
      postValues.map { args  =>

          val email = args("email").head
               tempDb.connectToDatabase()
              if(tempDb.doesAccountExist(email))
              {
                  // [TODO] get the password and send it
                  var currUser = tempDb.fetchUser(email)
                if(sendEmailObject.send(currUser.fullname, email, "Your Password","Here is your password "+currUser.fullname+": "+currUser.password
                  , /*html=*/""))
                  {
                    Ok(views.html.forgotPasswordForm("We have sent you an email with your password"))
                  } else
                  {
                    Ok(views.html.forgotPasswordForm("Sorry we could not send you an email right now. Try Later :("))
                  }

              } else {
                  // could not log user in
                  Ok(views.html.forgotPasswordForm("The user with this email does not exists"))
              }
      }.getOrElse({
          // this request did not come from a form. Just take the user back to the normal page
          Redirect(routes.loginController.forgotPassword)
      })
  }



}
