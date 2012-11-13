import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "mailkit"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
    	  "javax.activation" % "activation" % "1.1-rev-1",
    	  "javax.mail" % "mailapi" % "1.4.3",
    	  "com.sun.mail" % "smtp" % "1.4.4",
          "org.jsoup" % "jsoup" % "1.7.1"
    )

    val valraisoReleases   = Resolver.file("file", new File(Path.userHome.absolutePath+"/dev/apps/valraiso.github.com/releases/"))
    val valraisoSnapshots  = Resolver.file("file", new File(Path.userHome.absolutePath+"/dev/apps/valraiso.github.com/snapshot/"))
    val valraisoRepository = if(appVersion.endsWith("SNAPSHOT")) valraisoSnapshots else valraisoReleases


    val main = PlayProject(appName, appVersion, appDependencies).settings(
        organization := "valraiso",
        publishMavenStyle := true,
        publishTo := Some(valraisoRepository)
    )

}
