package integration

import org.specs2.mutable.Specification
import play.api.test._
import play.api.test.Helpers._
import play.api.libs.ws._
import play.api.Play
import play.api.Play.current
import play.api.libs.json.Json
import unit.org.stock.manager.test.DatabaseSetupBefore

import model.{DB, TestCase}
import scala.slick.session.Database
import Database.threadLocalSession

class TestCaseControllerSpec extends PlaySpecification with DatabaseSetupBefore {
	sequential
	import DB.dal._
	import DB.dal.profile.simple._
	import model.JsonHelper._

	"GET to /api/<owner>/<project>/<testSuiteId>" should {
		"return latest 10 test cases" in new WithServer {
			insert10TestCases ()
			val response = await(WS.url(s"http://localhost:$port/api/pussinboots/bankapp/1").get, 10000)
			response.status must equalTo(OK)
			val testCases = Json.fromJson[JsonFmtListWrapper[TestCase]](response.json).get
			testCases.count must equalTo(11)
			testCases.items.length must equalTo(11)
			for(testCase <- testCases.items) {
				testCase.testSuiteId must equalTo(1)
			}
		}
	}

	def insert10TestCases() {
		DB.db withSession {
			for(i <- 2 to 11)
				TestCases.insert(TestCase(None, 1, "pussinboots", "bankapp", s"testcase $i", "testclass",1000))
		}	
	}
}