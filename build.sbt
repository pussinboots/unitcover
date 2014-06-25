import sbt._
import sbt.Keys._
import play.Play.autoImport._
import PlayKeys._
import java.io.File
import scala.sys.process._

instrumentSettings

CoverallsPlugin.coverallsSettings

name := "UnitCover"

version := "0.1"

scalaVersion := "2.10.4"

parallelExecution in Global := true //disable parallel execution for all tasks the below configuration could be deleted but for documentation purpose they are still there

parallelExecution in Test := true

parallelExecution in ScoverageTest := true

Keys.fork in Test := true

val logger = ProcessLogger(
    (o: String) => println("out " + o),
    (e: String) => println("err " + e))

lazy val npm = taskKey[Unit]("npm install")

npm := scala.sys.process.Process( "npm" :: "install" :: Nil) ! logger

//(compile in Compile) <<= (compile in Compile) dependsOn (npm)

ScoverageKeys.excludedPackages in ScoverageCompile := "controllers.javascript;controllers.ref;tools.imports;Routes;controllers.ReverseAssets;controllers.ReverseApplication;controllers.ReverseBuildController;controllers.ReverseTestCaseController;controllers.ReverseTestSuiteController;controllers.ReverseBadgeController;scala.slick.migration.api..*"

envVars := Map("aes_key" -> "16rdKQfqN3L4TY7YktgxBw==", "sparkasse_username"->"username", "sparkasse_password"->"password") // setted for EasyCryptSpec

javaOptions ++= Seq("-Xmx2G", "-Xms1G", "-XX:MaxPermSize=256M", "-XX:+CMSClassUnloadingEnabled"/*, "-verbose:gc", "-XX:+PrintGCDetails", "-XX:+PrintGCTimeStamps"*/, "-XX:+UseConcMarkSweepGC", "-XX:ReservedCodeCacheSize=128M")

lazy val stickMigrationApi = RootProject(uri("git://github.com/nafg/slick-migration-api.git"))

lazy val root = (project in file(".")).enablePlugins(PlayScala).dependsOn(stickMigrationApi)

libraryDependencies ++= Seq(
    "net.databinder.dispatch" %% "dispatch-core" % "0.10.0"
)

libraryDependencies += ws

//embedded jetty dependencies
libraryDependencies ++= Seq(
  "org.eclipse.jetty.orbit" % "javax.servlet" % "2.5.0.v201103041518" % "test" artifacts Artifact("javax.servlet", "jar", "jar"),
  "org.eclipse.jetty.orbit" % "javax.security.auth.message" % "1.0.0.v201108011116" % "test" artifacts Artifact("javax.security.auth.message", "jar", "jar"),
  "org.eclipse.jetty.orbit" % "javax.mail.glassfish" % "1.4.1.v201005082020" % "test" artifacts Artifact("javax.mail.glassfish", "jar", "jar"),
  "org.eclipse.jetty.orbit" % "javax.activation" % "1.1.0.v201105071233" % "test" artifacts Artifact("javax.activation", "jar", "jar"),
  "org.eclipse.jetty.orbit" % "javax.annotation" % "1.1.0.v201108011116" % "test" artifacts Artifact("javax.annotation", "jar", "jar"),
  "org.eclipse.jetty.aggregate" % "jetty-all-server" % "7.6.3.v20120416" % "test"
)

// test dependencies
libraryDependencies ++= Seq(
    "org.hsqldb" %  "hsqldb" % "[2,)",
    "co.freeside" % "betamax" % "1.1.2" % "test",
    "org.codehaus.groovy" % "groovy-all" % "1.8.8" % "test"
)

libraryDependencies ++= Seq(
    "com.typesafe.slick" %% "slick" % "2.0.2",
    "mysql" % "mysql-connector-java" % "5.1.18",
    "c3p0" % "c3p0" % "0.9.1.2"
)

//needed for database migration scala.slick.migration.api
libraryDependencies ++= Seq(
  "org.xerial"          % "sqlite-jdbc"          % "3.7.2"         % "test",
  "org.apache.derby"    % "derby"                % "10.10.1.1"     % "test",
  "org.hsqldb"          % "hsqldb"               % "2.2.8"         % "test",
  "postgresql"          % "postgresql"           % "9.1-901.jdbc4" % "test",
  "com.h2database"      % "h2"                   % "1.3.170"          
)
