package integration

import org.specs2.mutable.Specification
import play.api.test._
import play.api.libs.ws._
import play.api.Play
import play.api.cache.Cache
    
class IronCacheSpec extends PlaySpecification {
	
	"iron cache" should {
		"get cache" in new WithServer {
            Cache.remove("keyNotExists")
			Thread.sleep(1000)
            val item = Cache.get("keyNotExists")
            item must beNone
		}
        
        "put cache" in new WithServer {
            Cache.remove("key")
			Cache.set("key", "item")
            Thread.sleep(1000)
            val item = Cache.get("key")
            item must beEqualTo(Some("item"))
		}
    }
}