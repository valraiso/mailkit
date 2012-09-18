package izmailer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.FileDataSource;
import javax.mail.Message.RecipientType;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringEscapeUtils;
import org.codemonkey.simplejavamail.Recipient;
import org.codemonkey.simplejavamail.Email;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import play.api.libs.MimeTypes;
import play.api.mvc.Request;
import play.api.templates.Html;
import play.api.templates.Txt;
import play.mvc.Call;
import play.mvc.Http;
import scala.Option;

public class IzMail {

	private static final String newline = System.getProperty("line.separator");
	private static final Pattern imgTag = Pattern.compile("(?i)(<img.*?)src=\"(.*?)\"(.*?>)");

	/**
	 * org.codemonkey.simplejavamailEmail
	 * Email message with all necessary data for an effective mailing action, including attachments etc.
	 */
	public Email email;
	
	/**
	 * Is this email will interpret and automatically inline CSS to html email text
	 */
	private boolean interpretCss = true;
	
	/**
	 * Is this email will embed external images 
	 */
	private boolean embedImages  = false;
	
	/**
	 * Current host
	 */
	private String host;
	
	/**
	 * The priority of Email. For HIGH and LOW priority, a X-Priority header will be added.
	 */
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
	
	/**
	 * Instantiate new email object
	 */
	public IzMail() {
		email = new Email();
	}

	/**
	 * Bean setter for {@link #interpretCss}
	 * @param boolean
	 * @return this
	 */
	public IzMail setInterpretCss(boolean interpretCss){
		this.interpretCss = interpretCss;
		return this;
	}
	
	/**
	 * Bean setter for {@link #embedImages}.use this method in Java
	 * @param boolean
	 * @return this
	 */
	public IzMail setEmbedImages(boolean embedImages){
		this.embedImages = embedImages;
		return this;
	}
	
	/**
	 * Bean setter for {@link #embedImages}.use this method in Scala
	 * @param boolean
	 * @param host : full host : example https://google.fr
	 * @return this
	 */
	public IzMail setEmbedImages(boolean embedImages, String host){
		this.embedImages = embedImages;
		this.host = host;
		return this;
	}
	
	
	/**
	 * Bean setter for {@link #priority}.
	 * @param EmailPriority
	 * @return this
	 */
	public IzMail setPriority(EmailPriority priority){
		if (priority != null){
			this.priority = priority;
		}
		return this;
	}
	
	/**
	 * Set the sender of the email
	 * @param : The sender email address.
	 * @return this
	 */
	public IzMail from(String address){
		return from(address, "");
	}
	
	/** 
	 * Set the sender of the email
	 * @param name: The sender name .
	 * @param address: The sender email address .
	 * @return this
	 */
	public IzMail from(String address, String name){
		email.setFromAddress(name, address);
		return this;
	}
	
	/**
	 * Set the replyTo of the email
	 * @param : The replyTo email address.
	 * @return this
	 */
	public IzMail replyTo(String address){
		return replyTo(address, "");
	}
	
	/** 
	 * Set the replyTo of the email
	 * @param name: The replyTo name .
	 * @param address: The replyTo email address .
	 * @return this
	 */
	public IzMail replyTo(String address, String name){
		email.setReplyToAddress(name, address);
		return this;
	}
	
	/**
	 * Add array of to recipient
	 * @param String[]
	 * @return this 
	 * */
	public IzMail tos(String... addresses){
		
		if (addresses != null){
			for (String address : addresses){
				to(address, "");
			}
		}
		return this;
	}
	
	/**
	 * Add a to recipient
	 * @param name: The recipient name .
	 * @param address: The recipient email address .
	 * @return this
	 */
	public IzMail to(String address, String name){
		email.addRecipient(name, address, RecipientType.TO);
		return this;
	}
	/**
	 * Add a to recipient
	 * @param name: The recipient name .
	 * @param address: The recipient email address .
	 * @return this
	 */
	public IzMail to(String address){
		return to ( address , "" );
	}
	
