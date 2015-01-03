package controllers

import play.api.cache.Cache
import play.api.libs.json.{Json, _}
import play.api.mvc.{Controller, Action} 
import play.api.Play.current
import play.api.Play
import model.{DB, Build}
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession

object BuildController extends Controller {

  import controllers.ControllerHelpers._
  import DB.dal._
  import DB.dal.profile.simple._
  import model.SlickHelpers._
  import model.JsonHelper._

  def buildLimit = Play.current.configuration.getInt("buildslimit").get
      
  def startBuild(owner: String, project: String, trigger: Option[String], branch: Option[String], travisBuildId: Option[String]) = Action{request =>
    val build = DB.db withDynSession Builds.insertAndIncrement(Build(owner=owner, project=project, trigger=trigger, 
      buildNumber=0, branch=branch, travisBuildId=travisBuildId))
    DB.db withDynSession Builds.deleteOldestFirstUntil(buildLimit, build)
    Ok(Json.obj("buildNumber" -> build.buildNumber))
  }

  def endBuild(owner: String, project: String, buildNumber: Int) = ActionWithoutToken {request =>
    DB.db withDynSession {
      var query = DB.dal.findResultsBy(owner, project, buildNumber)
      val stats = query.list
      val tests = stats.map(_._1.getOrElse(0)).sum
      val failures = stats.map(_._2.getOrElse(0)).sum
      val errors = stats.map(_._3.getOrElse(0)).sum
      Builds.updateStats(owner, project, buildNumber, tests, failures, errors)
      Cache.remove(s"$owner-$project-badge")
    }
    Ok(Json.obj("build" -> "good"))
  }
  
  def latestBuilds(owner: String, project: String) = ActionWithoutToken {request =>
    DB.db withDynSession  {
      var query = Builds.findByOwnerAndProject(owner, project).sortBy(_.buildNumber.desc)
      Ok(toJson(query)) as ("application/json")
    }
  }
  
  def latests() = ActionWithoutToken {request =>
    DB.db withDynSession {
      var query = Builds.findLatestBuilds()
      Ok(toJson(query)) as ("application/json")
    }
  }
  
  def toJson(query: Query[model.DB.dal.Builds,model.Build, Seq]) = {
    val json = query.take(10).list
    val count = query.list.length
    Json.stringify(Json.toJson(JsonFmtListWrapper(json, count)))    
  }
}
