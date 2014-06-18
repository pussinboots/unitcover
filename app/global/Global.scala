package global

import play.api._
import play.api.mvc._
import scala.concurrent.Future
import model.DB

object Global extends GlobalSettings
{
    override def onStart(app: Application) {
      val enableDBSSL = app.configuration.getBoolean("enableDBSSL").getOrElse(true)
      val enablePoolLogging = app.configuration.getBoolean("enablePoolLogging").getOrElse(false)
      if(enableDBSSL) {
        //TODO set log level for test to info
        //Logger.debug("set custom truststore for cleardb mysql ssl connections")
        DB.WithSSL()
      } else {
        Logger.debug("clear system properties truststore/keystore")
        System.clearProperty("javax.net.ssl.keyStore")
        System.clearProperty("javax.net.ssl.keyStorePassword")
        System.clearProperty("javax.net.ssl.trustStore")
        System.clearProperty("javax.net.ssl.trustStorePassword")
      }
      if(enablePoolLogging) {
        //Logger.info("activate verbose db logging")
        DB.WithPoolLogging()
      } else {
        System.clearProperty("com.mchange.v2.log.MLog")
        System.clearProperty("com.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL")
      }
    }
    override def onStop(app: Application) {
    }
}