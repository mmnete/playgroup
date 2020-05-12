//import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import javax.inject.Inject

import play.api.i18n.Messages
import play.api.mvc._
import play.api.test._
import controllers._
import db._
import model._
import util._
import play.api.test.Helpers._
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar

import scala.concurrent.Future

 class LoginRegistrationForgotPasswordTestSpec extends PlaySpecification with Results with MockitoSugar {

   "Log In/Registration/Forgot Password Page" should {

     "show a valid login form by default" in {
       val controller             = new loginController(Helpers.stubControllerComponents())
       val result: Future[Result] = controller.index().apply(FakeRequest())
       val bodyText: String       = contentAsString(result)
       bodyText must contain ("Log In")
       bodyText must contain ("/register")
       bodyText must contain ("/forgotPassword")
       val containsLoginLink = bodyText.contains("/login")
       containsLoginLink mustEqual false
     }

     "redirect from login form when session is set" in {
       val controller             = new loginController(Helpers.stubControllerComponents())
       val result: Future[Result] = controller.index().apply(FakeRequest().withSession("email" -> "test@test.com"))
       val bodyText: String       = contentAsString(result)

       status(result) mustEqual 303
       redirectLocation(result).head must contain ("/")
     }

     "show a valid register form by default" in {
       val controller             = new loginController(Helpers.stubControllerComponents())
       val result: Future[Result] = controller.register().apply(FakeRequest())
       val bodyText: String       = contentAsString(result)
       bodyText must contain ("Register")
       bodyText must contain ("/login")
       bodyText must contain ("/forgotPassword")
       val containsLoginLink = bodyText.contains("/register")
       containsLoginLink mustEqual false
     }

     "redirect from register form when session is set" in {
       val controller             = new loginController(Helpers.stubControllerComponents())
       val result: Future[Result] = controller.register().apply(FakeRequest().withSession("email" -> "test@test.com"))
       val bodyText: String       = contentAsString(result)

       status(result) mustEqual 303
       redirectLocation(result).head must contain ("/")
     }

     "show a valid forgotPassword form by default" in {
       val controller             = new loginController(Helpers.stubControllerComponents())
       val result: Future[Result] = controller.forgotPassword().apply(FakeRequest())
       val bodyText: String       = contentAsString(result)
       bodyText must contain ("Forgot Password")
       bodyText must contain ("/login")
       bodyText must contain ("/register")
       val containsLoginLink = bodyText.contains("/forgotPassword")
       containsLoginLink mustEqual false
     }

     "redirect from forgotPassword form when session is set" in {
       val controller             = new loginController(Helpers.stubControllerComponents())
       val result: Future[Result] = controller.forgotPassword().apply(FakeRequest().withSession("email" -> "test@test.com"))
       val bodyText: String       = contentAsString(result)

       status(result) mustEqual 303
       redirectLocation(result).head must contain ("/")
     }

     "redirect from validateLogIn when session is set" in {
       val controller             = new loginController(Helpers.stubControllerComponents())
       val result: Future[Result] = controller.validateLogIn().apply(FakeRequest().withSession("email" -> "test@test.com"))
       val bodyText: String       = contentAsString(result)

       status(result) mustEqual 303
       redirectLocation(result).head must contain ("/")
     }

    // we fake a database response
     "redirect to index for valid user in the validateLogIn route" in {

      // fake the return values of the database object
       val mockDbConnector = mock[myDbConnector]
       when(mockDbConnector.connectToDatabase).thenReturn(true)
       when(mockDbConnector.validateLogIn("test_email","test_pass")).thenReturn(true)

       val controller             = new loginController(Helpers.stubControllerComponents())
       controller.tempDb = mockDbConnector
       val result: Future[Result] = controller.validateLogIn().apply(FakeRequest().withFormUrlEncodedBody(
          "email" -> "test_email",
          "password" -> "test_pass"
        ))
       val bodyText: String       = contentAsString(result)

       status(result) mustEqual 303
       redirectLocation(result).head must contain ("/")
     }

     // we fake a database response
      "stay in login page for invalid user in the validateLogIn route" in {

       // fake the return values of the database object
        val mockDbConnector = mock[myDbConnector]
        when(mockDbConnector.connectToDatabase).thenReturn(true)
        when(mockDbConnector.validateLogIn("test_email","test_pass")).thenReturn(true)

        val controller             = new loginController(Helpers.stubControllerComponents())
        controller.tempDb = mockDbConnector
        val result: Future[Result] = controller.validateLogIn().apply(FakeRequest().withFormUrlEncodedBody(
           "email" -> "wrong_email",
           "password" -> "wrong_pass"
         ))
        val bodyText: String       = contentAsString(result)

        status(result) mustEqual 200
        bodyText must contain ("Log In")
        bodyText must contain ("/register")
        bodyText must contain ("/forgotPassword")
        val containsLoginLink = bodyText.contains("/login")
        containsLoginLink mustEqual false
      }


      "redirect from registerUser when session is set" in {
        val controller             = new loginController(Helpers.stubControllerComponents())
        val result: Future[Result] = controller.registerUser().apply(FakeRequest().withSession("email" -> "test@test.com"))
        val bodyText: String       = contentAsString(result)

        status(result) mustEqual 303
        redirectLocation(result).head must contain ("/")
      }

     // we fake a database response
      "redirect to index for valid user registration in the registerUser route" in {

       // fake the return values of the database object
        val mockDbConnector = mock[myDbConnector]
        when(mockDbConnector.connectToDatabase).thenReturn(true)
        when(mockDbConnector.createAccount("test_email", "test_password", "test_fullname")).thenReturn(true)

        val controller             = new loginController(Helpers.stubControllerComponents())
        controller.tempDb = mockDbConnector
        val result: Future[Result] = controller.registerUser().apply(FakeRequest().withFormUrlEncodedBody(
           "fullname" -> "test_fullname",
          "email" -> "test_email",
           "password" -> "test_password",
           "confpassword" -> "test_password"
         ))

        val bodyText: String       = contentAsString(result)

        status(result) mustEqual 303
        redirectLocation(result).head must contain ("/")
      }

      // we fake a database response
       "redirect to index for invalid user registration passwords do not match in the registerUser route" in {

        // fake the return values of the database object
         val mockDbConnector = mock[myDbConnector]
         when(mockDbConnector.connectToDatabase).thenReturn(true)
         when(mockDbConnector.createAccount("test_email", "test_password", "test_fullname")).thenReturn(true)

         val controller             = new loginController(Helpers.stubControllerComponents())
         controller.tempDb = mockDbConnector
         val result: Future[Result] = controller.registerUser().apply(FakeRequest().withFormUrlEncodedBody(
            "fullname" -> "test_fullname",
           "email" -> "test_email",
            "password" -> "test_password",
            "confpassword" -> "test_confpassword"
          ))

         val bodyText: String       = contentAsString(result)

         status(result) mustEqual 200
         bodyText must contain ("Passwords do not match")
       }

      // we fake a database response
       "stay in register page if user exists in the registerUser route" in {

        // fake the return values of the database object
         val mockDbConnector = mock[myDbConnector]
         when(mockDbConnector.connectToDatabase).thenReturn(true)
         when(mockDbConnector.doesAccountExist("test_email")).thenReturn(true)

         val controller             = new loginController(Helpers.stubControllerComponents())
         controller.tempDb = mockDbConnector
         val result: Future[Result] = controller.registerUser().apply(FakeRequest().withFormUrlEncodedBody(
         "fullname" -> "test_fullname",
        "email" -> "test_email",
         "password" -> "test_password",
         "confpassword" -> "test_confpassword"
          ))
         val bodyText: String       = contentAsString(result)

         status(result) mustEqual 200
         bodyText must contain ("Register")
         bodyText must contain ("/login")
         bodyText must contain ("/forgotPassword")
         val containsLoginLink = bodyText.contains("/register")
         containsLoginLink mustEqual false
       }


       // forgot password unit tests

       "redirect from sendForgottenPassword when session is set" in {
         val controller             = new loginController(Helpers.stubControllerComponents())
         val result: Future[Result] = controller.sendForgottenPassword().apply(FakeRequest().withSession("email" -> "test@test.com"))
         val bodyText: String       = contentAsString(result)

         status(result) mustEqual 303
         redirectLocation(result).head must contain ("/")
       }

      // we fake a database response
       "send email if the forgotPassword email input is valid" in {

        // fake the return values of the database object
         val mockDbConnector = mock[myDbConnector]
         when(mockDbConnector.connectToDatabase).thenReturn(true)
         when(mockDbConnector.doesAccountExist("test_email")).thenReturn(true)
         when(mockDbConnector.fetchUser("test_email")).thenReturn(new user("test_id", "test_email", "test_password", "test_fullname"))

         // create a fake email sender object
         val mockEmailSender = mock[emailSender]
         when(mockEmailSender.send("test_fullname", "test_email", "Your Password","Here is your password "+"test_fullname"+": "+"test_password"
           , /*html=*/"")).thenReturn(true)

         val controller             = new loginController(Helpers.stubControllerComponents())
         controller.tempDb = mockDbConnector
         controller.sendEmailObject = mockEmailSender
         val result: Future[Result] = controller.sendForgottenPassword().apply(FakeRequest().withFormUrlEncodedBody(
            "email" -> "test_email"
          ))
         val bodyText: String       = contentAsString(result)

         status(result) mustEqual 200
         bodyText must contain ("We have sent you an email with your password")
       }

       "reject sending email if the forgotPassword email input is invalid" in {

        // fake the return values of the database object
         val mockDbConnector = mock[myDbConnector]
         when(mockDbConnector.connectToDatabase).thenReturn(true)
         when(mockDbConnector.doesAccountExist("test_email")).thenReturn(false)
         when(mockDbConnector.fetchUser("test_email")).thenReturn(new user("test_id", "test_email", "test_password", "test_fullname"))

         // create a fake email sender object
         val mockEmailSender = mock[emailSender]
         when(mockEmailSender.send("test_fullname", "test_email", "Your Password","Here is your password "+"test_fullname"+": "+"test_password"
           , /*html=*/"")).thenReturn(true)

         val controller             = new loginController(Helpers.stubControllerComponents())
         controller.tempDb = mockDbConnector
         controller.sendEmailObject = mockEmailSender
         val result: Future[Result] = controller.sendForgottenPassword().apply(FakeRequest().withFormUrlEncodedBody(
            "email" -> "test_email"
          ))
         val bodyText: String       = contentAsString(result)

         status(result) mustEqual 200
         bodyText must contain ("The user with this email does not exists")
       }





   }

 }
