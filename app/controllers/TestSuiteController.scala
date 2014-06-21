package controllers

import play.api.libs.json._
import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.mvc.Controller
import play.api.mvc.BodyParsers
import model.{DB, TestSuite, TestCase}
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession
import java.sql.Timestamp

object TestSuiteController extends Controller {

  import controllers.ControllerHelpers._
  import DB.dal
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

  implicit class ExtendOption(option: Option[String]) {
    def toDouble:Option[Double] = option.map(_.toDouble)
    

    def toInt:Option[Int] = option.map(_.toInt)
  }

  def saveTestSuite(owner: String, project: String, buildNumber: Int) = ActionWithoutToken(BodyParsers.parse.xml) {request =>
      //extract multiple test suites like karma unit report
      val testSuites = (request.body \ "testsuite").map{testSuite=>{
        parseTestSuite(owner, project, testSuite, buildNumber)
      }}
      if (testSuites.length > 0) {
        Ok(Json.obj("testsuites" -> Json.arr(testSuites.map(testSuite=>Json.obj("id" ->testSuite.id.get)))))
      } else {//extract only one test suite like sbt junit report
        val testSuite = parseTestSuite(owner, project, request.body, buildNumber)
        Ok(Json.obj("testsuites" -> Json.arr(Json.obj("id" ->testSuite.id.get))))
      }
}

def parseTestSuite(owner: String, project: String, testSuiteNode: NodeSeq, buildNumber: Int) = {
  val testSuiteXml = testSuiteNode 
  val testSuiteData = ((testSuiteXml \ "@name").text, 
                       (testSuiteXml \ "@tests").textOption.toInt, 
                       (testSuiteXml \ "@failures").textOption.toInt, 
                       (testSuiteXml \ "@errors").textOption.toInt, 
                       (testSuiteXml \ "@skipped").textOption.toInt, 
                       (testSuiteXml \ "@time").textOption.toDouble)

  val testCasesData = (testSuiteXml \ "testcase").map(testCaseNode => (
                      (testCaseNode \ "@name").text,
                      (testCaseNode \ "@classname").text, 
                      (testCaseNode \ "@time").textOption.toDouble,
                      (testCaseNode \ "failure" \ "@message").textOption,
                      (testCaseNode \ "error" \ "@message").textOption,
                      (testCaseNode \ "failure" \ "@type").textOption,
                      (testCaseNode \ "error" \ "@type").textOption,
                      (testCaseNode \ "failure").textOption,
                      (testCaseNode \ "error").textOption))
  val suite = TestSuite(None, buildNumber, owner, project, testSuiteData._1, testSuiteData._2, testSuiteData._3, testSuiteData._4, testSuiteData._6)
  val testSuite = DB.db withDynSession dal.testSuiteForInsert.insert(suite)
  def getType(failureType: Option[String], errorType: Option[String]) = {
    if (failureType !=None) failureType
    else errorType
  }
  def getMessage(message1: Option[String], message2: Option[String]) = {
    if (message1 !=None) message1
    else message2
  }
  DB.db withDynSession testCasesData.map(testCase=>(
                      dal.testCases.insert(TestCase(None, testSuite.id.get, owner, project, testCase._1, 
                                                testCase._2 , testCase._3, getMessage(testCase._4, testCase._8), getMessage(testCase._5, testCase._9), 
                                                getType(testCase._6, testCase._7) 
                                                )
                                      )
                    ))
  testSuite
}

  def latestTestSuites(owner: String, project: String, buildNumber: Int) = ActionWithoutToken {request =>
    DB.db withDynSession  {
      var query = dal.findBy(owner, project, buildNumber).sortBy(_.id.desc)
      val json = query.list()
      val count = query.list.length
      Ok(Json.stringify(Json.toJson(JsonFmtListWrapper(json, count)))) as ("application/json")
    }
  }
}
