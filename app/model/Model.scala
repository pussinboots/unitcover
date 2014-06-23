package model

import scala.slick.driver.{JdbcDriver, H2Driver, MySQLDriver}
import java.sql.{Timestamp, Date}
import java.util.{Date, Calendar}
import scala.slick.jdbc.meta.MTable
import scala.slick.model.ForeignKeyAction

trait Profile {
  val profile: JdbcDriver
}

/**
 * The Data Access Layer contains all components and a profile
 */
class DAL(override val profile: JdbcDriver) extends TestSuiteComponent with TestCaseComponent with BuildComponent with Profile {

  import profile.simple._

  def recreate(implicit session: Session) {
    drop(session)
    create(session)
  }

  def create(implicit session: Session) {
    (testSuites.ddl ++ testCases.ddl ++ Builds.builds.ddl).create //helper method to create all tables
/*    if (MTable.getTables(testSuites.tableName).list().isEmpty) {
      (testSuites.ddl).create
    }
    if (MTable.getTables(testCases.tableName).list().isEmpty) {
      (testCases.ddl).create
    }
    if (MTable.getTables(builds.tableName).list().isEmpty) {
      (builds.ddl).create
    }*/
  }

  def drop(implicit session: Session) = try {
    (testSuites.ddl ++ testCases.ddl ++ Builds.builds.ddl).drop
  } catch {
    case e: Exception => e.printStackTrace(System.out)
  }
}

case class TestSuite(var id: Option[Long] = None, buildNumber:Int, owner: String, project: String,
                     name: String, tests: Option[Int], failures: Option[Int], errors: Option[Int], duration: Option[Double], 
                     date: Timestamp = DateUtil.nowDateTime())

trait TestSuiteComponent {
  this: BuildComponent with Profile =>
  //requires a Profile to be mixed in...

  import profile.simple._

  //...to be able import profile.simple._
  class TestSuites(tag: Tag) extends Table[TestSuite](tag, "testSuite") {
    //todo database optimization removed autoinc key and have a other unique identifier also multiple fields supported 
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
    def buildFK = foreignKey("build_fk", (owner, project, buildNumber), Builds.builds)(o=>(o.owner, o.project, o.buildNumber), onDelete=ForeignKeyAction.Cascade)
    def * = (id.?, buildNumber, owner, project, name, tests, failures, errors, duration, date) <>(TestSuite.tupled, TestSuite.unapply)
  }
  val testSuites = TableQuery[TestSuites]
  val testSuiteForInsert = testSuites returning testSuites.map(_.id) into { case (testSuite, id) => testSuite.copy(id = Some(id)) }
  def findBy(owner: String, project: String, buildNumber: Int) = (for {a <- testSuites if a.owner === owner && a.project === project && a.buildNumber === buildNumber} yield (a))
  def findResultsBy(owner: String, project: String, buildNumber: Int) = (for {a <- testSuites if a.owner === owner && a.project === project && a.buildNumber === buildNumber} yield (a.tests, a.failures, a.errors))
  def findById(id: Long) = (for {a <- testSuites if a.id === id} yield (a))
}
case class TestCase(var id: Option[Long] = None, testSuiteId: Long, owner: String, project: String,
                     name: String, className: String, duration: Option[Double], 
                     failureMessage: Option[String] = None, errorMessage: Option[String] = None,
                     typ: Option[String] = None)

trait TestCaseComponent {
  this: Profile with TestSuiteComponent =>
  //requires a Profile to be mixed in...

  import profile.simple._

  //...to be able import profile.simple._

  class TestCases(tag: Tag) extends Table[TestCase](tag, "testCase") {
        //todo database optimization removed autoinc key and have a other unique identifier also multiple fields supported
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
    def suiteFK = foreignKey("suite_fk", testSuiteId, testSuites)(_.id, onDelete=ForeignKeyAction.Cascade)
    def * = (id.?, testSuiteId, owner, project, name, className, duration, failureMessage, errorMessage, typ) <>(TestCase.tupled, TestCase.unapply)
  }
  val testCases = TableQuery[TestCases]
  val testCaseForInsert = testCases returning testCases.map(_.id) into { case (testCase, id) => testCase.copy(id = Some(id)) }
  def findBySuite(testSuiteId: Long) = (for {a <- testCases if a.testSuiteId === testSuiteId} yield (a))  
}

case class Build(owner: String, project: String,
                     buildNumber: Int, date: Timestamp = DateUtil.nowDateTime(), 
                     tests: Option[Int]=None, failures: Option[Int]=None, errors: Option[Int]=None,
                     trigger: Option[String] = None, branch:Option[String] = None, travisBuildId: Option[String] = None)

trait BuildComponent {
  this: Profile =>
  //requires a Profile to be mixed in...

  import profile.simple._
  import profile.simple.Database.dynamicSession
  //...to be able import profile.simple._
  class Builds(tag: Tag) extends Table[Build](tag, "builds") {
    def owner = column[String]("owner")
    def project = column[String]("project")
    def buildNumber = column[Int]("buildNumber")
    def date = column[Timestamp]("date")
    def tests = column[Option[Int]]("tests")
    def failures = column[Option[Int]]("failures")
    def errors = column[Option[Int]]("errors")
    def trigger = column[Option[String]]("trigger")
    def branch = column[Option[String]]("branch")
    def travisBuildId = column[Option[String]]("travisBuildId")
    def pk = primaryKey("pk_a", (owner, project, buildNumber))
    def idx1 = index("idx_owner_project", (owner, project), unique = false)
    def idx2 = index("idx_owner_project_buildNumber", (owner, project, buildNumber), unique = true)
    def * = (owner, project, buildNumber, date, tests, failures, errors, trigger, branch, travisBuildId)<>(Build.tupled, Build.unapply _)
  }
  object Builds {
    val builds = TableQuery[Builds]
    //val buildForInsert = builds returning builds.map(_) into { case (build) => build }
    def insertAndIncrement(ownerStr: String, projectStr: String): Build = insertAndIncrement(Build(owner=ownerStr, project=projectStr, buildNumber=0))
    def insertAndIncrement(build: Build): Build = {
      val q =findLatestBuildNumber(build.owner, build.project)
      val latestBuildNumber:Int = q.first.getOrElse(0)
      val buildToInsert = build.copy(buildNumber=latestBuildNumber+1)
      builds.insert(buildToInsert)      
      buildToInsert
    }
    def updateStats(owner: String, project: String, buildNumber: Int, testSum: Int, failureSum: Int, errorSum: Int) {
      val q2 = for {a <- builds if a.buildNumber === buildNumber} yield (a.tests, a.failures, a.errors)
      q2.update(Some(testSum), Some(failureSum), Some(errorSum))
    }
    def deleteOldestFirstUntil(buildLimit: Int, latestBuild: Build) {
      if (buildLimit <=0) return
      val q = for {a <- builds if a.buildNumber < (latestBuild.buildNumber+1)-buildLimit} yield (a)
      q.delete
    }
    def findByOwnerAndProject(owner: String, project: String) = (for {a <- builds if a.owner === owner && a.project === project} yield (a))
    def findByBuildNumber(buildNumber: Int) = (for {a <- builds if a.buildNumber === buildNumber} yield (a))
    def findLatestBuildNumber(owner: String, project: String) = (for {a <- builds if a.owner === owner && a.project === project} yield (a.buildNumber)).max
    def findLatestBuilds() = (for {a <- builds} yield (a)).sortBy(_.date.desc)
  }
}
