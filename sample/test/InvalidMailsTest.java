import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

import java.util.HashMap;
import java.util.Map;

import org.codemonkey.simplejavamail.MailException;
import org.junit.Test;

import play.test.FakeApplication;

import simplemailer.Email;

public class InvalidMailsTest {

		public static FakeApplication fakeApplication;
		static{
			Map<String,String> conf = new HashMap<String, String>();
			conf.put ( "mail.smtp.mock" , "true" );
			fakeApplication = fakeApplication ( conf );
		}
	
	
		@Test(expected=MailException.class)
		public void invalidFrom() {
			running ( fakeApplication , new Runnable(){
				@Override
				public void run() {
					Email 	mail 	= new Email ();
					mail.from ( "INVALID FROM" , "from" );
					mail.replyTo ( "replyto@replyto.fr" , "replyTo");
					mail.to ( "to1@to1.fr", "to1").to ( "to2@to2.fr", "to2" );
				  	mail.cc ( "cc1@cc1.fr", "cc1").cc ( "cc2@cc2.fr", "cc2" );
				  	mail.bcc ( "bcc1@bcc1.fr", "bcc1").bcc ( "bcc2@bcc2.fr", "bcc2" );
					mail.subject ( "A subject" );
					mail.text ( "Text content" );
					mail.html ( "Html content" );
					mail.send ();
				}
			} );
	    }
		@Test(expected=MailException.class)
		public void invalidReplyTo() {
			running ( fakeApplication , new Runnable(){
				@Override
				public void run() {
					 Email 	mail 	= new Email ();
					  mail.from ( "from@from.fr" , "from" );
					  mail.replyTo ( "INVALID REPLYTO" , "replyTo");
					  mail.to ( "to1@to1.fr", "to1").to ( "to2@to2.fr", "to2" );
				  	  mail.cc ( "cc1@cc1.fr", "cc1").cc ( "cc2@cc2.fr", "cc2" );
				  	  mail.bcc ( "bcc1@bcc1.fr", "bcc1").bcc ( "bcc2@bcc2.fr", "bcc2" );
					  mail.subject ( "A subject" );
					  mail.text ( "Text content" );
					  mail.html ( "Html content" );
					  mail.send ();
				}
			} );
		}
		
		@Test(expected=MailException.class)
		public void invalidTo() {
			running ( fakeApplication , new Runnable(){
				@Override
				public void run() {
					Email 	mail 	= new Email ();
					mail.from ( "from@from.fr" , "from" );
					mail.replyTo ( "replyto@replyto.fr" , "replyTo");
					mail.to ( "INVALID TO", "to1").to ( "to2@to2.fr", "to2" );
					mail.cc ( "cc1@cc1.fr", "cc1").cc ( "cc2@cc2.fr", "cc2" );
					mail.bcc ( "bcc1@bcc1.fr", "bcc1").bcc ( "bcc2@bcc2.fr", "bcc2" );
					mail.subject ( "A subject" );
					mail.text ( "Text content" );
					mail.html ( "Html content" );
					mail.send ();
				}
			} );
		}
		
		@Test(expected=MailException.class)
		public void invalidCc() {
			running ( fakeApplication , new Runnable(){
				@Override
				public void run() {
					Email 	mail 	= new Email ();
					mail.from ( "from@from.fr" , "from" );
					mail.replyTo ( "replyto@replyto.fr" , "replyTo");
					mail.to ( "to1@to1.fr", "to1").to ( "to2@to2.fr", "to2" );
					mail.cc ( "INVALID CC", "cc1").cc ( "cc2@cc2.fr", "cc2" );
					mail.bcc ( "bcc1@bcc1.fr", "bcc1").bcc ( "bcc2@bcc2.fr", "bcc2" );
					mail.subject ( "A subject" );
					mail.text ( "Text content" );
					mail.html ( "Html content" );
					mail.send ();
				}
			} );
		}
		@Test(expected=MailException.class)
		public void invalidBcc() {
			running ( fakeApplication , new Runnable(){
				@Override
				public void run() {
					Email 	mail 	= new Email ();
					mail.from ( "from@from.fr" , "from" );
					mail.replyTo ( "replyto@replyto.fr" , "replyTo");
					mail.to ( "to1@to1.fr", "to1").to ( "to2@to2.fr", "to2" );
					mail.cc ( "cc1@cc1.fr", "cc1").cc ( "cc2@cc2.fr", "cc2" );
					mail.bcc ( "INVALID BCC", "bcc1").bcc ( "bcc2@bcc2.fr", "bcc2" );
					mail.subject ( "A subject" );
					mail.text ( "Text content" );
					mail.html ( "Html content" );
					mail.send ();
				}
			} );
		}
		@Test(expected=MailException.class)
		public void noRecipients() {
			running ( fakeApplication , new Runnable(){
				@Override
				public void run() {
					Email 	mail 	= new Email ();
					mail.from ( "from@from.fr" , "from" );
					mail.replyTo ( "replyto@replyto.fr" , "replyTo");
					mail.subject ( "A subject" );
					mail.text ( "Text content" );
					mail.html ( "Html content" );
					mail.send ();
				}
			} );
		}
		
		@Test(expected=MailException.class)
		public void noFrom() {
			running ( fakeApplication , new Runnable(){
				@Override
				public void run() {
					Email 	mail 	= new Email ();
					mail.replyTo ( "replyto@replyto.fr" , "replyTo");
					mail.to ( "to1@to1.fr", "to1").to ( "to2@to2.fr", "to2" );
					mail.cc ( "cc1@cc1.fr", "cc1").cc ( "cc2@cc2.fr", "cc2" );
					mail.bcc ( "bcc1@bcc1.fr", "bcc1").bcc ( "bcc2@bcc2.fr", "bcc2" );
					mail.subject ( "A subject" );
					mail.text ( "Text content" );
					mail.html ( "Html content" );
					mail.send ();
				}
			} );
		}


}
