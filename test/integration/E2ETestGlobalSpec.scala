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
      
  val googleId  = "test googleId"
  val googleIdEnc  = "test googleid encrypted"

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
        8    ! 8             !  "pussinboots"  !!  "bankapp"  !!    "testsuite 8"   !!    8         ! 0          !  0         !  1000.0     |
        9    ! 9             !  "pussinboots"  !!  "bankapp"  !!    "testsuite 9"   !!    8         ! 0          !  0         !  1000.0     |
        10   ! 10            !  "pussinboots"  !!  "bankapp"  !!    "testsuite 10"  !!    8         ! 0          !  0         !  1000.0     |
        11   ! 11            !  "pussinboots"  !!  "bankapp"  !!    "testsuite 11"  !!    8         ! 0          !  0         !  1000.0     |> { (id, buildNumber, owner, project, name, tests, failures, errors, duration)=>around{
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

    "eleven builds exists" in { 
        "id" | "buildNumber" |  "owner"         |  "project"   |        "tests"   | "failures" |  "errors"  | 
        1    ! 1             !  "pussinboots"  !!  "bankapp"  !!        1         ! 0          !  0         | 
        2    ! 2             !  "pussinboots"  !!  "bankapp"  !!        2         ! 0          !  0         | 
        3    ! 3             !  "pussinboots"  !!  "bankapp"  !!        3         ! 0          !  0         | 
        4    ! 4             !  "pussinboots"  !!  "bankapp"  !!        4         ! 0          !  0         | 
        5    ! 5             !  "pussinboots"  !!  "bankapp"  !!        5         ! 0          !  0         | 
        6    ! 6             !  "pussinboots"  !!  "bankapp"  !!        6         ! 0          !  0         | 
        7    ! 7             !  "pussinboots"  !!  "bankapp"  !!        7         ! 0          !  0         | 
        8    ! 8             !  "pussinboots"  !!  "bankapp"  !!        8         ! 1          !  0         | 
        9    ! 9             !  "pussinboots"  !!  "bankapp"  !!        9         ! 1          !  0         | 
        10   ! 10            !  "pussinboots"  !!  "bankapp"  !!        10         ! 1          !  1         | 
        11   ! 11            !  "pussinboots"  !!  "bankapp"  !!        11         ! 1          !  1         |> { (id, buildNumber, owner, project, tests, failures, errors)=>around{
            val testSuite = Builds.findByBuildNumber(buildNumber).first
            testSuite.id must beEqualTo(Some(id))
            testSuite.buildNumber must beEqualTo(buildNumber)
            testSuite.owner must beEqualTo(owner)
            testSuite.project must beEqualTo(project)
            testSuite.tests must beEqualTo(Some(tests))
            testSuite.failures must beEqualTo(Some(failures))
            testSuite.errors must beEqualTo(Some(errors))          
            testSuite.travisBuildId must beEqualTo(Some(s"$buildNumber"))    
          }
        }
    }
  }
}