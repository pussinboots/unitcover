package controllers

import play.api.libs.json._
import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.mvc.BodyParsers
import play.api.mvc.Controller
import model.{DB, Build, TestSuite}
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession
import java.sql.Timestamp
import play.api.mvc.Results

object BadgeController extends Controller {

  import controllers.ControllerHelpers._
  import DB.dal._
  import DB.dal.profile.simple._
  import model.SlickHelpers._
  import model.JsonHelper._


  def status(owner: String, project: String) = ActionWithoutToken {request =>
    DB.db withDynSession  {
      var query = Builds.findByOwnerAndProject(owner, project).sortBy(_.id.desc)
      val json = query.first
      val count = query.list.length
      Ok(Json.stringify(Json.toJson(json))) as ("application/json")
    }
  }

  def badge(owner: String, project: String) = ActionWithoutToken {request =>
    DB.db withDynSession  {
      var query = Builds.findByOwnerAndProject(owner, project).sortBy(_.id.desc)
      query.firstOption match {
        case Some(build) => println(build)
                            val desc = if(build.errors.getOrElse(0) > 0) "error" else if (build.failures.getOrElse(0) > 0 ) "failed" else "passed"
                            val color = if(build.errors.getOrElse(0) > 0) "red" else if (build.failures.getOrElse(0) > 0 ) "yellow" else "brightgreen"
                            val count = if(build.errors.getOrElse(0) > 0) build.errors.get else if (build.failures.getOrElse(0) > 0 ) build.failures.get else build.tests.getOrElse(0)
                            Results.Redirect(s"http://img.shields.io/badge/test-$desc $count-$color.svg?ts=${scala.compat.Platform.currentTime}")
        case None => Results.Redirect("http://img.shields.io/badge/test-unknown-lightgrey.svg")
      }
      
      
      /*val file = Play.getFile("images/heroku-badge.png")
      val fileContent: Enumerator[Array[Byte]] = Enumerator.fromFile(file)    
        
      SimpleResult(
        header = ResponseHeader(200, Map(CONTENT_LENGTH -> file.length.toString)),
        body = fileContent
      )*/
      /*val svg = <svg xmlns="http://www.w3.org/2000/svg" width="90" height="18">
            <linearGradient id="a" x2="0" y2="100%">
            <stop offset="0" stop-color="#fff" stop-opacity=".7"/>
            <stop offset=".1" stop-color="#aaa" stop-opacity=".1"/>
            <stop offset=".9" stop-opacity=".3"/>
            <stop offset="1" stop-opacity=".5"/>
            </linearGradient>
            <rect rx="4" width="90" height="18" fill="#555"/>
            <rect rx="4" x="37" width="53" height="18" fill="#4c1"/>
            <path fill="#4c1" d="M37 0h4v18h-4z"/>
            <rect rx="4" width="90" height="18" fill="url(#a)"/>
            <g fill="#fff" text-anchor="middle" font-family="DejaVu Sans,Verdana,Geneva,sans-serif" font-size="11">
            <text x="19.5" y="13" fill="#010101" fill-opacity=".3">tests</text>
            <text x="19.5" y="12">tests</text>
            <text x="62.5" y="13" fill="#010101" fill-opacity=".3">{json.tests.getOrElse(0)}</text>
            <text x="62.5" y="12">{json.tests.getOrElse(0)}</text>
            </g>
            </svg>
      Ok(svg) as ("image/svg+xml")*/
    }
  }
}
