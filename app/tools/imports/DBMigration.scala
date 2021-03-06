package tools.imports

import scala.slick.driver.MySQLDriver
import model.DB
import model.DAL
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession
import scala.slick.migration.api.TableMigration
import scala.slick.migration.api.MySQLDialect
import scala.slick.migration.api.H2Dialect

object DBMigration extends App {
  val db = DB.getSlickMysqlJdbcConnection()
  val dao = new DAL(MySQLDriver)
  implicit val dialect = new MySQLDialect
  import dao._
  import dao.profile.simple._

  //update2106()
  update2606()

  def create() {
   db withDynSession {
     println("create tables")
	    //dao.create
	  }
  }

  //add trigger and branch column to builds table
  def update2006() {
  	val migrate = TableMigration(Builds.builds)
    .addColumns(_.trigger, _.branch)
    db withDynSession {
      migrate()
    }
  }

  //add travisBuildId to build table
  def update2106() {
    val migrate = TableMigration(Builds.builds)
    .addColumns(_.travisBuildId)
    db withDynSession {
     migrate()
   }
 }

 def update2606() {
    db withDynSession {
       messages.ddl.create
   }
 }
}