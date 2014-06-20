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
	implicit def toOption[A](value: A) : Option[A] = Some(value)
	implicit val timeout = 10000
	import DB.dal._
	import DB.dal.profile.simple._
	import model.JsonHelper._

	"POST to /api/<owner>/<project>" should {
		"with sbt junit xml report all tests passed return http status 200 and store it in the db" in new WithServer { 
			val xmlString = scala.io.Source.fromFile(Play.getFile("test/resources/ApplicationSpec.xml")).mkString
			val response = await(WS.url(s"http://localhost:$port/api/pussinboots/bankapp/1").withHeaders("Content-Type" -> "text/xml").post(xmlString))
			response.status must equalTo(OK)
			val suiteId = (response.json \ "testsuites"  \\ "id").map(_.as[Int]).head
			DB.db withSession {
				checkTestSuite(suiteId)
				checkTestCases(suiteId)
			}
		}

		"with sbt junit xml report all tests passed return http status 200 and store it in the db" in new WithServer  { 
			val xmlString = scala.io.Source.fromFile(Play.getFile("test/resources/ApplicationSpec.xml")).mkString
			val response = await(WS.url(s"http://localhost:$port/api/pussinboots/bankapp/1").withHeaders("Content-Type" -> "text/xml").post(xmlString))
			response.status must equalTo(OK)
			val suiteId = (response.json \ "testsuites"  \\ "id").map(_.as[Int]).head
			DB.db withSession {
				checkTestSuite(suiteId)
				checkTestCases(suiteId)
			}
		}

		"with karma junit xml report from all tests passed return http status 200 and store it in the db" in new WithServer { 
			val xmlString = scala.io.Source.fromFile(Play.getFile("test/resources/test-results.xml")).mkString
			val response = await(WS.url(s"http://localhost:$port/api/pussinboots/bankapp/1").withHeaders("Content-Type" -> "text/xml").post(xmlString))
			response.status must equalTo(OK)
			val suiteId = (response.json \ "testsuites"  \\ "id").map(_.as[Int])
			DB.db withSession {
				checkTestSuiteKarmaOpera(suiteId(0))
				checkTestCasesKarma(suiteId(0))
				checkTestSuiteKarmaFireFox(suiteId(1))
				checkTestCasesKarma(suiteId(1))
			}
		}


		"with sbt junit xml report one failure return http status 200 and store it in the db" in new WithServer { 
			val xmlString = scala.io.Source.fromFile(Play.getFile("test/resources/TestSuiteControllerSpecFailure.xml")).mkString
			val response = await(WS.url(s"http://localhost:$port/api/pussinboots/bankapp/1").withHeaders("Content-Type" -> "text/xml").post(xmlString))
			response.status must equalTo(OK)
			val suiteId = (response.json \ "testsuites"  \\ "id").map(_.as[Int]).head
			DB.db withSession {
				checkTestSuiteWithFailure(suiteId)
				checkTestCasesWithFailure(suiteId)
			}
		}

		"with sbt junit xml report one error return http status 200 and store it in the db" in new WithServer { 
			val xmlString = scala.io.Source.fromFile(Play.getFile("test/resources/TestSuiteControllerSpecError.xml")).mkString
			val response = await(WS.url(s"http://localhost:$port/api/pussinboots/bankapp/1").withHeaders("Content-Type" -> "text/xml").post(xmlString))
			response.status must equalTo(OK)
			val suiteId = (response.json \ "testsuites"  \\ "id").map(_.as[Int]).head
			DB.db withSession {
				checkTestSuiteWithError(suiteId)
				checkTestCasesWithError(suiteId)
			}
		}
	}

	"GET to /api/<owner>/<project>" should {
		"return latest 10 test suites" in new WithServer {
			insert10TestSuites ()
			val response = await(WS.url(s"http://localhost:$port/api/pussinboots/bankapp/1").get)
			response.status must equalTo(OK)
			val testSuites = Json.fromJson[JsonFmtListWrapper[TestSuite]](response.json).get
			testSuites.count must equalTo(11)
			testSuites.items.length must equalTo(10)
		}
	}

	def insert10TestSuites() {
		DB.db withSession {
			for(i <- 1 to 10)
				TestSuites.insert(TestSuite(None, 1, "pussinboots", "bankapp", "testsuite", 5,1,2,1000.0, now))
			for(i <- 1 to 10)
				TestSuites.insert(TestSuite(None, 2, "pussinboots", "bankapp", "testsuite", 5,1,2,1000.0, now))
			for(i <- 1 to 10)
				TestSuites.insert(TestSuite(None, 3, "pussinboots", "bankapp", "testsuite", 5,1,2,1000.0, now))
		}	
	}

	//then

	def checkTestSuiteKarmaFireFox(suiteId: Long) {
		val suite = TestSuites.findById(suiteId).firstOption.get
		suite.id must beEqualTo(Some(suiteId))
		suite.buildNumber must beEqualTo(1)
		suite.owner must beEqualTo("pussinboots")
		suite.project must beEqualTo("bankapp")
		suite.name must beEqualTo("Firefox 30.0.0 (Ubuntu)")
		suite.tests must beEqualTo(Some(3))
		suite.failures must beEqualTo(Some(0))
		suite.errors must beEqualTo(Some(0))
		suite.duration must beEqualTo(Some(10.684))
	}

	def checkTestSuiteKarmaOpera(suiteId: Long) {
		val suite = TestSuites.findById(suiteId).firstOption.get
		suite.id must beEqualTo(Some(suiteId))
		suite.buildNumber must beEqualTo(1)
		suite.owner must beEqualTo("pussinboots")
		suite.project must beEqualTo("bankapp")
		suite.name must beEqualTo("Opera 12.16.0 (Linux)")
		suite.tests must beEqualTo(Some(3))
		suite.failures must beEqualTo(Some(0))
		suite.errors must beEqualTo(Some(0))
		suite.duration must beEqualTo(Some(10.997))
	}

	def checkTestCasesKarma(suiteId: Long) {
		val testCases = TestCases.findBySuite(suiteId).sortBy(_.id.asc).list()
		testCases.length must equalTo(3)
		testCases(0).name must equalTo("ten test suites are display")
		testCases(1).name must equalTo("the latest ten test suites should be display started with the eleven test suite")
		testCases(2).name must equalTo("should redirect products.html to products.html#/builds")
	}

	def checkTestSuite(suiteId: Long) {
		val suite = TestSuites.findById(suiteId).firstOption.get
		suite.id must beEqualTo(Some(suiteId))
		suite.buildNumber must beEqualTo(1)
		suite.owner must beEqualTo("pussinboots")
		suite.project must beEqualTo("bankapp")
		suite.name must beEqualTo("integration.ApplicationSpec")
		suite.tests must beEqualTo(Some(7))
		suite.failures must beEqualTo(Some(0))
		suite.errors must beEqualTo(Some(0))
		suite.duration must beEqualTo(Some(1.975))
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
		suite.tests must beEqualTo(Some(2))
		suite.failures must beEqualTo(Some(1))
		suite.errors must beEqualTo(Some(0))
		suite.duration must beEqualTo(Some(10.470))
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
		suite.tests must beEqualTo(Some(3))
		suite.failures must beEqualTo(Some(0))
		suite.errors must beEqualTo(Some(1))
		suite.duration must beEqualTo(Some(14.179))
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