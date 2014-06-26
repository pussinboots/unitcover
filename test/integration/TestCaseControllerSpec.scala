package integration

import org.specs2.mutable.Specification
import play.api.test._
import play.api.test.Helpers._
import play.api.libs.ws._
import play.api.Play
import play.api.Play.current
import play.api.libs.json.Json
import unit.test.utils.DatabaseSetupBefore

import model.{DB, TestCase, TestCaseJson, TestSuite, Message}
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession

class TestCaseControllerSpec extends PlaySpecification with DatabaseSetupBefore {
	implicit def toOption[A](value: A) : Option[A] = Some(value)
	implicit val timeout = 10000
	import DB.dal
	import DB.dal.profile.simple._
	import model.JsonHelper._

	"GET to /api/<owner>/<project>/<testSuiteId>" should {
		"return latest 10 test cases" in new WithServer {
			insert10TestCases (testSuiteId=1)
			val response = await(WS.url(s"http://localhost:$port/api/pussinboots/bankapp/testcases/1").get)
			response.status must equalTo(OK)
			val testCases = Json.fromJson[JsonFmtListWrapper[TestCaseJson]](response.json).get
			testCases.count must equalTo(11)
			testCases.items.length must equalTo(11)
			for(testCase <- testCases.items) {
				testCase.testSuiteId must equalTo(1)
			}
			testCases.items(6).id must equalTo(Some(5))
			testCases.items(6).detailFailureMessage must equalTo(Some("detail failure message"))
			testCases.items(0).id must equalTo(Some(11))
			testCases.items(0).detailErrorMessage must equalTo(Some("detail error message"))
		}
	}

	"given build with two test suites" should {
		"GET to /api/<owner>/<project>/<testSuiteId>" should {
			"return only 10 test cases for the specified testsuiteId" in new WithServer {
				insert10TestCases(testSuiteId=1)
				DB.db withDynSession {
					dal.testSuites.insert(TestSuite(None, 1, "pussinboots", "bankapp", "testsuite", 5,1,2,1000.0))
	   			}
				insert10TestCases(testSuiteId=2)
				val response = await(WS.url(s"http://localhost:$port/api/pussinboots/bankapp/testcases/1").get)
				response.status must equalTo(OK)
				val testCases = Json.fromJson[JsonFmtListWrapper[TestCaseJson]](response.json).get
				println(testCases)
				testCases.items.length must equalTo(11)
			}
		}
	}

	def insert10TestCases(testSuiteId: Long) {
		DB.db withDynSession {
			for(i <- 2 to 5) {
				val testCase = dal.testCaseForInsert.insert(TestCase(None, testSuiteId, "pussinboots", "bankapp", s"testcase $i", "testclass",1000.0))
				dal.messages.insert(Message(testCase.id.get, "detail failure message", 0))
			}
			for(i <- 6 to 11) {
				val testCase = dal.testCaseForInsert.insert(TestCase(None, testSuiteId, "pussinboots", "bankapp", s"testcase $i", "testclass",1000.0))
				dal.messages.insert(Message(testCase.id.get, "detail error message", 1))
			}
		}	
	}
}