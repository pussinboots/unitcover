package integration

import org.specs2.mutable.Specification
import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json.Json
import play.api.libs.ws._
import play.api.Play
import play.api.Play.current
import org.specs2.execute.AsResult
import unit.test.utils.DatabaseSetupBefore
import test.E2ETestGlobal

import model.{DB, Build}
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession

import controllers.BadgeController

class BadgeControllerSpec extends PlaySpecification with DatabaseSetupBefore {
	import DB.dal._
	import DB.dal.profile.simple._
	def setupTestData[T: AsResult](f: => T) = {
    running(FakeApplication()) {
      E2ETestGlobal.onStart(Play.application)
      DB.db withDynSession {
        AsResult(f)
      }
    }
  }

  "BadgeController" should {
	"tests passed build return tests passed badge" in {setupTestData{
	    val build = Builds.findByBuildNumber("pussinboots", "bankapp", 7).firstOption
	    val badgeUrl = BadgeController.badgeUrl(build)
	    val desc = "passed"
	    val count = 7
	    val color = "brightgreen"
	    badgeUrl must equalTo(s"http://img.shields.io/badge/test-$desc%20$count-$color.svg")
	  }	
	}
	
	"tests failed build return tests failed badge" in {setupTestData{
	    val build = Builds.findByBuildNumber("pussinboots", "bankapp", 9).firstOption
	    val badgeUrl = BadgeController.badgeUrl(build)
	    val desc = "failed"
	    val count = 1
	    val color = "yellow"
	    badgeUrl must equalTo(s"http://img.shields.io/badge/test-$desc%20$count-$color.svg")
	  }	
	}
	
	"tests errored build return tests error badge" in {setupTestData{ 
	    val build = Builds.findByBuildNumber("pussinboots", "bankapp", 10).firstOption
	    val badgeUrl = BadgeController.badgeUrl(build)
	    val desc = "error"
	    val count = 1
	    val color = "red"
	    badgeUrl must equalTo(s"http://img.shields.io/badge/test-$desc%20$count-$color.svg")
	  }	
	}
	
	"build not exists return tests unknown" in {setupTestData{ 
	    val build = Builds.findByBuildNumber("pussinboots", "bankapp", 200).firstOption
	    val badgeUrl = BadgeController.badgeUrl(build)
	    badgeUrl must equalTo("http://img.shields.io/badge/test-unknown-lightgrey.svg")
	  }	
	}
  }
}
