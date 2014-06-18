package unit.org.stock.manager

import scala.slick.driver.H2Driver
import scala.slick.session.Database
import Database.threadLocalSession
import play.api.test.PlaySpecification
import model.DateUtil
import scala.Some
import model.{DB, DAL, TestSuite, TestCase}
import unit.org.stock.manager.test.DatabaseSetupBefore

class ModelSpec extends PlaySpecification with DatabaseSetupBefore {
  sequential

  import DB.dal._
  import DB.dal.profile.simple._

  "TestCase model " should {
    "save" in {
      DB.db withSession {
        val now = DateUtil.nowDateTime()
        val savedTestCase = TestCases.insert(TestCase(None, 1, "pussinboots", "bankapp", "testcase", "testclass",1000))
        savedTestCase.id must beEqualTo(Some(2))
        savedTestCase.testSuiteId must beEqualTo(1)
        savedTestCase.owner must beEqualTo("pussinboots")
        savedTestCase.project must beEqualTo("bankapp")
        savedTestCase.name must beEqualTo("testcase")
        savedTestCase.className must beEqualTo("testclass")
        savedTestCase.duration must beEqualTo(1000)        
      }
    }
  }

  "TestSuite model" should {
    "save" in {
      DB.db withSession {
        val now = DateUtil.nowDateTime()
        val savedTestSuite = TestSuites.insert(TestSuite(None, 1, "pussinboots", "bankapp", "testsuite", 5,1,2,1000, now))
        savedTestSuite.id must beEqualTo(Some(2))
        savedTestSuite.buildNumber must beEqualTo(1)
        savedTestSuite.owner must beEqualTo("pussinboots")
        savedTestSuite.project must beEqualTo("bankapp")
        savedTestSuite.name must beEqualTo("testsuite")
        savedTestSuite.tests must beEqualTo(5)
        savedTestSuite.failures must beEqualTo(1)
        savedTestSuite.errors must beEqualTo(2)
        savedTestSuite.duration must beEqualTo(1000)
        savedTestSuite.date must beEqualTo(now)
      }
    }
  } 
}