	/**
	 * Add array of cc recipient
	 * @param String[]
	 * @return this 
	 * */
	public IzMail ccs(String... addresses){
		
		if (addresses != null){
			for (String address : addresses){
				cc(address, "");
			}
		}

		return this;
	}
	
	/**
	 * Add a cc recipient
	 * @param name: The recipient name .
	 * @param address: The recipient email address .
	 * @return this
	 */
	public IzMail cc(String address){
		return cc ( address , "");
	}
	
	/**
	 * Add a cc recipient
	 * @param name: The recipient name .
	 * @param address: The recipient email address .
	 * @return this
	 */
	public IzMail cc(String address, String name){
		email.addRecipient(name, address, RecipientType.CC);
		return this;
	}
	
	/**
	 * Add array of bcc recipient
	 * @param String[]
	 * @return this 
	 * */
	public IzMail bccs(String... addresses){
		
		if (addresses != null){
			for (String address : addresses){
				bcc(address, "");
			}
		}
		
		return this;
	}
	
	/**
	 * Add a bcc recipient
	 * @param name: The recipient name .
	 * @param address: The recipient email address .
	 * @return this
	 */
	public IzMail bcc(String address){
		return bcc( address , "");
	}
	/**
	 * Add a bcc recipient
	 * @param name: The recipient name .
	 * @param address: The recipient email address .
	 * @return this
	 */
	public IzMail bcc(String address, String name){
		email.addRecipient(name, address, RecipientType.BCC);
		return this;
	}
	
	/**
	 * Add an attachment
	 * @param File
	 */
	public IzMail attach(File attachment){
		if (attachment != null){
			return attach( attachment , attachment.getName () );
		}
		return this;
	}
	
	/**
	 * Add an attachment
	 * @param File
	 * @param String filename
	 */
	public IzMail attach( File attachment , String fileName){
		
		if ( attachment != null ){
			FileDataSource datasource = new FileDataSource ( attachment );
			email.addAttachment ( fileName , datasource );
		}
		
		return this;
	}
	
	/**
	 * Add an attachment
	 * @param String url
	 */
	public IzMail attach (String url){
		if ( url != null ){
			try {
				String filename = url.lastIndexOf ( "/" ) > 0 ? url.substring ( url.lastIndexOf ( "/" ) +1 ) : url;
				URL u = new URL ( url );
				return attach( u.openStream () , filename );
			}
			catch( IOException e ) {
				e.printStackTrace();
			} 
		}
		return this;
	}
	
	/**
	 * Add an attachment
	 * @param is
	 * @param attachmentName
	 * @return
	 */
	public IzMail attach ( InputStream is , String attachmentName ){
		if (is != null){
			try {
				ByteArrayDataSource bads = NamedByteArrayDataSource.get ( is, attachmentName );
				email.addAttachment ( attachmentName, bads);
			}
			catch( IOException e ) {
				e.printStackTrace();
			}
		}
		return this;
		
	}
	
	/**
	 * Set the subject
	 * @param String
	 * @return this
	 */
	public IzMail subject(String subject){
		email.setSubject(subject);
		return this;
	}
	
	/**
	 * Set the text
	 * @param String
	 * @return this
	 */
	public IzMail text(String text){
		email.setText(text);
		return this;
	}
	
	/**
	 * Set the text
	 * @param Txt
	 * @return this
	 */
	public IzMail text(Txt template){
		return text(template.body());
	}
	
	/**
	 * Set the html
	 * @param String
	 * @return this
	 */
	public IzMail html(String html){
		if (interpretCss){
			html = inlineCss(html);
		}
		if (embedImages){
			html = embedImages(html);
		}
		
		
		email.setTextHTML(html);
		return this;
	}
	
	/**
	 * Set the html
	 * @param Html
	 * @return this
	 */
	public IzMail html(Html template){
		
		return html ( template.body() );
	}
	
	/**
	 * Set at once Html and text :
	 * The Html will be cleaned and inject into email text
	 * @param String
	 * @return this
	 */
	public IzMail textAndHtml(String html){
		
		html(html);
		
		String text = html.replaceAll("<\\s*/?br\\s*>", "\n") // replace all <br/> by newline 
						  .replaceAll("<.*?>", ""); // strip all html tags
		text(StringEscapeUtils.unescapeHtml4(text));
		return this;
	}
	
