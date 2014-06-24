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
  import dal._
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
  
  def badgeTestSuites(owner: String, project: String) = ActionWithoutToken {request =>
    DB.db withDynSession  {
      var query = dal.findBy(owner, project, Builds.findLatestBuildNumber(owner, project).first.getOrElse(0)).sortBy(_.id.desc)
      val testSuites = query.list()
      import scala.xml.NodeSeq     
      val rect = testSuites.zipWithIndex.map {entry=>
        val testSuite = entry._1
        val i= entry._2
        val y=17*i+1
        val desc = if(testSuite.errors.getOrElse(0) > 0) "error" else if (testSuite.failures.getOrElse(0) > 0 ) "failed" else "passed"
        val color = if(testSuite.errors.getOrElse(0) > 0) "red" else if (testSuite.failures.getOrElse(0) > 0 ) "yellow" else "brightgreen"
        val count = if(testSuite.errors.getOrElse(0) > 0) testSuite.errors.get else if (testSuite.failures.getOrElse(0) > 0 ) testSuite.failures.get else testSuite.tests.getOrElse(0)
        import scala.xml.NodeBuffer        
        val rectXml = new NodeBuffer
        //rectXml += (<rect rx="4" y={s"$y"} width="90" height="18" fill="#555"/>)
        rectXml += (<rect rx="4" y={s"$y-17"} x="0" width="400" height="18" fill="#4c1"/>)
        rectXml += (<rect rx="4" y={s"$y-17"} width="90" height="18" fill="url(#lgr1)"/>)
       
		val textXml = //<text x="19.5" y={s"${y-4}"} fill="#010101" fill-opacity=".3">{testSuite.name}</text>
            <text x="19.5" y={s"${y-5}"}>{testSuite.name}</text>
                //<text x="62.5" y={s"${y-4}"} fill="#010101" fill-opacity=".3">{desc} {count}</text>
                <text x="240.5" y={s"${y-5}"}>{desc} {count}</text>;
          
        (rectXml, textXml)
      }
      
      
      import collection.breakOut
      val svg = <svg xmlns="http://www.w3.org/2000/svg" width="900" height="200">
                <defs>
                 <linearGradient id="lgr1"
                      x1="0" y1="0" x2="0" y2="1">
                      <stop offset="0" stop-color="#fff" stop-opacity=".7"/>
                	  <stop offset=".1" stop-color="#aaa" stop-opacity=".1"/>
                	  <stop offset=".9" stop-opacity=".3"/>
                	  <stop offset="1" stop-opacity=".5"/>
                    </linearGradient>
                </defs>
                
      {rect.flatMap{s=>s._1}}
                <path fill="#4c1" d="M37 0h4v18h-4z"/>
                
                <g fill="#fff" text-anchor="middle" font-family="DejaVu Sans,Verdana,Geneva,sans-serif" font-size="11">
                {rect.flatMap{s=>s._2}}
                </g>
                </svg>
      Ok(svg).withHeaders("Cache-Control" -> "no-cache, no-store, must-revalidate", "Etag"->s"${scala.compat.Platform.currentTime}") as ("image/svg+xml")
    }
  }
}
