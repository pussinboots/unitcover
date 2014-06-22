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

object BadgeController extends Controller {

  import controllers.ControllerHelpers._
  import DB.dal._
  import DB.dal.profile.simple._
  import model.SlickHelpers._
  import model.JsonHelper._


  def status(owner: String, project: String) = Action {request =>
    DB.db withDynSession  {
      var query = Builds.findByOwnerAndProject(owner, project).sortBy(_.id.desc)
      Ok(Json.stringify(Json.toJson(query.first))) as ("application/json")
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
    DB.db withDynSession  {
      var query = Builds.findByOwnerAndProject(owner, project).sortBy(_.id.desc)
      WS.url(badgeUrl(query.firstOption)).get().map { response =>
          Ok(response.body).withHeaders("Cache-Control" -> "no-cache, no-store, must-revalidate") as ("image/svg+xml")
      }
    }
  }
}
