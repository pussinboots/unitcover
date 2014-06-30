package cache
    
import play.api.cache.Cache
import play.api.Play.current
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
    
object CacheProvider {

    def getOrSet(key: String, f: ()=> Future[String]): Future[String] = {
        val cachedItem:Option[String] = Cache.getAs[String](key)
        cachedItem match {
            case Some(value) => Future[String](value)
            case None =>
            val value = f()
            value.map{response=>
        		Cache.set(key, response)
            }
            value
        }
    }
}