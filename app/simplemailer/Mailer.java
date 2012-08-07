package simplemailer;

import org.codemonkey.simplejavamail.Email;
import org.codemonkey.simplejavamail.MailException;
import org.codemonkey.simplejavamail.TransportStrategy;

import play.Configuration;
import play.Play;
import play.libs.F.Tuple;

public class Mailer {

	private static Mailer instance;
	
	private boolean mockMode = false;
	private Tuple<String, String>  defaultSender;
	
	private org.codemonkey.simplejavamail.Mailer mailer;
	
	private Mailer(){
		
		Configuration config = Play.application().configuration();

		String senderEmail = config.getString("mail.sender.email");
		if (senderEmail != null){
			String senderName = config.getString("mail.sender.name");
			defaultSender = new Tuple<String, String>(senderName, senderEmail); 
		}
		
		Boolean mock = config.getBoolean("mail.mock");
		if (mock) {
			mockMode = true;
		}
		
		String  host = config.getString("mail.smtp.host");
		Integer port = config.getInt   ("mail.smtp.port");
		String  user = config.getString("mail.smtp.user");
		String  pass = config.getString("mail.smtp.pass");

		String  channel = config.getString("mail.smtp.channel");

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
	
	public static Mailer instance(){
		
		if (instance == null){
			instance = new Mailer();
		}
		
		return instance;
	}
	
	// wrapper methods
	
	public void sendMail(Email email) throws MailException {
		
		if (email.getFromRecipient() == null && defaultSender != null){
			email.setFromAddress(defaultSender._1, defaultSender._2);
		}
		
		if (mockMode){
			toString(email);
		} else {
			mailer.sendMail(email);
		}
	}
	
	public boolean validate(Email email) throws MailException {
		
		return mailer.validate(email);
	}
	
	/**
	 *
	 *  -> Send mail as mock
	 *	-> from : 
	 *	-> to   :
	 *	-> cc	:
	 *	-> bcc  :
	 *	
	 *	-> attachments :
	 *	
	 *	-> subject : 
	 *	
	 *	-> text :
	 *	
	 *	-> html :
	 *
	 * @param email
	 */
	public void toString(Email email){
		
		String newline = "\n";
		StringBuilder sb = new StringBuilder();
		
		sb.append("-> Send mail as mock");
		sb.append(newline);
		
		if (email.getFromRecipient() != null){
			
			String name = email.getFromRecipient().getName();
			String address = email.getFromRecipient().getAddress();
			
			sb.append("-> from: ");
			if (name != null){
				sb.append( name + " <" + address + ">");
			} else {
				sb.append(address);
			}
			sb.append(newline);
		}
	}
}