import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "simplemailer-test"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      // Add your project dependencies here,
      "valraiso" %% "simplemailer" % "1.0-SNAPSHOT"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(
               resolvers += "Local Play Repository" at "/Users/Benoit/Applications/play-2.0.2/framework/../repository/local/"
    )

}
