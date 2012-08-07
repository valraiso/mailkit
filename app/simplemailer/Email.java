package simplemailer;

import java.util.StringTokenizer;

import javax.mail.Message.RecipientType;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import play.api.templates.Html;
import play.api.templates.Txt;

public class Email {

	public org.codemonkey.simplejavamail.Email email;
	
	public boolean interpretCss = true;
	public boolean embedImages  = false;
	public EmailPriority priority = EmailPriority.NORMAL;
	
	public static enum EmailPriority{
		HIGH(1), NORMAL(2), LOW(3);
		private int code;
		private EmailPriority(int val){
			this.code = val;
		}
		public int val(){
			return this.code;
		}
	}
	
	public Email() {
		email = new org.codemonkey.simplejavamail.Email();
	}

	public Email setInterpretCss(boolean interpretCss){
		this.interpretCss = interpretCss;
		return this;
	}
	
	public Email setEmbedImages(boolean embedImages){
		this.embedImages = embedImages;
		return this;
	}
	
	public Email setPriority(EmailPriority priority){
		if (priority != null){
			this.priority = priority;
		}
		return this;
	}
	
	public org.codemonkey.simplejavamail.Email raw(){
		return email;
	}
	
	public Email from(String address){
		return from(address, "");
	}
	
	public Email from(String address, String name){
		email.setFromAddress(name, address);
		return this;
	}
	
	public Email replyTo(String address){
		return replyTo(address, "");
	}
	
	public Email replyTo(String address, String name){
		email.setReplyToAddress(name, name);
		return this;
	}
	
	public Email to(String... addresses){
		
		if (addresses != null){
			for (String address : addresses){
				to(address, "");
			}
		}
		return this;
	}
	
	public Email to(String address, String name){
		email.addRecipient(name, address, RecipientType.TO);
		return this;
	}
	
	public Email cc(String... addresses){
		
		if (addresses != null){
			for (String address : addresses){
				cc(address, "");
			}
		}

		return this;
	}
	
	public Email cc(String address, String name){
		email.addRecipient(name, address, RecipientType.CC);
		return this;
	}
	
	public Email bcc(String... addresses){
		
		if (addresses != null){
			for (String address : addresses){
				bcc(address, "");
			}
		}
		
		return this;
	}
	
	public Email bcc(String address, String name){
		email.addRecipient(name, address, RecipientType.BCC);
		return this;
	}
	
	public Email subject(String subject){
		email.setSubject(subject);
		return this;
	}
	
	public Email text(String text){
		email.setText(text);
		return this;
	}
	
	public Email text(Txt template){
		return text(template.body());
	}
	
	public Email html(String html){
		email.setTextHTML(html);
		return this;
	}
	
	public Email html(Html template){
		
		String text = template.body();
		
		if (interpretCss){
			text = inlineCss(text);
		}
		if (embedImages){
			text = embedImages(text);
		}
		
		email.setTextHTML(text);
		return this;
	}
	
	public Email textAndHtml(String html){
		
		html(html);
		
		String text = html.replaceAll("<\\s*/?br\\s*>", "\n") // replace all <br/> by newline 
						  .replaceAll("<.*?>", ""); // strip all html tags
		
		//text(StringEscapeUtils.unescapeHtml(text));
		
		return this;
	}
	
	public Email textAndHtml(Html template){
		return textAndHtml(template.body());
	}
	
	public void send(){
		
		if (!EmailPriority.NORMAL.equals(this.priority)){
			email.addHeader("X-Priority", this.priority.val());
		}
		
		Mailer.instance().sendMail(
			email
		);
	}
	
	private String inlineCss(String html){
		
		Document doc = Jsoup.parse(html);
		Elements els = doc.select("style");
		for (Element e : els) {
			String styleRules = e.getAllElements().get(0).data().replaceAll("\n", "").trim(), delims = "{}";
			StringTokenizer st = new StringTokenizer(styleRules, delims);
			while (st.countTokens() > 1) {
				String selector = st.nextToken(), properties = st.nextToken();
				properties = properties.trim();
				Elements selectedElements = doc.select(selector);
				for (Element selElem : selectedElements) {
					String oldProperties = selElem.attr("style");
					selElem.attr("style", oldProperties.length() > 0 ? concatenateProperties(oldProperties, properties) : properties);
				}
			}
			e.remove();
		}
		
		return doc.toString();
	}
	
	private String embedImages(String html){
		
		return html;
	}
	
	private static String concatenateProperties( String oldProp, String newProp ) {
        oldProp = oldProp.trim();
        if ( !newProp.endsWith( ";" ) )
                newProp += ";";
        return newProp + oldProp; // The existing (old) properties should take precedence.
	}
	
}
