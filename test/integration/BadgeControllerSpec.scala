package integration

import org.specs2.mutable.Specification
import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json.Json
import play.api.libs.ws._
import play.api.Play
import play.api.Play.current
import org.specs2.execute.AsResult
import unit.test.utils.DatabaseSetupBefore
import test.E2ETestGlobal
import unit.test.utils.Betamax
import co.freeside.betamax.TapeMode

import model.{DB, Build}
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession

import controllers.BadgeController

class BadgeControllerSpec extends PlaySpecification with DatabaseSetupBefore {
	import DB.dal._
	import DB.dal.profile.simple._
	def setupTestData[T: AsResult](f: => T) = {running(FakeApplication()) {
	      E2ETestGlobal.onStart(Play.application)
	      DB.db withDynSession {
	        AsResult(f)
	      }
	    }
	}

  "BadgeController" should {
  	"status endpoint" should {
			import model.JsonHelper._
			"for valid build return latest build as json" in new WithServer {
			E2ETestGlobal.onStart(Play.application)
		    val response = await(WS.url(s"http://localhost:$port/api/pussinboots/bankapp/status").get)
			response.status must equalTo(OK)
			val build = Json.fromJson[Build](response.json).get
			build.owner must equalTo("pussinboots")
			build.project must equalTo("bankapp")
			build.owner must equalTo("pussinboots")
			build.buildNumber must equalTo(11)
			build.tests must equalTo(Some(11))
			build.failures must equalTo(Some(1))
			build.errors must equalTo(Some(1))
			build.travisBuildId must equalTo(Some("11"))
		}	

		"for not existing projects return http status 404 with error json object" in new WithServer {
		    val response = await(WS.url(s"http://localhost:$port/api/other/other/status").get)
			response.status must beEqualTo(NOT_FOUND)
			(response.json \ "error").as[Int] must equalTo(404)
			(response.json \ "error_message").as[String] must equalTo("For other other there exists no build.")
			
		}	
  	}

  	"badge endpoint" should {
		"tests passed build return tests passed badge" in {setupTestData{
		    val build = Builds.findByBuildNumber("pussinboots", "bankapp", 7).firstOption
		    val badgeUrl = BadgeController.badgeUrl(build)
		    val desc = "passed"
		    val count = 7
		    val color = "brightgreen"
		    badgeUrl must equalTo(s"http://img.shields.io/badge/test-$desc%20$count-$color.svg")
		  }	
		}
		
		"tests failed build return tests failed badge" in {setupTestData{
		    val build = Builds.findByBuildNumber("pussinboots", "bankapp", 9).firstOption
		    val badgeUrl = BadgeController.badgeUrl(build)
		    val desc = "failed"
		    val count = 1
		    val color = "yellow"
		    badgeUrl must equalTo(s"http://img.shields.io/badge/test-$desc%20$count-$color.svg")
		  }	
		}
		
		"tests errored build return tests error badge" in {setupTestData{ 
		    val build = Builds.findByBuildNumber("pussinboots", "bankapp", 10).firstOption
		    val badgeUrl = BadgeController.badgeUrl(build)
		    val desc = "error"
		    val count = 1
		    val color = "red"
		    badgeUrl must equalTo(s"http://img.shields.io/badge/test-$desc%20$count-$color.svg")
		  }	
		}
		
		"build not exists return tests unknown" in {setupTestData{ 
		    val build = Builds.findByBuildNumber("pussinboots", "bankapp", 200).firstOption
		    val badgeUrl = BadgeController.badgeUrl(build)
		    badgeUrl must equalTo("http://img.shields.io/badge/test-unknown-lightgrey.svg")
		  }	
		}
        
        "return passed badge from shields.io" in Betamax(tape="shields", mode=Some(TapeMode.READ_ONLY)) {
            setupTestData { 
                val fakeRequest = FakeRequest()
                val badgeSvg = BadgeController.badge("pussinboots", "bankapp")(fakeRequest)
                contentAsString(badgeSvg) must beEqualTo("""<svg xmlns="http://www.w3.org/2000/svg" width="81" height="18"><linearGradient id="a" x2="0" y2="100%"><stop offset="0" stop-color="#fff" stop-opacity=".7"/><stop offset=".1" stop-color="#aaa" stop-opacity=".1"/><stop offset=".9" stop-opacity=".3"/><stop offset="1" stop-opacity=".5"/></linearGradient><rect rx="4" width="81" height="18" fill="#555"/><rect rx="4" x="31" width="50" height="18" fill="#e05d44"/><path fill="#e05d44" d="M31 0h4v18h-4z"/><rect rx="4" width="81" height="18" fill="url(#a)"/><g fill="#fff" text-anchor="middle" font-family="DejaVu Sans,Verdana,Geneva,sans-serif" font-size="11"><text x="16.5" y="13" fill="#010101" fill-opacity=".3">test</text><text x="16.5" y="12">test</text><text x="55" y="13" fill="#010101" fill-opacity=".3">error 1</text><text x="55" y="12">error 1</text></g></svg>""")
            }
		}
	  }
  }
}
