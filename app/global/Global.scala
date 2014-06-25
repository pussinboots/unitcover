package global

import play.api._
import play.api.mvc._
import scala.concurrent.Future
import model.DB

object Global extends GlobalSettings {
  override def onStart(app: Application) {
    val enablePoolLogging = app.configuration.getBoolean("enablePoolLogging").getOrElse(false)
    if(enablePoolLogging) {
      DB.WithPoolLogging()
    } else {
      System.clearProperty("com.mchange.v2.log.MLog")
      System.clearProperty("com.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL")
    }
  }
}