	/**
	 * Set at once Html and text :
	 * The Html will be cleaned and inject into email text
	 * @param Html
	 * @return this
	 */
	public IzMail textAndHtml(Html template){
		return textAndHtml(template.body());
	}
	
	/**
	 * Send this email
	 */
	public void send(){
		if (!EmailPriority.NORMAL.equals(this.priority)){
			email.addHeader("X-Priority", this.priority.val());
		}
		
		Mailer.instance().sendMail(	this );
	}
	
	
	/**
	 * Inline css in html part, for example, it will transform this: 
	 *	<html>
	 *		<body>
	 *			<style>
	 *				body{background:#FFC}
	 *				p{background:red}
	 *				body, p{font-weight:bold}
	 *			</style>
	 *			<p>...</p>
	 *		</body>
	 *	</html>
	 *
	 * Into this =>
	 * 
	 *	<html>
	 *		<body style="background:red;font-weight:bold">
	 *			<p style="background:#FFC;font-weight:bold">...</p>
	 *		</body>
	 *	</html>
	 *
	 *
	 * @param String
	 * @return String
	 */
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
	
	/**
	 * Util method for concatenate CSS properties for inline CSS
	 * @param oldProp
	 * @param newProp
	 * @return String
	 */
	private static String concatenateProperties( String oldProp, String newProp ) {
        oldProp = oldProp.trim();
        if ( !newProp.endsWith( ";" ) )
                newProp += ";";
        return newProp + oldProp; // The existing (old) properties should take precedence.
	}
	
	/**
	 * Embed images 
	 * @param html
	 * @return replaced html
	 */
	private String embedImages(String html){
		
		StringBuffer result = new StringBuffer ();
		Matcher imagesMatch = imgTag.matcher(html);
		List<String> cids = new ArrayList<String> ();
		
		while (imagesMatch.find()){
			String src = imagesMatch.group(2);
			if (!src.startsWith ( "http" ) ){
				if (host != null){
					src = host + src;
				}
				else {
					src = "http://"+ Http.Context.current ().request ().host () + src;
				}
			}
			
			String replacement = "";
			src = src.replaceAll(" ", "%20");
			
			String cid = cidHash ( src );
			if ( cids.contains ( cid )){
				replacement = imagesMatch.group(1) + "src=\"cid:" + cid + "\"" + imagesMatch.group(3);
				imagesMatch.appendReplacement( result , replacement);
			}
			else {
				try{
					embedOne ( src, cid );
					replacement = imagesMatch.group(1) + "src=\"cid:" + cid + "\"" + imagesMatch.group(3);
					imagesMatch.appendReplacement( result , replacement);
					cids.add ( cid );
				}
				catch (IOException e) {
					System.err.printf ("Failed while reading bytes from %s: %s", src, e.getMessage());
					e.printStackTrace ();
				}
			}
		}
		imagesMatch.appendTail(result);
		return result.toString ();
	}
	
	/**
	 * Embed one image as cid
	 * @param src
	 * @param cid
	 * @throws Exception 
	 */
	private void embedOne(String src, String cid) throws IOException{
		InputStream is = null;
		URL url = null;
		String filename = src.lastIndexOf ( "/" ) > 0 ? src.substring (  src.lastIndexOf ( "/" ) +1 ) : src;
		try	{
			url = new URL(src);
			is = url.openStream ();
			email.addEmbeddedImage ( cid , NamedByteArrayDataSource.get ( is,  filename ) );
		}
		finally {
			if (is != null) { 
				try {
					is.close();
				}
				catch( IOException e ) {
					e.printStackTrace();
				}
			}
		}
		
	}
	


	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("Send mail as mock");
		sb.append(newline).append(newline);
		
