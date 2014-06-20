package tools.imports

import scala.slick.driver.MySQLDriver
import model.DB
import model.DAL
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession

object DBMigration extends App {
  val db = DB.getSlickMysqlJdbcConnection()
  val dao = new DAL(MySQLDriver)
  import dao._
  import dao.profile.simple._

  db withDynSession {
    println("create tables")
    dao.create
  }
}
