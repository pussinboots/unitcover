package unit.org.stock.manager.test

import org.specs2.specification.BeforeExample
import model.DB
import scala.slick.jdbc.JdbcBackend.Database
import Database.dynamicSession

trait SlickDbBefore extends BeforeExample {
  //set h2 database for tests
  sys.props.+=("Database" -> "h2")

  override def before {
    val schema = "test"
    val db = DB.db
    db withDynSession DB.dal.recreate
    initTestData(db)
  }

  def initTestData(db: Database) {}
}
