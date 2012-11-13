import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "mailkit-sample"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
       // Add your project dependencies here,
       
    )

    val mailkit = PlayProject("mailkit", "?", Seq(
       
    	  "javax.activation" % "activation" % "1.1-rev-1",
    	  "javax.mail" % "mailapi" % "1.4.3",
    	  "com.sun.mail" % "smtp" % "1.4.4",
      	  "org.jsoup" % "jsoup" % "1.7.1"
      ), path=file("modules/mailkit")).settings(
    	  organization := "valraiso"
    )

    val main = PlayProject(appName, appVersion, appDependencies).settings(
        
    ).dependsOn(mailkit).aggregate(mailkit)

}
