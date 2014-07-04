object SimplePlayRunHook {
  import java.net.InetSocketAddress
  import play.PlayRunHook
  import sbt._
  import sbt.Keys._
 
  def apply(): PlayRunHook = {
    new PlayRunHook {
      override def beforeStarted(): Unit = {
        val cmdNpm = Seq("sh", "-c", "npm install")

        (cmdNpm  #||  "echo npm is missing and needed for local development to fetch the nodejs and bower dependencies. For install look here https://github.com/npm/npm. Perform npm task manual from your command line. Ignore it during herku deployment npm install will be performed fro the nodejs buildpack." !)
        /*val cmdCoffee = Seq("./node_modules/coffee-script/bin/coffee", "-c", "-o", "public/js/bankapp/coffee", "public/js/bankapp/coffee")
        (cmdCoffee  #||  "echo npm is missing and needed for local development to fetch the nodejs and bower dependencies. For install look here https://github.com/npm/npm. Perform npm task manual from your command line. Ignore it during herku deployment npm install will be performed fro the nodejs buildpack." !)*/
       println(s"++++ compiled coffee script") 
      }
 
      override def afterStarted(address: InetSocketAddress): Unit = {
        println(s"++++ simplePlayRunHook.afterStarted: $address")
      }
 
      override def afterStopped(): Unit = {
        println("++++ simplePlayRunHook.afterStopped")
      }
    }
  }
}