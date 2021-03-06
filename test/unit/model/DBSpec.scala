package unit.model
import play.api.test.PlaySpecification
import scala.util.Properties
import model.DB

class DBSpec extends PlaySpecification {
  
  "DB" should {
    "given no jdbc url as CLEARDB_DATABASE_URL property return local jdbc url" in {
      val dburl = DB.parseDbUrl()
      dburl._1 must beEqualTo("jdbc:mysql://127.0.0.1:3306/unitcover?autoReconnectForPools=true&reconnect=true&useUnicode=yes&characterEncoding=UTF-8")
      dburl._2 must beEqualTo("root")
      dburl._3 must beEqualTo("mysql")
    }
    "given any jdbc url as CLEARDB_DATABASE_URL property don't use ssl database communication" in {
      val dburl = DB.parseDbUrl()
      dburl._1 must not contain("useSSL=true")    
    }

    "given default CLEARDB_DATABASE_URL property return mysql datasource" in {
      val datasource = DB.getSlickMysqlDataSource()
      datasource.getJdbcUrl() must beEqualTo("jdbc:mysql://127.0.0.1:3306/unitcover?autoReconnectForPools=true&reconnect=true&useUnicode=yes&characterEncoding=UTF-8")
      datasource.getDriverClass() must beEqualTo("com.mysql.jdbc.Driver")
      datasource.getUser() must beEqualTo("root")
      datasource.getPassword() must beEqualTo("mysql")
      datasource.getMaxPoolSize() must beEqualTo(10)
      datasource.getPreferredTestQuery() must beEqualTo("Select 1")
      datasource.getIdleConnectionTestPeriod() must beEqualTo(30)
      datasource.isTestConnectionOnCheckin() must beEqualTo(true)
      datasource.isTestConnectionOnCheckout() must beEqualTo(true)
      datasource.getMaxIdleTime() must beEqualTo(300)
      datasource.isDebugUnreturnedConnectionStackTraces() must beEqualTo(true)
    }
  }
}
