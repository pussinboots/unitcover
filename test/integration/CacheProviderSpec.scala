package integration

import org.specs2.mutable.Specification
import play.api.test._
import play.api.libs.ws._
import play.api.Play
import play.api.Play.current
import play.api.libs.json.Json
import play.api.cache.Cache
import play.api.test.Helpers._
import org.specs2.execute.AsResult
import unit.test.utils.DatabaseSetupBefore
import test.E2ETestGlobal
import controllers.BadgeController
import controllers.BuildController
import controllers.TestSuiteController
import unit.test.utils.Betamax
import co.freeside.betamax.TapeMode
import unit.test.utils.DatabaseSetupBefore

import model.{DB, Build}
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession
    
class CacheProviderSpec extends PlaySpecification with DatabaseSetupBefore {
    sys.props.+=("Database" -> "h2")
    import DB.dal._
	import DB.dal.profile.simple._
	def setupTestData[T: AsResult](f: => T) = {running(FakeApplication()) {
          E2ETestGlobal.onStart(Play.application)
	      DB.db withDynSession {
	        AsResult(f)
	      }
	    }
	}
	
	"cache is used" should {
		"BadgeController retrieve badge from cache" in {
            running(FakeApplication()) {
                Cache.set("pussinboots-cachedbankapp-badge", "cached badge")
                Thread.sleep(1000) //wait for Cache remove
                val fakeRequest = FakeRequest()
                val badgeSvg = BadgeController.badge("pussinboots", "cachedbankapp")(fakeRequest)
                contentAsString(badgeSvg) must beEqualTo("cached badge")
            }
	}
        
        "BadgeController retrieve unknown badge from database" in Betamax(tape="shieldsunknow", mode=Some(TapeMode.READ_ONLY)) {
            setupTestData { 
                Cache.remove("unknow-unknown-badge")
                Thread.sleep(1000) //wait for Cache remove
                val cachedBadge = Cache.get(s"unknow-unknown-badge")
                cachedBadge must beNone
                val fakeRequest = FakeRequest()
                val badgeSvg = BadgeController.badge("unknow", "unknown")(fakeRequest)
                contentAsString(badgeSvg) must beEqualTo("""<svg xmlns="http://www.w3.org/2000/svg" width="92" height="18"><linearGradient id="a" x2="0" y2="100%"><stop offset="0" stop-color="#fff" stop-opacity=".7"/><stop offset=".1" stop-color="#aaa" stop-opacity=".1"/><stop offset=".9" stop-opacity=".3"/><stop offset="1" stop-opacity=".5"/></linearGradient><rect rx="4" width="92" height="18" fill="#555"/><rect rx="4" x="31" width="61" height="18" fill="#9f9f9f"/><path fill="#9f9f9f" d="M31 0h4v18h-4z"/><rect rx="4" width="92" height="18" fill="url(#a)"/><g fill="#fff" text-anchor="middle" font-family="DejaVu Sans,Verdana,Geneva,sans-serif" font-size="11"><text x="16.5" y="13" fill="#010101" fill-opacity=".3">test</text><text x="16.5" y="12">test</text><text x="60.5" y="13" fill="#010101" fill-opacity=".3">unknown</text><text x="60.5" y="12">unknown</text></g></svg>""")
            }
	}
                                                         
        "BuildController endBuild should invalidate the badge cache" in Betamax(tape="shieldspassed", mode=Some(TapeMode.READ_ONLY)) {
            setupTestData {
                Cache.remove("pussinboots-bankapp-badge")
                Thread.sleep(1000) //wait for Cache remove
                val fakeRequest = FakeRequest()
                val badgeSvgBefore = BadgeController.badge("pussinboots", "bankapp")(fakeRequest)
                contentAsString(badgeSvgBefore) must beEqualTo("""<svg xmlns="http://www.w3.org/2000/svg" width="81" height="18"><linearGradient id="a" x2="0" y2="100%"><stop offset="0" stop-color="#fff" stop-opacity=".7"/><stop offset=".1" stop-color="#aaa" stop-opacity=".1"/><stop offset=".9" stop-opacity=".3"/><stop offset="1" stop-opacity=".5"/></linearGradient><rect rx="4" width="81" height="18" fill="#555"/><rect rx="4" x="31" width="50" height="18" fill="#e05d44"/><path fill="#e05d44" d="M31 0h4v18h-4z"/><rect rx="4" width="81" height="18" fill="url(#a)"/><g fill="#fff" text-anchor="middle" font-family="DejaVu Sans,Verdana,Geneva,sans-serif" font-size="11"><text x="16.5" y="13" fill="#010101" fill-opacity=".3">test</text><text x="16.5" y="12">test</text><text x="55" y="13" fill="#010101" fill-opacity=".3">error 1</text><text x="55" y="12">error 1</text></g></svg>""")
                
		val karmaTestResult = scala.xml.XML.loadFile(Play.getFile("test/resources/karma/test-results.xml"))
                val fakeRequestPost = FakeRequest("POST","/api/pussinboots/bankapp/builds")
                val buildResponse = BuildController.startBuild("pussinboots", "bankapp", None, None, None)(fakeRequestPost)
                status(buildResponse) must equalTo(OK)
                val buildNumber = (Json.parse(contentAsString(buildResponse)) \ "buildNumber").as[Int]
		buildNumber must beEqualTo(12)
                
		val fakeRequestTestSuitePost = FakeRequest().withXmlBody(karmaTestResult).map(_.xml)
                val rsp = TestSuiteController.saveTestSuite("pussinboots", "bankapp", buildNumber)(fakeRequestTestSuitePost)
                await(BuildController.endBuild("pussinboots", "bankapp", buildNumber)(fakeRequestPost))
                Thread.sleep(1000) //wait for Cache remove in endBuild method
		val cachedBadge = Cache.get(s"pussinboots-bankapp-badge")
                cachedBadge must beNone
                
		val badgeSvg = BadgeController.badge("pussinboots", "bankapp")(fakeRequest)
                println(contentAsString(badgeSvg))
                Cache.remove("pussinboots-bankapp-badge")
                contentAsString(badgeSvg) must beEqualTo("""<svg xmlns="http://www.w3.org/2000/svg" width="92" height="18"><linearGradient id="a" x2="0" y2="100%"><stop offset="0" stop-color="#fff" stop-opacity=".7"/><stop offset=".1" stop-color="#aaa" stop-opacity=".1"/><stop offset=".9" stop-opacity=".3"/><stop offset="1" stop-opacity=".5"/></linearGradient><rect rx="4" width="92" height="18" fill="#555"/><rect rx="4" x="31" width="61" height="18" fill="#4c1"/><path fill="#4c1" d="M31 0h4v18h-4z"/><rect rx="4" width="92" height="18" fill="url(#a)"/><g fill="#fff" text-anchor="middle" font-family="DejaVu Sans,Verdana,Geneva,sans-serif" font-size="11"><text x="16.5" y="13" fill="#010101" fill-opacity=".3">test</text><text x="16.5" y="12">test</text><text x="60.5" y="13" fill="#010101" fill-opacity=".3">passed 6</text><text x="60.5" y="12">passed 6</text></g></svg>""")
            }
		}
    }
}
