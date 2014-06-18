package integration

import org.specs2.mutable.Specification
import play.api.test._
import play.api.test.Helpers._
import play.api.libs.ws._
import play.api.Play
import play.api.Play.current
import play.api.libs.json.Json
import unit.org.stock.manager.test.DatabaseSetupBefore

import model.{DB, TestSuite, TestCase}
import scala.slick.session.Database
import Database.threadLocalSession

class TestSuiteControllerSpec extends PlaySpecification with DatabaseSetupBefore {
	sequential
	import DB.dal._
	import DB.dal.profile.simple._
	import model.JsonHelper._

	"POST to /api/<owner>/<project>" should {
		"with junit xml report all tests passed return http status 200 and store it in the db" in new WithServer { 
			val xmlString = scala.io.Source.fromFile(Play.getFile("test/resources/ApplicationSpec.xml")).mkString
			val response = await(WS.url(s"http://localhost:$port/api/pussinboots/bankapp").withHeaders("Content-Type" -> "text/xml").post(xmlString), 10000)
			response.status must equalTo(OK)
			val suiteId = (response.json \ "id").as[Int]
			DB.db withSession {
				checkTestSuite(suiteId)
				checkTestCases(suiteId)
			}
		}

		"with junit xml report one failure return http status 200 and store it in the db" in new WithServer { 
			val xmlString = scala.io.Source.fromFile(Play.getFile("test/resources/TestSuiteControllerSpecFailure.xml")).mkString
			val response = await(WS.url(s"http://localhost:$port/api/pussinboots/bankapp").withHeaders("Content-Type" -> "text/xml").post(xmlString), 10000)
			response.status must equalTo(OK)
			val suiteId = (response.json \ "id").as[Int]
			DB.db withSession {
				checkTestSuiteWithFailure(suiteId)
				checkTestCasesWithFailure(suiteId)
			}
		}

		"with junit xml report one error return http status 200 and store it in the db" in new WithServer { 
			val xmlString = scala.io.Source.fromFile(Play.getFile("test/resources/TestSuiteControllerSpecError.xml")).mkString
			val response = await(WS.url(s"http://localhost:$port/api/pussinboots/bankapp").withHeaders("Content-Type" -> "text/xml").post(xmlString), 10000)
			response.status must equalTo(OK)
			val suiteId = (response.json \ "id").as[Int]
			DB.db withSession {
				checkTestSuiteWithError(suiteId)
				checkTestCasesWithError(suiteId)
			}
		}
	}

	"GET to /api/<owner>/<project>" should {
		"return latest 10 test suites" in new WithServer {
			insert10TestSuites ()
			val response = await(WS.url(s"http://localhost:$port/api/pussinboots/bankapp").get)
			response.status must equalTo(OK)
			val testSuites = Json.fromJson[JsonFmtListWrapper[TestSuite]](response.json).get
			testSuites.count must equalTo(11)
			testSuites.items.length must equalTo(10)
		}
	}

	def insert10TestSuites() {
		DB.db withSession {
			for(i <- 1 to 10)
				TestSuites.insert(TestSuite(None, i, "pussinboots", "bankapp", "testsuite", 5,1,2,1000, now))
		}	
	}

	//then

	def checkTestSuite(suiteId: Long) {
		val suite = TestSuites.findById(suiteId).firstOption.get
		suite.id must beEqualTo(Some(suiteId))
		suite.buildNumber must beEqualTo(1)
		suite.owner must beEqualTo("pussinboots")
		suite.project must beEqualTo("bankapp")
		suite.name must beEqualTo("integration.ApplicationSpec")
		suite.tests must beEqualTo(7)
		suite.failures must beEqualTo(0)
		suite.errors must beEqualTo(0)
		suite.duration must beEqualTo(1.975)
	}

	def checkTestCases(suiteId: Long) {
		val testCases = TestCases.findBySuite(suiteId).sortBy(_.id.asc).list()
		testCases.length must equalTo(7)
		testCases(0).name must equalTo("application changed setup will work should::disable db ssl")
		testCases(1).name must equalTo("application changed setup will work should::enable DB logging")
		testCases(2).name must equalTo("application setup should should::configured with custom keystore is enabled")
		testCases(3).name must equalTo("application setup should should::check reditect to products.html work")
		testCases(4).name must equalTo("application setup should should::configured to redirect all http request to https on heroku")
		testCases(5).name must equalTo("application setup should should::configured with custom truststore is enabled")
		testCases(6).name must equalTo("application setup should should::configured with DB logging deactivate")
	}

	def checkTestSuiteWithFailure(suiteId: Long) {
		val suite = TestSuites.findById(suiteId).firstOption.get
		suite.id must beEqualTo(Some(suiteId))
		suite.buildNumber must beEqualTo(1)
		suite.owner must beEqualTo("pussinboots")
		suite.project must beEqualTo("bankapp")
		suite.name must beEqualTo("integration.TestSuiteControllerSpec")
		suite.tests must beEqualTo(2)
		suite.failures must beEqualTo(1)
		suite.errors must beEqualTo(0)
		suite.duration must beEqualTo(10.470)
	}
	def checkTestCasesWithFailure(suiteId: Long) {
		val testCases = TestCases.findBySuite(suiteId).sortBy(_.id.asc).list()
		testCases.length must equalTo(2)
		testCases(0).name must equalTo("POST to /api/<owner>/<project> should::with a valid junit xml report return http status 200 and store it in the db")
		testCases(0).failureMessage must equalTo(Some("'application changed setup will work should::disable db ssl'  is not equal to  'application changed setup will work should::enable DB logging'"))
		testCases(0).typ must equalTo(Some("java.lang.Exception"))
		testCases(1).name must equalTo("GET to /api/<owner>/<project> should::return latest 10 test suites")
		testCases(1).failureMessage must equalTo(None)
	}

	def checkTestSuiteWithError(suiteId: Long) {
		val suite = TestSuites.findById(suiteId).firstOption.get
		suite.id must beEqualTo(Some(suiteId))
		suite.buildNumber must beEqualTo(1)
		suite.owner must beEqualTo("pussinboots")
		suite.project must beEqualTo("bankapp")
		suite.name must beEqualTo("integration.TestSuiteControllerSpec")
		suite.tests must beEqualTo(3)
		suite.failures must beEqualTo(0)
		suite.errors must beEqualTo(1)
		suite.duration must beEqualTo(14.179)
	}
	def checkTestCasesWithError(suiteId: Long) {
		val testCases = TestCases.findBySuite(suiteId).sortBy(_.id.asc).list()
		testCases.length must equalTo(3)
		testCases(0).name must equalTo("POST to /api/<owner>/<project> should::with junit xml report all tests passed return http status 200 and store it in the db")
		testCases(0).typ must equalTo(Some("java.lang.Exception"))
		testCases(0).errorMessage must equalTo(Some("test error"))
		testCases(1).name must equalTo("GET to /api/<owner>/<project> should::return latest 10 test suites")
		testCases(1).failureMessage must equalTo(None)
		testCases(2).name must equalTo("POST to /api/<owner>/<project> should::with junit xml report one failure return http status 200 and store it in the db")
		testCases(2).failureMessage must equalTo(None)
	}
}