package integration

import model.{DB, DAL}
import test.E2ETestGlobal
import play.api.Play
import play.api.Play.current
import play.api.test._
import play.api.test.Helpers._
import org.specs2.execute.AsResult
import org.specs2.matcher.DataTables
import org.specs2.mutable.Before

import scala.slick.session.Database
import Database.threadLocalSession


class E2ETestGlobalSpec extends PlaySpecification with DataTables {
  sys.props.+=("Database" -> "h2")
  import DB.dal._
  import DB.dal.profile.simple._
      
  val googleId  = "test googleId"
  val googleIdEnc  = "test googleid encrypted"

  def around[T: AsResult](f: => T) = {
    running(FakeApplication()) {
      E2ETestGlobal.onStart(Play.application)
      DB.db withSession {
        AsResult(f)
      }
    }
  }
}