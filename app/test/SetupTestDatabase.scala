package test

import model.{DateUtil, DB, TestSuite, TestCase}
import scala.slick.session.Database
import Database.threadLocalSession

object SetupTestDatabase {

  var now = DateUtil.nowDateTime()
  var yesterday = Option(DateUtil.daysBeforDateTime(1))
  import DB.dal._
  import DB.dal.profile.simple._
  def insertTestData(googleId: String = "test googleId") = {
    DB.dal.recreate
    TestCases.insert(TestCase(None, 1, "pussinboots", "bankapp", "testcase", "testclass",1000))
    TestSuites.insert(TestSuite(None, 1, "pussinboots", "bankapp", "testsuite", 5,1,2,1000, now))
    (now, yesterday)
  }
  //insert some test data they are enrcypted for e2e test of encryption and decryption see karma test
  def insertE2ETestData(googleId: String = "test googleId") = {
    insert10TestSuites()
    (now, yesterday)
  }

  def insert10TestSuites() {
    DB.db withSession {
      for(i <- 2 to 11)
        TestSuites.insert(TestSuite(None, i, "pussinboots", "bankapp", s"testsuite $i", 8,0,0,1000, now))
    } 
  }
}