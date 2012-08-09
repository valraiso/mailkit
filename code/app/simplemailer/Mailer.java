package simplemailer;

import org.codemonkey.simplejavamail.MailException;
import org.codemonkey.simplejavamail.TransportStrategy;

import play.Configuration;
import play.Logger;
import play.Play;
import play.libs.F.Tuple;

public class Mailer {
	
	public static final String newline = System.getProperty("line.separator");
	
	private static Mailer instance;

	private boolean mockMode = false;
	
	private Tuple<String, String>  defaultSender;
	
	private org.codemonkey.simplejavamail.Mailer mailer;
	
	/**
	 * Instantiate a new Mailer with following configuration properties :
	 * 		mail.smtp.mock		-> Is this mailer will send real mail or just log it
	 *		mail.smtp.host		-> SMTP server mail host, for example mail.google.com
	 * 		mail.smtp.port		-> SMTP server port, default : 25
	 *		mail.smtp.user		-> SMTP user email, optional
	 *		mail.smtp.pass		-> SMTP user password, optional
	 *		mail.smtp.channel	-> SMTP transport strategy, values are ssl,tls,plain. default :plain 
	 */
	private Mailer(){
		
		Configuration config = Play.application().configuration();

		String senderEmail = config.getString("mail.sender.email");
		if (senderEmail != null){
			String senderName = config.getString("mail.sender.name");
			defaultSender = new Tuple<String, String>(senderName, senderEmail); 
		}
		
		Boolean mock = config.getBoolean("mail.smtp.mock");
		if ( mock!= null && mock ) {
			mockMode = true;
		}
		
		String  host = mockMode ? "localhost" : config.getString("mail.smtp.host");
		Integer port = mockMode ? 993 :  ( config.getInt   ("mail.smtp.port") == null ? 25 : config.getInt   ("mail.smtp.port") );
		String  user = mockMode ? "mock@mock.com" : config.getString("mail.smtp.user");
		String  pass = mockMode ? "mock" : config.getString("mail.smtp.pass");

		String  channel =  mockMode ? "ssl" : config.getString("mail.smtp.channel");

		TransportStrategy transportStrategy;

		if ("ssl".equals(channel)){
			transportStrategy = TransportStrategy.SMTP_SSL;
		} else if ("tls".equals(channel)){
			transportStrategy = TransportStrategy.SMTP_TLS;
		} else {
			transportStrategy = TransportStrategy.SMTP_PLAIN;
		}
		
		mailer = new org.codemonkey.simplejavamail.Mailer(host, port, user, pass, transportStrategy);
	}
	
	/**
	 * Singleton access
	 * @return Mailer
	 */
	public static Mailer instance(){
		
		if (instance == null){
			instance = new Mailer();
		}
		
		return instance;
	}
	
	/**
	 * Send an email
	 * @param email
	 * @throws MailException
	 */
	public void sendMail(Email email) throws MailException {
		
		if (email.email.getFromRecipient() == null && defaultSender != null){
			email.email.setFromAddress(defaultSender._1, defaultSender._2);
		}
		if (mockMode && mailer.validate ( email.email )){
			Logger.info ( email.toString() );
		} else {
			mailer.sendMail(email.email);
		}
	}
	
	/**
	 * Validate an email, Validation fails if the subject is missing, content is missing, or no recipients are defined, or if a recipient address is malformed
	 * @param email
	 * @return
	 * @throws MailException
	 */
	public boolean validate(Email email) throws MailException {
		
		return mailer.validate(email.email);
	}
}