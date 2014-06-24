package integration

import org.specs2.mutable.Specification
import play.api.test._
import play.api.test.Helpers._
import play.api.libs.ws._
import play.api.Play
import play.api.Play.current
import play.api.libs.json.Json
import unit.org.stock.manager.test.DatabaseSetupBefore

import model.{DB, Build, TestSuite, TestCase}
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession

class TestSuiteControllerSpec extends PlaySpecification with DatabaseSetupBefore {
	sequential
	implicit def toOption[A](value: A) : Option[A] = Some(value)
	implicit val timeout = 10000
	import DB.dal
	import dal._
	import DB.dal.profile.simple._
	import model.JsonHelper._

	/*"POST to /api/<owner>/<project>" should {
		"with sbt junit xml report all tests passed return http status 200 and store it in the db" in new WithServer { 
			val xmlString = scala.io.Source.fromFile(Play.getFile("test/resources/sbt/ApplicationSpec.xml")).mkString
			val response = await(WS.url(s"http://localhost:$port/api/pussinboots/bankapp/1").withHeaders("Content-Type" -> "text/xml").post(xmlString))
			response.status must equalTo(OK)
			val suiteId = (response.json \ "testsuites"  \\ "id").map(_.as[Int]).head
			DB.db withDynSession {
				checkTestSuite(suiteId)
				checkTestCases(suiteId)
			}
		}

		"with sbt junit xml report all tests passed return http status 200 and store it in the db" in new WithServer  { 
			val xmlString = scala.io.Source.fromFile(Play.getFile("test/resources/sbt/ApplicationSpec.xml")).mkString
			val response = await(WS.url(s"http://localhost:$port/api/pussinboots/bankapp/1").withHeaders("Content-Type" -> "text/xml").post(xmlString))
			response.status must equalTo(OK)
			val suiteId = (response.json \ "testsuites"  \\ "id").map(_.as[Int]).head
			DB.db withDynSession {
				checkTestSuite(suiteId)
				checkTestCases(suiteId)
			}
		}

		"with karma junit xml report from all tests passed return http status 200 and store it in the db" in new WithServer { 
			val xmlString = scala.io.Source.fromFile(Play.getFile("test/resources/karma/test-results.xml")).mkString
			val response = await(WS.url(s"http://localhost:$port/api/pussinboots/bankapp/1").withHeaders("Content-Type" -> "text/xml").post(xmlString))
			response.status must equalTo(OK)
			val suiteId = (response.json \ "testsuites"  \\ "id").map(_.as[Int])
			DB.db withDynSession {
				checkTestSuiteKarmaOpera(suiteId(0))
				checkTestCasesKarma(suiteId(0))
				checkTestSuiteKarmaFireFox(suiteId(1))
				checkTestCasesKarma(suiteId(1))
			}
		}

		// test results contains failures and errors


		"with sbt junit xml report one failure return http status 200 and store it in the db" in new WithServer { 
			val xmlString = scala.io.Source.fromFile(Play.getFile("test/resources/sbt/TestSuiteControllerSpecFailure.xml")).mkString
			val response = await(WS.url(s"http://localhost:$port/api/pussinboots/bankapp/1").withHeaders("Content-Type" -> "text/xml").post(xmlString))
			response.status must equalTo(OK)
			val suiteId = (response.json \ "testsuites"  \\ "id").map(_.as[Int]).head
			DB.db withDynSession {
				checkTestSuiteWithFailure(suiteId)
				checkTestCasesWithFailure(suiteId)
			}
		}

		"with sbt junit xml report one error return http status 200 and store it in the db" in new WithServer { 
			val xmlString = scala.io.Source.fromFile(Play.getFile("test/resources/sbt/TestSuiteControllerSpecError.xml")).mkString
			val response = await(WS.url(s"http://localhost:$port/api/pussinboots/bankapp/1").withHeaders("Content-Type" -> "text/xml").post(xmlString))
			response.status must equalTo(OK)
			val suiteId = (response.json \ "testsuites"  \\ "id").map(_.as[Int]).head
			DB.db withDynSession {
				checkTestSuiteWithError(suiteId)
				checkTestCasesWithError(suiteId)
			}
		}

		"with karma junit xml report one failure return http status 200 and store it in the db" in new WithServer { 
			val xmlString = scala.io.Source.fromFile(Play.getFile("test/resources/karma/test-results-failures.xml")).mkString
			val response = await(WS.url(s"http://localhost:$port/api/pussinboots/bankapp/1").withHeaders("Content-Type" -> "text/xml").post(xmlString))
			response.status must equalTo(OK)
			println(response.json)
			val suiteIds = (response.json \ "testsuites"  \\ "id").map(_.as[Long])
			DB.db withDynSession {
				checkKarmaTestSuiteWithFailure(suiteIds)
				checkKarmaTestCasesWithFailure(suiteIds)
			}
		}
	}

	"GET to /api/<owner>/<project>" should {
		"return all test suites" in new WithServer {
			insert10TestSuites ()
			val response = await(WS.url(s"http://localhost:$port/api/pussinboots/bankapp/1").get)
			response.status must equalTo(OK)
			val testSuites = Json.fromJson[JsonFmtListWrapper[TestSuite]](response.json).get
			testSuites.count must equalTo(11)
			testSuites.items.length must equalTo(11)
		}
	}*/
    
