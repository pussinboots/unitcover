package controllers

import play.api.libs.json._
import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.mvc.BodyParsers
import play.api.mvc.Controller
import model.{DB, Build, TestSuite}
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession
import java.sql.Timestamp

object BuildController extends Controller {

  import controllers.ControllerHelpers._
  import DB.dal
  import DB.dal.profile.simple._
  import model.SlickHelpers._
  import model.JsonHelper._

  def startBuild(owner: String, project: String, trigger: Option[String], branch: Option[String], travisBuildId: Option[String]) = ActionWithoutToken {request =>
      val build = DB.db withDynSession dal.insertAndIncrement(Build(id=None, owner=owner, project=project, trigger=trigger, 
                                                                    buildNumber=0, branch=branch, travisBuildId=travisBuildId))
      Ok(Json.obj("buildNumber" -> build.buildNumber))
  }


  def endBuild(owner: String, project: String, buildNumber: Int) = ActionWithoutToken {request =>
      DB.db withDynSession {
        var query = dal.findResultsBy(owner, project, buildNumber)
        val stats = query.list
        val tests = stats.map(_._1.getOrElse(0)).sum
        val failures = stats.map(_._2.getOrElse(0)).sum
        val errors = stats.map(_._3.getOrElse(0)).sum
        dal.updateStats(owner, project, buildNumber, tests, failures, errors)
      }
      Ok(Json.obj("build" -> "good"))
  }
  
  def latestBuilds(owner: String, project: String) = ActionWithoutToken {request =>
    DB.db withDynSession  {
      var query = dal.findByOwnerAndProject(owner, project).sortBy(_.id.desc)
      val json = query.take(10).list()
      val count = query.list.length
      Ok(Json.stringify(Json.toJson(JsonFmtListWrapper(json, count)))) as ("application/json")
    }
  }
}
