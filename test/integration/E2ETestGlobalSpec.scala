package integration

import model.{DB, DAL}
import test.E2ETestGlobal
import play.api.Play
import play.api.Play.current
import play.api.test._
import play.api.test.Helpers._
import org.specs2.execute.AsResult
import org.specs2.matcher.DataTables
import org.specs2.mutable.Before
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession


class E2ETestGlobalSpec extends PlaySpecification with DataTables {
  sys.props.+=("Database" -> "h2")
  import DB.dal
  import dal._
  import DB.dal.profile.simple._
 
  def around[T: AsResult](f: => T) = {
    running(FakeApplication()) {
      E2ETestGlobal.onStart(Play.application)
      DB.db withDynSession {
        AsResult(f)
      }
    }
  }

  "teat data configuration for e2e" should {
    "eleven testsuites exists" in { 
        "id" | "buildNumber" |  "owner"         |  "project"   |    "name"           |    "tests"   | "failures" |  "errors"  |  "duration" |
        1    ! 1             !  "pussinboots"  !!  "bankapp"  !!    "testsuite"     !!    5         ! 1          !  2         !  1000.0     |
        2    ! 2             !  "pussinboots"  !!  "bankapp"  !!    "testsuite 2"   !!    8         ! 0          !  0         !  1000.0     |
        3    ! 3             !  "pussinboots"  !!  "bankapp"  !!    "testsuite 3"   !!    8         ! 0          !  0         !  1000.0     |
        4    ! 4             !  "pussinboots"  !!  "bankapp"  !!    "testsuite 4"   !!    8         ! 0          !  0         !  1000.0     |
        5    ! 5             !  "pussinboots"  !!  "bankapp"  !!    "testsuite 5"   !!    8         ! 0          !  0         !  1000.0     |
        6    ! 6             !  "pussinboots"  !!  "bankapp"  !!    "testsuite 6"   !!    8         ! 0          !  0         !  1000.0     |
        7    ! 7             !  "pussinboots"  !!  "bankapp"  !!    "testsuite 7"   !!    8         ! 0          !  0         !  1000.0     |
        8    ! 8             !  "pussinboots"  !!  "bankapp"  !!    "testsuite 8"   !!    8         ! 1          !  0         !  1000.0     |
        9    ! 9             !  "pussinboots"  !!  "bankapp"  !!    "testsuite 9"   !!    8         ! 1          !  0         !  1000.0     |
        10   ! 10            !  "pussinboots"  !!  "bankapp"  !!    "testsuite 10"  !!    8         ! 1          !  1         !  1000.0     |
        11   ! 11            !  "pussinboots"  !!  "bankapp"  !!    "testsuite 11"  !!    8         ! 1          !  1         !  1000.0     |> { (id, buildNumber, owner, project, name, tests, failures, errors, duration)=>around{
            val testSuite = dal.findById(id).first
            testSuite.id must beEqualTo(Some(id))
            testSuite.buildNumber must beEqualTo(buildNumber)
            testSuite.owner must beEqualTo(owner)
            testSuite.project must beEqualTo(project)
            testSuite.name must beEqualTo(name)
            testSuite.tests must beEqualTo(Some(tests))
            testSuite.failures must beEqualTo(Some(failures))
            testSuite.errors must beEqualTo(Some(errors))
            testSuite.duration must beEqualTo(Some(duration))
          }
        }
    }

    "one test case with detailed failure message exists" in { 
        "id" | "testSuiteId" |  "owner"         |  "project"   |    "name"                               |   "className" |    "failureMessage"       | "detailFailureMessage"            | "typ"   |  "duration" |
        1    ! 1             !  "pussinboots"  !!  "bankapp"  !!    "testcase"                          !!  "testclass" !!   None                    ! None                              ! None    !!  1000.0    |
        2    ! 2             !  "pussinboots"  !!  "bankapp"  !!    "testcase 2 testsuite 2"            !!  "testclass" !!   None                    ! None                              ! None    !!  1000.0    |> { (id, testSuiteId, owner, project, name, className, failureMessage, detailFailureMessage, typ, duration)=>around{
            val testCase = findBySuite(testSuiteId).first
            testCase.id must beEqualTo(Some(id))
            testCase.testSuiteId must beEqualTo(testSuiteId)
            testCase.owner must beEqualTo(owner)
            testCase.project must beEqualTo(project)
            testCase.name must beEqualTo(name)
            testCase.className must beEqualTo(className)
            testCase.typ must beEqualTo(typ)
            testCase.failureMessage must beEqualTo(failureMessage)
            if(failureMessage != None /*|| errorMessage != None*/) {
                val message = findByCaseId(testCase.id.get).first
                if(failureMessage != None) {
                    message.message must beEqualTo(detailFailureMessage.get)
                    message.typ must beEqualTo(0)
                }
                /*if(errorMessage != None) {
                    message.message must beEqualTo(errorMessage.get)
                    message.typ must beEqualTo(1)
                }*/
            } else {
                val message = findByCaseId(testCase.id.get).firstOption
                message must beNone
            }
            testCase.duration must beEqualTo(Some(duration))

            
          }
        }
    }

    "eleven builds exists" in { 
        "buildNumber" |  "owner"         |  "project"   |        "tests"   | "failures" |  "errors"  | 
        1             !  "pussinboots"  !!  "bankapp"        !!        5         ! 1          !  2         | 
        2             !  "pussinboots"  !!  "bankapp"       !!        2         ! 0          !  0         | 
        3             !  "pussinboots"  !!  "bankapp"       !!        3         ! 0          !  0         | 
        4             !  "pussinboots"  !!  "bankapp"       !!        4         ! 0          !  0         | 
        5             !  "pussinboots"  !!  "bankapp"       !!        5         ! 0          !  0         | 
        6             !  "pussinboots"  !!  "bankapp"       !!        6         ! 0          !  0         | 
        7             !  "pussinboots"  !!  "bankapp"       !!        7         ! 0          !  0         | 
        8             !  "pussinboots"  !!  "bankapp"       !!        8         ! 1          !  0         | 
        9             !  "pussinboots"  !!  "bankapp"       !!        9         ! 1          !  0         | 
        10            !  "pussinboots"  !!  "bankapp"       !!        10        ! 1          !  1         | 
        11            !  "pussinboots"  !!  "bankapp"       !!        11        ! 1          !  1         |
        1             !  "otherowner"   !!  "otherproject"  !!        20        ! 0          !  1         |> { (buildNumber, owner, project, tests, failures, errors)=>around{
            val build = Builds.findByBuildNumber(owner, project, buildNumber).first
            build.buildNumber must beEqualTo(buildNumber)
            build.owner must beEqualTo(owner)
            build.project must beEqualTo(project)
            build.tests must beEqualTo(Some(tests))
            build.failures must beEqualTo(Some(failures))
            build.errors must beEqualTo(Some(errors))          
            build.travisBuildId must beEqualTo(Some(s"$buildNumber"))    
          }
        }
    }
  }
}
