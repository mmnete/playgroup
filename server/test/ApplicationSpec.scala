import org.junit.runner._
import org.specs2.runner._
import play.api.test._

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
@RunWith(classOf[JUnitRunner])
class ApplicationSpec() extends PlaySpecification {

  "Application" should {

    "send 404 on a bad request" in new WithApplication {
      route(app, FakeRequest(GET, "/boum")) must beSome.which (status(_) == NOT_FOUND)
    }

    "redirect from the index page when session is not set" in new WithApplication {
      val home = route(app, FakeRequest(GET, "/")).get

      status(home) mustEqual 303 // 303 is a redirect response
      redirectLocation(home).head must contain ("/login")
    }

    "render the index page when session is set" in new WithApplication {
      val home = route(app, FakeRequest(GET, "/").withSession("email" -> "test@test.com")).get

      status(home) mustEqual 200
      contentAsString(home) must contain ("What is on your mind?")
    }
  }
}
