package controllers

import play.api.libs.ws._
import play.api.libs.json._
import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.mvc.BodyParsers
import play.api.mvc.Controller
import play.api.mvc.Action
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.Play.current
import model.{DB, Build, TestSuite}
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession
import java.sql.Timestamp
import play.api.cache.Cache
import cache.CacheProvider
import scala.concurrent.Future

object BadgeController extends Controller {

  import controllers.ControllerHelpers._
  import DB.dal._
  import DB.dal.profile.simple._
  import model.SlickHelpers._
  import model.JsonHelper._

  def status(owner: String, project: String) = Action {request =>
    DB.db withDynSession  {
      var build = Builds.findByOwnerAndProject(owner, project).sortBy(_.buildNumber.desc).firstOption
      build match {
        case Some(build) => Ok(Json.stringify(Json.toJson(build))) as ("application/json")
        case None => NotFound(Json.obj("error" -> 404, "error_message" -> s"For $owner $project there exists no build.")) as ("application/json")
      }
    }
  }
  
  def badgeUrl(build: Option[Build]) = {
    build match {
      case Some(build) => val desc = if(build.errors.getOrElse(0) > 0) "error" else if (build.failures.getOrElse(0) > 0 ) "failed" else "passed"
                          val color = if(build.errors.getOrElse(0) > 0) "red" else if (build.failures.getOrElse(0) > 0 ) "yellow" else "brightgreen"
                          val count = if(build.errors.getOrElse(0) > 0) build.errors.get else if (build.failures.getOrElse(0) > 0 ) build.failures.get else build.tests.getOrElse(0)
                          s"http://img.shields.io/badge/test-$desc%20$count-$color.svg"
      case None => "http://img.shields.io/badge/test-unknown-lightgrey.svg"
    }  
  }

  def badge(owner: String, project: String) = Action.async {request =>
    val result: Future[String] = CacheProvider.getOrSet(s"/$owner/$project/badge",()=>{
        val result: Future[String] = DB.db withDynSession{
          var query = Builds.findByOwnerAndProject(owner, project).sortBy(_.buildNumber.desc)
          WS.url(badgeUrl(query.firstOption)).get().map { response =>
   			response.body
          }
        }
      	result
    })
    result.map{value: String=>
        Ok(value).withHeaders("Cache-Control" -> "no-cache, no-store, must-revalidate", "Etag"->s"${scala.compat.Platform.currentTime}") as ("image/svg+xml")
    }
  }
}
