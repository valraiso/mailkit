import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "simplemailer"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
    	"javax.activation" % "activation" % "1.1-rev-1",
    	"javax.mail" % "mailapi" % "1.4.3",
    	"com.sun.mail" % "smtp" % "1.4.4",
      	"org.jsoup" % "jsoup" % "1.6.3"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(
      organization := "valraiso"      
    )

}