    import org.specs2.matcher.XmlMatchers._
    "GET to /api/<owner>/<project>/testsuites/badge" should {
		"return all test suites" in new WithServer {
			DB.db withDynSession {
                Builds.builds.insert(Build(owner="pussinboots", project="bankapp", buildNumber=2, date=now, tests=Some(5),failures = Some(1), errors = Some(2), travisBuildId=Some("2")))	
                for(i <- 1 to 2)
                    dal.testSuites.insert(TestSuite(None, 2, "pussinboots", "bankapp", "testsuite", 5,1,2,1000.0, now))
            }
			val response = await(WS.url(s"http://localhost:$port/api/pussinboots/bankapp/testsuites/badge").get)
			response.status must equalTo(OK)
            println(response.xml)
            val printer = new scala.xml.PrettyPrinter(140, 2)
            printer.format(response.xml) must equalTo(printer.format(<svg height="200" width="900" xmlns="http://www.w3.org/2000/svg">                                                                                                  
                <defs>                                                                                                                                             
                 <linearGradient y2="1" x2="0" y1="0" x1="0" id="lgr1">                                                                                            
                      <stop stop-opacity=".7" stop-color="#fff" offset="0"/>                                                                                       
                          <stop stop-opacity=".1" stop-color="#aaa" offset=".1"/>                                                                                  
                          <stop stop-opacity=".3" offset=".9"/>                                                                                                    
                          <stop stop-opacity=".5" offset="1"/>                                                                                                     
                    </linearGradient>                                                                                                                              
                </defs>                                                                                                                                            
                                                                                                                                                                   
                <rect fill="#555" height="18" width="340" x="0" y="0" rx="4"/>
               	<rect fill="red" height="18" width="60" x="340" y="0" rx="4"/>
                <rect fill="url(#lgr1)" height="18" width="400" y="0" rx="4"/>
                <rect fill="#555" height="18" width="340" x="0" y="17" rx="4"/>                                                                                                                                                                                                                                                                      
                <rect fill="red" height="18" width="60" x="340" y="17" rx="4"/>
                <rect fill="url(#lgr1)" height="18" width="400" y="17" rx="4"/>
         		                                                                                                                                                   
                <text font-size="11" font-family="DejaVu Sans,Verdana,Geneva,sans-serif" text-anchor="middle" fill="#fff" y="12" x="19.5">testsuite</text>	
                <text font-size="11" font-family="DejaVu Sans,Verdana,Geneva,sans-serif" text-anchor="middle" fill="#fff" y="12" x="240.5">error 2</text>
                <text font-size="11" font-family="DejaVu Sans,Verdana,Geneva,sans-serif" text-anchor="middle" fill="#fff" y="29" x="19.5">testsuite</text>
                <text font-size="11" font-family="DejaVu Sans,Verdana,Geneva,sans-serif" text-anchor="middle" fill="#fff" y="29" x="240.5">error 2</text>
                
				</svg>))
		}
	}

	def insert10TestSuites() {
		DB.db withDynSession {
			for(i <- 1 to 10)
				dal.testSuites.insert(TestSuite(None, 1, "pussinboots", "bankapp", "testsuite", 5,1,2,1000.0, now))
			Builds.builds.insert(Build(owner="pussinboots", project="bankapp", buildNumber=2, date=now, tests=Some(5),failures = Some(1), errors = Some(2), travisBuildId=Some("2")))	
			for(i <- 1 to 10)
				dal.testSuites.insert(TestSuite(None, 2, "pussinboots", "bankapp", "testsuite", 5,1,2,1000.0, now))
		}	
	}

	//then

	def checkTestSuiteKarmaFireFox(suiteId: Long) {
		val suite = dal.findById(suiteId).firstOption.get
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
		val suite = dal.findById(suiteId).firstOption.get
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
		val testCases = dal.findBySuite(suiteId).sortBy(_.id.asc).list()
		testCases.length must equalTo(3)
		testCases(0).name must equalTo("ten test suites are display")
		testCases(1).name must equalTo("the latest ten test suites should be display started with the eleven test suite")
		testCases(2).name must equalTo("should redirect products.html to products.html#/builds")
	}

