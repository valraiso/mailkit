import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "mailkit-sample"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
       // Add your project dependencies here,
       javaCore
    )

    val mailkit = play.Project("mailkit", "?", Seq(
          javaCore,
    	  "javax.activation" % "activation" % "1.1-rev-1",
    	  "javax.mail" % "mailapi" % "1.4.3",
    	  "com.sun.mail" % "smtp" % "1.4.4",
      	  "org.jsoup" % "jsoup" % "1.6.3"
      ), path=file("modules/mailkit")).settings(
    	  organization := "valraiso"
    )

    val main = play.Project(appName, appVersion, appDependencies).settings(
        playPlugin:=true
    ).dependsOn(mailkit).aggregate(mailkit)

}
