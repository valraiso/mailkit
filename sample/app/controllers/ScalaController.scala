package controllers

import play.api.mvc._
import mailkit.Email

object ScalaController extends Controller {

  def index = Action {implicit request =>
  	  val email = new Email ();
	  email.from ( "from@from.fr" );
	  email.to ( "to@to.fr" );
	  email.subject ( "simplemail" );
	  email.setEmbedImages ( true, "http://" + request.host );
	  email.html ( "<body><p>Simple test <img src=\"/assets/images/favicon.png\"/></p></body>" ).send ();
    Ok("It works!")
  }
    
}