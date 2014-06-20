package integration

import org.specs2.mutable.Specification
import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json.Json
import play.api.libs.ws._
import play.api.Play
import unit.org.stock.manager.test.DatabaseSetupBefore

import model.{DB, Build}
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession

class BuildControllerSpec extends PlaySpecification with DatabaseSetupBefore {
	sequential
	implicit def toOption[A](value: A) : Option[A] = Some(value)
	implicit val timeout = 10000
	import DB.dal
	import DB.dal.profile.simple._
	import model.JsonHelper._

	"POST to /api/<owner>/<project>/builds" should {
		"create a new build and return its buildNumber" in new WithServer { 
			val response = await(WS.url(s"http://localhost:$port/api/pussinboots/bankapp/builds").post(""))
			response.status must equalTo(OK)
			val buildNumber = (response.json \ "buildNumber").as[Int]
			DB.db withDynSession {
				checkBuild(buildNumber)
			}
		}

		"create two new builds and second build should have buildname equal two" in new WithServer { 
			val response1 = await(WS.url(s"http://localhost:$port/api/pussinboots/bankapp/builds").post(""))
			val response2 = await(WS.url(s"http://localhost:$port/api/pussinboots/bankapp/builds").post(""))
			response1.status must equalTo(OK)
			response2.status must equalTo(OK)
			val buildNumber1 = (response1.json \ "buildNumber").as[Int]
			val buildNumber2 = (response2.json \ "buildNumber").as[Int]
			DB.db withDynSession {
				checkBuild(buildNumber1)
				checkSecondBuild(buildNumber2)
			}
		}
	}

	"GET to /api/<owner>/<project>/builds" should {
		"return latest 10 builds" in new WithServer {
			insert10Builds ()
			val response = await(WS.url(s"http://localhost:$port/api/pussinboots/bankapp/builds").get)
			response.status must equalTo(OK)
			val builds = Json.fromJson[JsonFmtListWrapper[Build]](response.json).get
			builds.count must equalTo(11)
			builds.items.length must equalTo(10)
			builds.items(0).buildNumber must equalTo(11)
		}
	}

	"upload complete build to /api/<owner>/<project>/builds" should {
		"complete build" in new WithServer {
			val xmlString = scala.io.Source.fromFile(Play.getFile("test/resources/test-results.xml")).mkString
			val response0 = await(WS.url(s"http://localhost:$port/api/pussinboots/bankapp/1").withHeaders("Content-Type" -> "text/xml").post(xmlString))
			val response1 = await(WS.url(s"http://localhost:$port/api/pussinboots/bankapp/builds").post(""))
			val response2 = await(WS.url(s"http://localhost:$port/api/pussinboots/bankapp/builds/1/end").post(""))
			response1.status must equalTo(OK)
			response2.status must equalTo(OK)
			DB.db withDynSession {
				val build = dal.findByBuildNumber(1).firstOption.get
				build.buildNumber must beEqualTo(1)
				build.owner must beEqualTo("pussinboots")
				build.project must beEqualTo("bankapp")
				build.tests must beEqualTo(Some(11))
			}
		}
	}

	"POST to /api/<owner>/<project>/builds with trigger parameter" should {
		"create a new build and return its buildNumber" in new WithServer { 
			val response = await(WS.url(s"http://localhost:$port/api/pussinboots/bankapp/builds?trigger=TravisCI").post(""))
			response.status must equalTo(OK)
			val buildNumber = (response.json \ "buildNumber").as[Int]
			DB.db withDynSession {
				checkBuildTravisCI(buildNumber)
			}
		}
	}

	"POST to /api/<owner>/<project>/builds with trigger and branch parameter" should {
		"create a new build and return its buildNumber" in new WithServer { 
			val response = await(WS.url(s"http://localhost:$port/api/pussinboots/bankapp/builds?trigger=TravisCI&branch=notMaster").post(""))
			response.status must equalTo(OK)
			val buildNumber = (response.json \ "buildNumber").as[Int]
			DB.db withDynSession {
				checkBuildTravisCINotMaster(buildNumber)
			}
		}
	}

	//given

	def insert10Builds() {
		DB.db withDynSession {
			for(i <- 1 to 11)
				dal.insertAndIncrement("pussinboots", "bankapp")
		}	
	}

	//then
	def checkBuildTravisCINotMaster(buildId: Int) {
		val build = dal.findByBuildNumber(buildId).firstOption.get
		build.id must beEqualTo(Some(buildId))
		build.buildNumber must beEqualTo(1)
		build.owner must beEqualTo("pussinboots")
		build.project must beEqualTo("bankapp")
		build.trigger must beEqualTo(Some("TravisCI"))
		build.branch must beEqualTo(Some("notMaster"))
	}

	def checkBuildTravisCI(buildId: Int) {
		val build = dal.findByBuildNumber(buildId).firstOption.get
		build.id must beEqualTo(Some(buildId))
		build.buildNumber must beEqualTo(1)
		build.owner must beEqualTo("pussinboots")
		build.project must beEqualTo("bankapp")
		build.trigger must beEqualTo(Some("TravisCI"))
	}

    def checkBuild(buildId: Int) {
		val build = dal.findByBuildNumber(buildId).firstOption.get
		build.id must beEqualTo(Some(buildId))
		build.buildNumber must beEqualTo(1)
		build.owner must beEqualTo("pussinboots")
		build.project must beEqualTo("bankapp")
	}

	def checkSecondBuild(buildId: Int) {
		val build = dal.findByBuildNumber(buildId).firstOption.get
		build.buildNumber must beEqualTo(2)
		build.owner must beEqualTo("pussinboots")
		build.project must beEqualTo("bankapp")
	}
}