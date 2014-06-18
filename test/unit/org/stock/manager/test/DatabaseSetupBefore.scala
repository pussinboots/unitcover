package unit.org.stock.manager.test

import org.specs2.mutable.Before
import org.specs2.execute.AsResult
import model.DB
import scala.slick.session.Database
import Database.threadLocalSession
import test.SetupTestDatabase

trait DatabaseSetupBefore extends SlickDbBefore {

  implicit val googleId = "test googleId"
  var now:java.sql.Timestamp = null
  var yesterday:Option[java.sql.Timestamp] = null
  override def initTestData(db: Database) {
    import DB.dal._
    import DB.dal.profile.simple._

    db.withSession {
      val result = SetupTestDatabase.insertTestData()
      now = result._1
      yesterday = result._2
    }
  }

  def around[T: AsResult](f: => T) = {
    DB.db withSession {
      AsResult(f)
    }
  }
}
