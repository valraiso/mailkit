import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "simplemailer-test"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
       // Add your project dependencies here,
      
    )

    val izMailer = PlayProject("izMailer", "?", Seq(
    	"javax.activation" % "activation" % "1.1-rev-1",
    	"javax.mail" % "mailapi" % "1.4.3",
    	"com.sun.mail" % "smtp" % "1.4.4",
      	"org.jsoup" % "jsoup" % "1.6.3"
      ), mainLang = JAVA, path=file("modules/izmailer")).settings(
    	organization := "valraiso",
      	playPlugin := true 
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(
        
    ).dependsOn(izMailer).aggregate(izMailer)

}
