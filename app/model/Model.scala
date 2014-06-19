package model

import scala.slick.driver.ExtendedProfile
import java.sql.{Timestamp, Date}
import java.util.{Date, Calendar}
import scala.slick.session.{Database, Session}
import scala.slick.jdbc.meta.MTable

trait Profile {
  val profile: ExtendedProfile
}

/**
 * The Data Access Layer contains all components and a profile
 */
class DAL(override val profile: ExtendedProfile) extends TestSuiteComponent with TestCaseComponent with BuildComponent with Profile {

  import profile.simple._

  def recreate(implicit session: Session) {
    drop(session)
    create(session)
  }

  def create(implicit session: Session) {
    if (MTable.getTables(TestSuites.tableName).list().isEmpty) {
      (TestSuites.ddl).create
    }
    if (MTable.getTables(TestCases.tableName).list().isEmpty) {
      (TestCases.ddl).create
    }
    if (MTable.getTables(Builds.tableName).list().isEmpty) {
      (Builds.ddl).create
    }
  }

  def drop(implicit session: Session) = try {
    (TestSuites.ddl ++ TestCases.ddl ++ Builds.ddl).drop
  } catch {
    case ioe: Exception =>
  }
}

case class TestSuite(var id: Option[Long] = None, buildNumber:Int, owner: String, project: String,
                     name: String, tests: Option[Int], failures: Option[Int], errors: Option[Int], duration: Option[Double], 
                     date: Timestamp = DateUtil.nowDateTime())

trait TestSuiteComponent {
  this: Profile =>
  //requires a Profile to be mixed in...

  import profile.simple._

  //...to be able import profile.simple._

  import profile.simple.Database.threadLocalSession

  object TestSuites extends Table[TestSuite]("testSuite") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def buildNumber = column[Int]("buildNumber")
    def owner = column[String]("owner")
    def project = column[String]("project")
    def name = column[String]("name")
    def tests = column[Option[Int]]("tests")
    def failures = column[Option[Int]]("failures")
    def errors = column[Option[Int]]("errors")
    def duration = column[Option[Double]]("duration")
    def date = column[Timestamp]("date")
    def * = id.? ~ buildNumber ~ owner ~ project ~ name ~ tests ~ failures ~ errors ~ duration ~ date <>(TestSuite, TestSuite.unapply _)

    def forInsert = buildNumber ~ owner ~ project ~ name ~ tests ~ failures ~ errors ~ duration ~ date<>( 
      { t => TestSuite(None, t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9)}, 
      { (u: TestSuite) => Some((u.buildNumber, u.owner, u.project, u.name, u.tests, u.failures, u.errors, u.duration, u.date))}) returning id
    def insert(testSuite: TestSuite): TestSuite = testSuite.copy(id = Some(forInsert.insert(testSuite)))
    def findBy(owner: String, project: String, buildNumber: Int) = (for {a <- TestSuites if a.owner === owner && a.project === project && a.buildNumber === buildNumber} yield (a))
    def findResultsBy(owner: String, project: String, buildNumber: Int) = (for {a <- TestSuites if a.owner === owner && a.project === project && a.buildNumber === buildNumber} yield (a.tests, a.failures, a.errors))
    
    def findById(id: Long) = (for {a <- TestSuites if a.id === id} yield (a))
  }
}
case class TestCase(var id: Option[Long] = None, testSuiteId: Long, owner: String, project: String,
                     name: String, className: String, duration: Option[Double], 
                     failureMessage: Option[String] = None, errorMessage: Option[String] = None,
                     typ: Option[String] = None)

trait TestCaseComponent {
  this: Profile =>
  //requires a Profile to be mixed in...

  import profile.simple._

  //...to be able import profile.simple._

  import profile.simple.Database.threadLocalSession

  object TestCases extends Table[TestCase]("testCase") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def testSuiteId = column[Long]("testSuiteId")
    def owner = column[String]("owner")
    def project = column[String]("project")
    def name = column[String]("name")
    def className = column[String]("tests")
    def duration = column[Option[Double]]("duration")
    def failureMessage = column[Option[String]]("failureMessage")
    def errorMessage = column[Option[String]]("errorMessage")
    def typ = column[Option[String]]("typ")
    def * = id.? ~ testSuiteId ~ owner ~ project ~ name ~ className ~ duration ~ failureMessage ~ errorMessage ~ typ <>(TestCase, TestCase.unapply _)

    def forInsert = testSuiteId ~ owner ~ project ~ name ~ className ~ duration ~ failureMessage ~ errorMessage ~ typ <>( 
      { t => TestCase(None, t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9)}, 
      { (u: TestCase) => Some((u.testSuiteId, u.owner, u.project, u.name, u.className, u.duration, u.failureMessage, u.errorMessage, u.typ))}) returning id
    def insert(testCase: TestCase): TestCase = testCase.copy(id = Some(forInsert.insert(testCase)))
    def findBySuite(testSuiteId: Long) = (for {a <- TestCases if a.testSuiteId === testSuiteId} yield (a))
  }
}

case class Build(var id: Option[Long] = None, owner: String, project: String,
                     buildNumber: Int, date: Timestamp = DateUtil.nowDateTime(), 
                     tests: Option[Int]=None, failures: Option[Int]=None, errors: Option[Int]=None)

trait BuildComponent {
  this: Profile =>
  //requires a Profile to be mixed in...

  import profile.simple._

  //...to be able import profile.simple._

  import profile.simple.Database.threadLocalSession

  object Builds extends Table[Build]("builds") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def owner = column[String]("owner")
    def project = column[String]("project")
    def buildNumber = column[Int]("buildNumber")
    def date = column[Timestamp]("date")
    def tests = column[Option[Int]]("tests")
    def failures = column[Option[Int]]("failures")
    def errors = column[Option[Int]]("errors")
    def idx = index("idx_owner_project", (owner, project), unique = false)
    def * = id.? ~ owner ~ project ~ buildNumber ~ date ~ tests ~ failures ~ errors<>(Build, Build.unapply _)

    def forInsert = owner ~ project ~ buildNumber ~ date ~ tests ~ failures ~ errors<>( 
      { t => Build(None, t._1, t._2, t._3, t._4, t._5, t._6, t._7)}, 
      { (u: Build) => Some((u.owner, u.project, u.buildNumber, u.date, u.tests, u.failures, u.errors))}) returning id
    def insert(build: Build): Build = build.copy(id = Some(forInsert.insert(build)))
    def insertAndIncrement(owner: String, project: String): Build = {
      val q =findLatestBuildNumber(owner, project)
      val latestBuildNumber:Int = q.first.getOrElse(0)
      insert(Build(owner=owner, project=project, buildNumber=latestBuildNumber+1))      
    }
    def updateStats(owner: String, project: String, buildNumber: Int, testSum: Int, failureSum: Int, errorSum: Int) {
      //val build=findByOwnerAndProject(owner, project).firstOption.get
      //val q2 = Query(Builds).where(_.buildNumber === buildNumber).map(r=>r.tests)
      val q2 = for {a <- Builds if a.buildNumber === buildNumber} yield (a.tests)
      q2.update(Some(testSum))
    }
    def findByOwnerAndProject(owner: String, project: String) = (for {a <- Builds if a.owner === owner && a.project === project} yield (a))
    def findByBuildNumber(buildNumber: Int) = (for {a <- Builds if a.buildNumber === buildNumber} yield (a))
    def findLatestBuildNumber(owner: String, project: String) = Query((for {a <- Builds if a.owner === owner && a.project === project} yield (a.buildNumber)).max)
  }
}