	def checkTestSuite(suiteId: Long) {
		val suite = dal.findById(suiteId).firstOption.get
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
		val testCases = dal.findBySuite(suiteId).sortBy(_.id.asc).list()
		testCases.length must equalTo(7)
		testCases(0).name must equalTo("application changed setup will work should::disable db ssl")
		testCases(1).name must equalTo("application changed setup will work should::enable DB logging")
		testCases(2).name must equalTo("application setup should should::configured with custom keystore is enabled")
		testCases(3).name must equalTo("application setup should should::check reditect to products.html work")
		testCases(4).name must equalTo("application setup should should::configured to redirect all http request to https on heroku")
		testCases(5).name must equalTo("application setup should should::configured with custom truststore is enabled")
		testCases(6).name must equalTo("application setup should should::configured with DB logging deactivate")
	}

	def checkKarmaTestSuiteWithFailure(suiteIds: Seq[Long]) {
		//the first test suite coul dbe ignored 
		var suite = dal.findById(suiteIds(0)).firstOption.get
		suite.id must beEqualTo(Some(suiteIds(0)))
		suite.buildNumber must beEqualTo(1)
		suite.owner must beEqualTo("pussinboots")
		suite.project must beEqualTo("bankapp")
		suite.name must beEqualTo("Firefox 30.0.0 (Ubuntu)")
		suite.tests must beEqualTo(None)
		suite.failures must beEqualTo(None)
		suite.errors must beEqualTo(None)
		suite.duration must beEqualTo(None)

		suite = dal.findById(suiteIds(1)).firstOption.get
		suite.id must beEqualTo(Some(suiteIds(1)))
		suite.buildNumber must beEqualTo(1)
		suite.owner must beEqualTo("pussinboots")
		suite.project must beEqualTo("bankapp")
		suite.name must beEqualTo("Firefox 30.0.0 (Ubuntu)")
		suite.tests must beEqualTo(Some(8))
		suite.failures must beEqualTo(Some(6))
		suite.errors must beEqualTo(Some(0))
		println(suite)
		suite.duration must beEqualTo(Some(9.67))
	}
	def checkKarmaTestCasesWithFailure(suiteIds: Seq[Long]) {
		var testCases = dal.findBySuite(suiteIds(0)).sortBy(_.id.asc).list()
		testCases.length must equalTo(0)
		testCases = dal.findBySuite(suiteIds(1)).sortBy(_.id.asc).list()
		testCases.length must equalTo(4)
		testCases(0).name must equalTo("ten builds are display")
		testCases(1).name must equalTo("the latest build with buildNumber 11 show as first")
		testCases(2).name must equalTo("the latest testsuite show as first")
		testCases(3).name must equalTo("should redirect products.html to products.html#/builds")
		testCases(0).failureMessage must equalTo(Some("expect repeater 'li.build' count toBe 10\n/home/vagrant/workspace/frank/unitcover/node_modules/karma-ng-scenario/lib/angular-scenario.js:27592:5: expected 10 but was 0\n"))
		testCases(0).typ must equalTo(None)
		testCases(1).failureMessage must equalTo(Some("repeater 'li.build:eq(0)' column 'build.buildNumber'\n/home/vagrant/workspace/frank/unitcover/node_modules/karma-ng-scenario/lib/angular-scenario.js:27592:5: Selector li.build:eq(0) did not match any elements.\n"))
		testCases(1).typ must equalTo(None)
		testCases(2).errorMessage must equalTo(Some("repeater 'li.build:eq(0)' column 'build.name'\n/home/vagrant/workspace/frank/unitcover/node_modules/karma-ng-scenario/lib/angular-scenario.js:27592:5: Selector li.build:eq(0) did not match any elements.\n"))
		testCases(2).typ must equalTo(None)
		testCases(3).failureMessage must equalTo(None)
		testCases(3).typ must equalTo(None)		
	}

	def checkTestSuiteWithFailure(suiteId: Long) {
		val suite = dal.findById(suiteId).firstOption.get
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
		val testCases = dal.findBySuite(suiteId).sortBy(_.id.asc).list()
		testCases.length must equalTo(2)
		testCases(0).name must equalTo("POST to /api/<owner>/<project> should::with a valid junit xml report return http status 200 and store it in the db")
		testCases(0).failureMessage must equalTo(Some("'application changed setup will work should::disable db ssl'  is not equal to  'application changed setup will work should::enable DB logging'"))
		testCases(0).typ must equalTo(Some("java.lang.Exception"))
		testCases(1).name must equalTo("GET to /api/<owner>/<project> should::return latest 10 test suites")
		testCases(1).failureMessage must equalTo(None)
	}

	def checkTestSuiteWithError(suiteId: Long) {
		val suite = dal.findById(suiteId).firstOption.get
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
		val testCases = dal.findBySuite(suiteId).sortBy(_.id.asc).list()
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
