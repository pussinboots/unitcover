package controllers

import play.api.libs.json._
import play.api.libs.json.Json
import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.mvc.BodyParsers
import play.api.mvc.Controller
import model.{DB, TestSuite, TestCase}
import scala.slick.session.Database
import Database.threadLocalSession
import DB.dal.profile.simple.{Query => SlickQuery}
import java.sql.Timestamp

object TestSuiteController extends Controller {

  import controllers.ControllerHelpers._
  import DB.dal._
  import DB.dal.profile.simple._
  import model.SlickHelpers._
  import model.JsonHelper._

  import scala.xml.NodeSeq
  implicit class ExtendedNodeSeq(nodeSeq: NodeSeq) {
    def textOption: Option[String] = {
      val text = nodeSeq.text
      if (text == null || text.length == 0) None else Some(text)
    }
  }

  def saveTestSuite(owner: String, project: String) = ActionWithoutToken(BodyParsers.parse.xml) {request =>
      val testSuiteXml = (request.body)
      val testSuiteData = ((testSuiteXml \ "@name").text, 
                           (testSuiteXml \ "@tests").text.toInt, 
                           (testSuiteXml \ "@failures").text.toInt, 
                           (testSuiteXml \ "@errors").text.toInt, 
                           (testSuiteXml \ "@skipped").text.toInt, 
                           (testSuiteXml \ "@time").text.toDouble)

      val testCasesData = (testSuiteXml \ "testcase").map(testCaseNode => (
                          (testCaseNode \ "@name").text,
                          (testCaseNode \ "@classname").text, 
                          (testCaseNode \ "@time").text.toDouble,
                          (testCaseNode \ "failure" \ "@message").textOption,
                          (testCaseNode \ "error" \ "@message").textOption,
                          (testCaseNode \ "failure" \ "@type").textOption,
                          (testCaseNode \ "error" \ "@type").textOption))
      val suite = TestSuite(None, 1, owner, project, testSuiteData._1, testSuiteData._2, testSuiteData._3, testSuiteData._4, testSuiteData._6)
      val testSuite = DB.db withSession TestSuites.insert(suite)
      def getType(failureType: Option[String], errorType: Option[String]) = {
        if (failureType !=None) failureType
        else errorType
      }
      DB.db withSession testCasesData.map(testCase=>(
                          TestCases.insert(TestCase(None, testSuite.id.get, owner, project, testCase._1, 
                                                    testCase._2 , testCase._3, testCase._4, testCase._5, 
                                                    getType(testCase._6, testCase._7) 
                                                    )
                                          )
                        ))
      Ok(Json.obj("id" -> testSuite.id.get))
}

  def latestTestSuites(owner: String, project: String) = ActionWithoutToken {request =>
    DB.db withSession  {
      var query = TestSuites.findBy(owner, project).sortBy(_.id.desc)
      val json = query.take(10).list()
      val count = query.list.length
      Ok(Json.stringify(Json.toJson(JsonFmtListWrapper(json, count)))) as ("application/json")
    }
  }
}