		if (email.getFromRecipient() != null){
			sb.append("[from]    ").append ( email.getFromRecipient().toString ()  );
			sb.append(newline);
		}
		if (email.getReplyToRecipient () != null){
			sb.append("[ReplyTo] ").append ( email.getReplyToRecipient ().toString () );
			sb.append(newline);
		}
		if (email.getRecipients () != null){
			List<Recipient> tos 	= new ArrayList<Recipient> ();
			List<Recipient> ccs 	= new ArrayList<Recipient> ();
			List<Recipient> bccs 	= new ArrayList<Recipient> ();
				
			for (Recipient recipient : email.getRecipients ()){
				if ( RecipientType.TO.equals ( recipient.getType () ) ){
					tos.add ( recipient );
				}
				else if ( RecipientType.CC.equals ( recipient.getType () ) ){
					ccs.add ( recipient );
				}
				else if ( RecipientType.BCC.equals ( recipient.getType () ) ){
					bccs.add ( recipient );
				}
			}
			if ( ! tos.isEmpty () ){
				sb.append ( "[to]      " ).append( IzMail.join ( tos , " , " ) ).append ( newline );
			}
			if ( ! ccs.isEmpty () ){
				sb.append ( "[cc]      " ).append(  IzMail.join ( ccs, " , " ) ).append ( newline );
			}
			if ( ! bccs.isEmpty () ){
				sb.append ( "[bcc]     " ).append(  IzMail.join ( bccs, " , " ) ).append ( newline );
			}
			sb.append ( newline );
		}
		
		if ( email.getSubject () != null && ! email.getSubject ().isEmpty () ){
			sb.append("[subject] ").append ( email.getSubject () ).append ( newline ).append ( newline );
		}
			
		if ( email.getAttachments () != null && !email.getAttachments ().isEmpty ()){
			sb.append ( "[Attachement] " ).append(  IzMail.join ( email.getAttachments (), " , " ) ).append ( newline );
		}
		
		if ( email.getEmbeddedImages () != null && ! email.getEmbeddedImages ().isEmpty () ) {
			sb.append ( "[Embedded images] " ).append(  IzMail.join ( email.getEmbeddedImages (), " , " ) ).append ( newline );
		} 
		
		sb.append(newline);
		
		if ( email.getText () != null && ! email.getText ().isEmpty () ){
			sb.append("[Text] ").append ( email.getText () ).append ( newline ).append ( newline );
		}
		if ( email.getTextHTML () != null && ! email.getTextHTML ().isEmpty () ){
			sb.append("[Html] ").append ( email.getTextHTML () ).append ( newline ).append ( newline );
		}
		return sb.toString ();
	}
	
	/**
	 * Concatenate items of a collection as a string separated with <tt>separator</tt>
	 *  items toString() method should be implemented to provide a string representation
	 */
	public static <T extends Object> String join( Collection<T> items, String separator ) {
		if ( items == null ) {
			return "";
		}
		StringBuffer sb = new StringBuffer ();
		Iterator<T> ite = items.iterator ();
		int i = 0;
		while ( ite.hasNext () ) {
			if ( i++ > 0 ) {
				sb.append ( separator );
			}
			sb.append ( ite.next () );
		}
		return sb.toString ();
	}
	
	/**
	 * Hash f
	 * @param input
	 * @return
	 */
	private static String cidHash( String input ) {
		try {
			MessageDigest m = MessageDigest.getInstance ( "MD5" );
			byte[] out = m.digest ( input.getBytes () );
			return new String ( Base64.encodeBase64 ( out ) );
		}
		catch( NoSuchAlgorithmException e ) {
			throw new RuntimeException ( e );
		}
	}
	
	/**
	 * Wrapper of ByteArrayDataSource in order to set DataSource name 
	 * @author Benoit
	 *
	 */
	private static class NamedByteArrayDataSource extends ByteArrayDataSource {
		
		public static ByteArrayDataSource get( InputStream is,String name ) throws IOException{
			
			Option<String> opt = MimeTypes.forFileName ( name );
			String type = opt.get ();
			return new NamedByteArrayDataSource ( is, type, name );
		}
    	
		private NamedByteArrayDataSource( InputStream is, String type, String name ) throws IOException {
			super ( is, type );
			this.setName ( name );
		}
    }

}
