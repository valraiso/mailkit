package forms;

import java.util.List;

import play.data.Form;
import play.data.validation.Constraints.*;

public class EmailForm {

	public final static Form<EmailForm> defaultForm =  new Form<EmailForm>(EmailForm.class);
	
	public EmailForm.Email from;
	
	public List<EmailForm.Email> tos;
	
	
	public static class Email {
		@play.data.validation.Constraints.Email
		@Required
		public String mail;
		
		public Email(String mail){
			this.mail = mail;
		}
	}
}
