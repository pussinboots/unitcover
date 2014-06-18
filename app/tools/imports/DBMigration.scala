package tools.imports

import scala.slick.driver.MySQLDriver
import model.DB
import model.DAL
import scala.slick.session.Database
import Database.threadLocalSession

object DBMigration extends App {
  DB.WithSSL()
  val db = DB.getSlickMysqlJdbcConnection()
  val dao = new DAL(MySQLDriver)
  import dao._
  import dao.profile.simple._

  db withSession {
    println("create tables")
    dao.create
  }
}
