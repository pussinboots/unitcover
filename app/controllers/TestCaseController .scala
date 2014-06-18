package controllers

import play.api.libs.json._
import play.api.libs.json.Json
import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.mvc.BodyParsers
import play.api.mvc.Controller
import model.{DB, TestCase}
import scala.slick.session.Database
import Database.threadLocalSession
import DB.dal.profile.simple.{Query => SlickQuery}
import java.sql.Timestamp

object TestCaseController extends Controller {

  import controllers.ControllerHelpers._
  import DB.dal._
  import DB.dal.profile.simple._
  import model.SlickHelpers._
  import model.JsonHelper._

  def findTestCases(owner: String, project: String, testSuiteId: Long) = ActionWithoutToken {request =>
    DB.db withSession  {
      var query = TestCases.findBySuite(testSuiteId).sortBy(_.id.desc)
      val json = query.list()
      val count = query.list.length
      Ok(Json.stringify(Json.toJson(JsonFmtListWrapper(json, count)))) as ("application/json")
    }
  }
}
