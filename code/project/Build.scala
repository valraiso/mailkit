import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

    val appName         = "mailkit"
    val appVersion      = "1.0"

    val appDependencies = Seq(
          javaCore,
    	  "javax.activation" % "activation" % "1.1-rev-1",
    	  "javax.mail" % "mailapi" % "1.4.3",
    	  "com.sun.mail" % "smtp" % "1.4.4",
          "org.jsoup" % "jsoup" % "1.7.1"
    )

    val valraisoReleases   = Resolver.file("file", new File("../../valraiso.github.com/releases/"))
    val valraisoSnapshots  = Resolver.file("file", new File("../../valraiso.github.com/snapshot/"))
    val valraisoRepository = if(appVersion.endsWith("SNAPSHOT")) valraisoSnapshots else valraisoReleases


    val main = play.Project(appName, appVersion, appDependencies).settings(
        organization := "valraiso",
        publishMavenStyle := true,
        playPlugin:=true,
        publishTo := Some(valraisoRepository)
    )

}
