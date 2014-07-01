package model

import java.net.URI
import scala.util.Properties
import com.mchange.v2.c3p0.ComboPooledDataSource
import scala.slick.jdbc.JdbcBackend.Database
import scala.slick.driver.{ExtendedProfile, H2Driver, MySQLDriver}
import com.mchange.v2.log._
import Database.dynamicSession

object DB {

  lazy val db = sys.props.get("Database").getOrElse("mysql") match {
    case "mysql" => DB.getSlickMysqlConnection()
    case "h2" => DB.getSlickHSQLDatabase()
  }
  lazy val dal = sys.props.get("Database").getOrElse("mysql") match {
    case "mysql" => new DAL(MySQLDriver)
    case "h2" => new DAL(H2Driver)
  }

  def dbConfigUrl: String = {
    Properties.envOrElse("CLEARDB_DATABASE_URL", "mysql://root:mysql@127.0.0.1:3306/unitcover")
  }

  def WithPoolLogging() {
   System.setProperty("com.mchange.v2.log.MLog","com.mchange.v2.log.FallbackMLog")
   System.setProperty("com.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL","ALL")
  }
  def getSlickMysqlDataSource(jdbcUrl: String = dbConfigUrl) = {
    val dbConnectionInfo = parseDbUrl(jdbcUrl)
    val ds = new ComboPooledDataSource
    ds.setDriverClass("com.mysql.jdbc.Driver")
    ds.setJdbcUrl(dbConnectionInfo._1)
    ds.setUser(dbConnectionInfo._2)
    ds.setPassword(dbConnectionInfo._3)
    ds.setMaxPoolSize(10)
    ds.setPreferredTestQuery("Select 1")
    ds.setIdleConnectionTestPeriod(30)
    ds.setTestConnectionOnCheckin(true)
    ds.setTestConnectionOnCheckout(true)
    ds.setMaxIdleTime(300)
    ds.setDebugUnreturnedConnectionStackTraces(true)
    ds
  }
  
  def getSlickMysqlConnection(jdbcUrl: String = dbConfigUrl) = {
    Database.forDataSource(getSlickMysqlDataSource(jdbcUrl))
  }
  
  def getSlickMysqlJdbcConnection(jdbcUrl: String = dbConfigUrl) = {
    val dbConnectionInfo = parseDbUrl(jdbcUrl)
    Database.forURL(dbConnectionInfo._1, driver="com.mysql.jdbc.Driver", user=dbConnectionInfo._2, password=dbConnectionInfo._3)
  }
  
  def getSlickHSQLDatabase(jdbcUrl: String = "jdbc:hsqldb:mem:test1") = {
    val ds = new ComboPooledDataSource
    ds.setDriverClass("org.hsqldb.jdbc.JDBCDriver")
    ds.setJdbcUrl(jdbcUrl + ";sql.enforce_size=false")
    Database.forDataSource(ds)
  }
  
  def parseConfiguredDbUrl() = parseDbUrl()
  
  def parseDbUrl(mysqlUrl: String = dbConfigUrl) = {
    val dbUri = new URI(mysqlUrl);
  
    val username = dbUri.getUserInfo().split(":").head
    val password = dbUri.getUserInfo().split(":").last
    val port = if (dbUri.getPort() == -1) "" else s":${dbUri.getPort()}"
  
    val dbUrl = "jdbc:mysql://" + dbUri.getHost() + port + dbUri.getPath() + "?autoReconnectForPools=true&reconnect=true&useUnicode=yes&characterEncoding=UTF-8"
    (dbUrl, username, password)
  }
}
