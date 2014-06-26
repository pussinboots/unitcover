package controllers

import play.api.libs.json._
import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.mvc.Controller
import model.{DB, TestCase, TestCaseJson, Message}
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession
import java.sql.Timestamp

object TestCaseController extends Controller {

  import controllers.ControllerHelpers._
  import DB.dal
  import DB.dal.profile.simple._
  import model.SlickHelpers._
  import model.JsonHelper._

implicit class TestCaseToJson(testCase: TestCase) {
    //TODO remove code duplication
    def toJson(message: Option[Message]):TestCaseJson = {
       message match {
        case Some(m) => m.typ match {
          case 0 => TestCaseJson(testCase.id, testCase.testSuiteId, testCase.owner, testCase.project,
                                  testCase.name,testCase.className,testCase.duration,testCase.failureMessage,
                                  Some(m.message),testCase.errorMessage,None,
                                  testCase.typ, hasFailures=true)  
          case 1 => TestCaseJson(testCase.id, testCase.testSuiteId, testCase.owner, testCase.project,
                                  testCase.name,testCase.className,testCase.duration,testCase.failureMessage,
                                  None,testCase.errorMessage,Some(m.message),
                                  testCase.typ, hasErrors=true)  
        }
        case None => TestCaseJson(testCase.id, testCase.testSuiteId, testCase.owner, testCase.project,
                                  testCase.name,testCase.className,testCase.duration,testCase.failureMessage,
                                  None,testCase.errorMessage,None,
                                  testCase.typ)  
      }
    }
}


  def findTestCases(owner: String, project: String, testSuiteId: Long) = ActionWithoutToken {request =>
    DB.db withDynSession  {
      val suite = dal.findById(testSuiteId).first
      var query = dal.findBySuiteWithMessages(testSuiteId).sortBy(_._1.id.desc)
      val json = query.list()
      val count = query.list.length
      val result = json map { case (testCase: TestCase, columns: (Option[Long], Option[String], Option[Int])) =>
        columns match {
          case (Some(id), Some(message), Some(typ)) => testCase.toJson(Some(Message(id, message, typ)))
          case _ => testCase.toJson(None)
        }
      }
      Ok(Json.stringify(Json.toJson(JsonFmtTestCaseListWrapper(result, count, suite.failures.getOrElse(0) > 0, suite.errors.getOrElse(0) > 0)))) as ("application/json")
    }
  }
}
