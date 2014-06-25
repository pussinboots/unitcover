package model

import play.api.libs.json._
import java.sql.Timestamp
import play.api.libs.functional.syntax._
import play.api.libs.json.JsString
import play.api.libs.json.JsSuccess
import java.text.SimpleDateFormat
import java.util.Date

object JsonHelper {

  case class JsonFmtListWrapper[T](items: List[T], count: Int)

  implicit def listWrapperFormat[T: Format]: Format[JsonFmtListWrapper[T]] = (
    (__ \ "items").format[List[T]] and
    (__ \ "count").format[Int]
  )(JsonFmtListWrapper.apply, unlift(JsonFmtListWrapper.unapply))

  implicit object TimestampFormatter extends Format[Timestamp] {
    def reads(s: JsValue): JsResult[Timestamp] = JsSuccess(new java.sql.Timestamp(s.as[Long]))

    def writes(timestamp: Timestamp) = JsNumber(timestamp.getTime)
  }

  implicit val testSuiteWrites = Json.writes[TestSuite]
  implicit val testSuiteReads = Json.reads[TestSuite]

  implicit val testCaseWrites = Json.writes[TestCase]
  implicit val testCaseReads = Json.reads[TestCase]

  implicit val buildWrites = Json.writes[Build]
  implicit val buildReads = Json.reads[Build]
}