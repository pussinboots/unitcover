package integration

import play.api.libs.ws._
import play.api.test._
import play.api.test.Helpers._

class ApplicationSpec extends PlaySpecification {
  sequential
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

    /*"check reditect to products.html work" in new WithServer(FakeApplication()) {
      val response = await(WS.url(s"http://localhost:$port/").withFollowRedirects(false).get)
      response.header("Location") must beEqualTo(Some(s"products.html"))
    }*/

    //TODO not working in travis try to fix it later
    /*"configured to redirect all http request to https on heroku" in new WithServer(FakeApplication(additionalConfiguration=Map("enableDBSSL" -> "false"))) {
      val response = await(WS.url(s"http://localhost:$port/products.html").withFollowRedirects(false).withHeaders("x-forwarded-proto" -> "http").get)
      response.status must beEqualTo(OK)
      response.header("Location") must beEqualTo(None)
    }*/
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
