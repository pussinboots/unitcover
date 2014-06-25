package test

import play.api._
import model.DB
import global.Global

object E2ETestGlobal extends GlobalSettings {
  override def onStart(app: Application) {
    sys.props.+=("Database" -> "h2")
    val enablePoolLogging = app.configuration.getBoolean("enablePoolLogging").getOrElse(false)
    if(!enablePoolLogging) {
      System.clearProperty("com.mchange.v2.log.MLog")
      System.clearProperty("com.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL")
    }
    DB.db withDynSession {
     SetupTestDatabase.insertTestData()
     SetupTestDatabase.insertE2ETestData()
   }
   Logger.trace("Application for e2e test has started")
 }
}