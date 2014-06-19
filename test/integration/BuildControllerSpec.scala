package integration

import org.specs2.mutable.Specification
import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json.Json
import play.api.libs.ws._
import play.api.Play
import unit.org.stock.manager.test.DatabaseSetupBefore

import model.{DB, Build}
import scala.slick.session.Database
import Database.threadLocalSession

class BuildControllerSpec extends PlaySpecification with DatabaseSetupBefore {
	sequential
	implicit def toOption[A](value: A) : Option[A] = Some(value)
	import DB.dal._
	import DB.dal.profile.simple._
	import model.JsonHelper._

	"POST to /api/<owner>/<project>/builds" should {
		"create a new build and return its buildNumber" in new WithServer { 
			val response = await(WS.url(s"http://localhost:$port/api/pussinboots/bankapp/builds").post(""), 10000)
			response.status must equalTo(OK)
			val buildNumber = (response.json \ "buildNumber").as[Int]
			DB.db withSession {
				checkBuild(buildNumber)
			}
		}

		"create two new builds and second build should have buildname equal two" in new WithServer { 
			val response1 = await(WS.url(s"http://localhost:$port/api/pussinboots/bankapp/builds").post(""), 10000)
			val response2 = await(WS.url(s"http://localhost:$port/api/pussinboots/bankapp/builds").post(""), 10000)
			response1.status must equalTo(OK)
			response2.status must equalTo(OK)
			val buildNumber1 = (response1.json \ "buildNumber").as[Int]
			val buildNumber2 = (response2.json \ "buildNumber").as[Int]
			DB.db withSession {
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
			val response0 = await(WS.url(s"http://localhost:$port/api/pussinboots/bankapp/1").withHeaders("Content-Type" -> "text/xml").post(xmlString), 10000)
			val response1 = await(WS.url(s"http://localhost:$port/api/pussinboots/bankapp/builds").post(""), 10000)
			val response2 = await(WS.url(s"http://localhost:$port/api/pussinboots/bankapp/builds/1/end").post(""), 10000)
			response1.status must equalTo(OK)
			response2.status must equalTo(OK)
			println(response2.json)
			DB.db withSession {
				val build = Builds.findByBuildNumber(1).firstOption.get
				build.buildNumber must beEqualTo(1)
				build.owner must beEqualTo("pussinboots")
				build.project must beEqualTo("bankapp")
				build.tests must beEqualTo(Some(11))
			}
		}
	}

	def insert10Builds() {
		DB.db withSession {
			for(i <- 1 to 11)
				Builds.insertAndIncrement("pussinboots", "bankapp")
		}	
	}

	//then
    def checkBuild(buildId: Int) {
		val build = Builds.findByBuildNumber(buildId).firstOption.get
		build.id must beEqualTo(Some(buildId))
		build.buildNumber must beEqualTo(1)
		build.owner must beEqualTo("pussinboots")
		build.project must beEqualTo("bankapp")
	}

	def checkSecondBuild(buildId: Int) {
		val build = Builds.findByBuildNumber(buildId).firstOption.get
		build.buildNumber must beEqualTo(2)
		build.owner must beEqualTo("pussinboots")
		build.project must beEqualTo("bankapp")
	}
}