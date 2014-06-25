package integration

import play.api.libs.ws._
import play.api.test._
import play.api.test.Helpers._

class ApplicationSpec extends PlaySpecification {
  "application setup should" should {
    "no truststore and keystore are configured" in {
      running(FakeApplication()) {
        val keyStoreFile = System.getProperty("javax.net.ssl.keyStore")
        val keyStorePassword = System.getProperty("javax.net.ssl.keyStorePassword")
        val trustStoreFile = System.getProperty("javax.net.ssl.trustStore")
        val trustStorePassword = System.getProperty("javax.net.ssl.trustStorePassword")
        trustStoreFile must beEqualTo(null)
        trustStorePassword must beEqualTo(null)
        keyStoreFile must beEqualTo(null)
        keyStorePassword must beEqualTo(null)
      }
    }
    "enable DB logging" in {
      running(FakeApplication(additionalConfiguration=Map("enablePoolLogging" -> "true"))) {
        val loggingClass = System.getProperty("com.mchange.v2.log.MLog")
        val loggingLevel = System.getProperty("com.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL")
        loggingClass must beEqualTo("com.mchange.v2.log.FallbackMLog")
        loggingLevel must beEqualTo("ALL")
      }
    }

    "configured with DB logging deactivate" in {
      running(FakeApplication()) {
        val logging = System.getProperty("com.mchange.v2.log.MLog")
        logging must beEqualTo(null)
      }
    }
  }

  "application changed setup will work" should {
    "enable DB logging" in {
      running(FakeApplication(additionalConfiguration=Map("enablePoolLogging" -> "true"))) {
        val loggingClass = System.getProperty("com.mchange.v2.log.MLog")
        val loggingLevel = System.getProperty("com.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL")
        loggingClass must beEqualTo("com.mchange.v2.log.FallbackMLog")
        loggingLevel must beEqualTo("ALL")
      }
    }
  }
}
