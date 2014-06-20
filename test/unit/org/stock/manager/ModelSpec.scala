package unit.org.stock.manager

import scala.slick.driver.H2Driver
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession
import play.api.test.PlaySpecification
import model.DateUtil
import scala.Some
import model.{DB, DAL, TestSuite, TestCase}
import unit.org.stock.manager.test.DatabaseSetupBefore

class ModelSpec extends PlaySpecification with DatabaseSetupBefore {
  sequential
  implicit def toOption[A](value: A) : Option[A] = Some(value)

  import DB.dal
  import DB.dal.profile.simple._

  "TestCase model " should {
    "save" in {
      DB.db withDynSession {
        val now = DateUtil.nowDateTime()
        val savedTestCase = dal.testCaseForInsert.insert(TestCase(None, 1, "pussinboots", "bankapp", "testcase", "testclass",1000.0))
        savedTestCase.id must beEqualTo(Some(2))
        savedTestCase.testSuiteId must beEqualTo(1)
        savedTestCase.owner must beEqualTo("pussinboots")
        savedTestCase.project must beEqualTo("bankapp")
        savedTestCase.name must beEqualTo("testcase")
        savedTestCase.className must beEqualTo("testclass")
        savedTestCase.duration must beEqualTo(Some(1000.0))        
      }
    }
  }

  "TestSuite model" should {
    "save" in {
      DB.db withDynSession {
        val now = DateUtil.nowDateTime()
        val savedTestSuite = dal.testSuiteForInsert.insert(TestSuite(None, 1, "pussinboots", "bankapp", "testsuite", 5,1,2,1000.0, now))
        savedTestSuite.id must beEqualTo(Some(2))
        savedTestSuite.buildNumber must beEqualTo(1)
        savedTestSuite.owner must beEqualTo("pussinboots")
        savedTestSuite.project must beEqualTo("bankapp")
        savedTestSuite.name must beEqualTo("testsuite")
        savedTestSuite.tests must beEqualTo(Some(5))
        savedTestSuite.failures must beEqualTo(Some(1))
        savedTestSuite.errors must beEqualTo(Some(2))
        savedTestSuite.duration must beEqualTo(Some(1000.0))
        savedTestSuite.date must beEqualTo(now)
      }
    }
  } 
}