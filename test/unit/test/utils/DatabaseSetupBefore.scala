package unit.test.utils

import org.specs2.mutable.Before
import org.specs2.execute.AsResult
import model.DB
import scala.slick.jdbc.JdbcBackend.Database
import Database.dynamicSession
import test.SetupTestDatabase

trait DatabaseSetupBefore extends SlickDbBefore {

  implicit val googleId = "test googleId"
  var now:java.sql.Timestamp = null
  var yesterday:Option[java.sql.Timestamp] = null
  override def initTestData(db: Database) {
    import DB.dal._
    import DB.dal.profile.simple._

    db.withDynSession {
      val result = SetupTestDatabase.insertTestData()
      now = result._1
      yesterday = result._2
    }
  }

  def around[T: AsResult](f: => T) = {
    DB.db withDynSession {
      AsResult(f)
    }
  }
}
