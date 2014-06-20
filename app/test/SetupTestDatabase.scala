package test

import model.{DateUtil, DB, TestSuite, TestCase, Build}
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession

object SetupTestDatabase {
  implicit def toOption[A](value: A) : Option[A] = Some(value)

  var now = DateUtil.nowDateTime()
  var yesterday = Option(DateUtil.daysBeforDateTime(1))
  import DB.dal
  import DB.dal.profile.simple._
  def insertTestData(googleId: String = "test googleId") = {
    DB.dal.recreate
    dal.testCases.insert(TestCase(None, 1, "pussinboots", "bankapp", "testcase", "testclass",1000.0))
    dal.testSuites.insert(TestSuite(None, 1, "pussinboots", "bankapp", "testsuite", 5,1,2,1000.0, now))
    (now, yesterday)
  }
  //insert some test data they are enrcypted for e2e test of encryption and decryption see karma test
  def insertE2ETestData(googleId: String = "test googleId") = {
    insert10TestSuites()
    (now, yesterday)
  }

  def insert10TestSuites() {
    for(i <- 2 to 11)
      dal.testSuites.insert(TestSuite(None, i, "pussinboots", "bankapp", s"testsuite $i", 8,0,0,1000.0, now))
  
    for(i <- 1 to 11)
      dal.builds.insert(Build(buildNumber=i, owner="pussinboots", project="bankapp", tests=i, failures=i/8, errors=i/10))
  }
}