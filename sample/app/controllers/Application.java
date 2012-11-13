package controllers;

import mailkit.Email;
import play.mvc.Controller;
import play.mvc.Result;

public class Application extends Controller {
  
  public static Result index() throws Exception {
	  
	  Email email = new Email();
	  email.from ( "benoit.glevarec@from.fr" );
	  email.to ( "to@to.fr" );
	  email.subject ( "simplemail" );
	  email.setEmbedImages ( true );
	  email.attach ( "http://www.playframework.org/assets/images/logo.png" );
	  email.html ( "<body><p>Simple test <img src=\"/assets/images/favicon.png\"/></p></body>" ).send ();
	  
    return ok("It works!");
  }
}