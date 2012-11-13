package mailkit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
import org.apache.commons.lang.StringEscapeUtils;
import org.codemonkey.simplejavamail.Recipient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import play.api.libs.MimeTypes;
import play.api.templates.Html;
import play.api.templates.Txt;
import play.mvc.Http;
import scala.Option;

public class Email {

	private static final String newline = System.getProperty("line.separator");
	private static final Pattern imgTag = Pattern.compile("(?i)(<img.*?)src=\"(.*?)\"(.*?>)");

	/**
	 * org.codemonkey.simplejavamailEmail
	 * Email message with all necessary data for an effective mailing action, including attachments etc.
	 */
	public org.codemonkey.simplejavamail.Email simpleMail;
	
	/**
	 * Is this simpleMail will interpret and automatically inline CSS to html simpleMail text
	 */
	private boolean interpretCss = true;
	
	/**
	 * Is this simpleMail will embed external images
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
	 * Instantiate new simpleMail object
	 */
	public Email() {
		simpleMail = new org.codemonkey.simplejavamail.Email();
	}

	/**
	 * Bean setter for {@link #interpretCss}
	 * @param boolean
	 * @return this
	 */
	public Email setInterpretCss(boolean interpretCss){
		this.interpretCss = interpretCss;
		return this;
	}
	
	/**
	 * Bean setter for {@link #embedImages}.use this method in Java
	 * @param boolean
	 * @return this
	 */
	public Email setEmbedImages(boolean embedImages){
		this.embedImages = embedImages;
		return this;
	}
	
	/**
	 * Bean setter for {@link #embedImages}.use this method in Scala
	 * @param boolean
	 * @param host : full host : example https://google.fr
	 * @return this
	 */
	public Email setEmbedImages(boolean embedImages, String host){
		this.embedImages = embedImages;
		this.host = host;
		return this;
	}
	
	
	/**
	 * Bean setter for {@link #priority}.
	 * @param EmailPriority
	 * @return this
	 */
	public Email setPriority(EmailPriority priority){
		if (priority != null){
			this.priority = priority;
		}
		return this;
	}
	
	/**
	 * Set the sender of the simpleMail
	 * @param : The sender simpleMail address.
	 * @return this
	 */
	public Email from(String address){
		return from(address, "");
	}
	
	/** 
	 * Set the sender of the simpleMail
	 * @param name: The sender name .
	 * @param address: The sender simpleMail address .
	 * @return this
	 */
	public Email from(String address, String name){
		simpleMail.setFromAddress(name, address);
		return this;
	}
	
	/**
	 * Set the replyTo of the simpleMail
	 * @param : The replyTo simpleMail address.
	 * @return this
	 */
	public Email replyTo(String address){
		return replyTo(address, "");
	}
	
	/** 
	 * Set the replyTo of the simpleMail
	 * @param name: The replyTo name .
	 * @param address: The replyTo simpleMail address .
	 * @return this
	 */
	public Email replyTo(String address, String name){
		simpleMail.setReplyToAddress(name, address);
		return this;
	}
	
	/**
	 * Add array of to recipient
	 * @param String[]
	 * @return this 
	 * */
	public Email tos(String... addresses){
		
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
	 * @param address: The recipient simpleMail address .
	 * @return this
	 */
	public Email to(String address, String name){
		simpleMail.addRecipient(name, address, RecipientType.TO);
		return this;
	}
	/**
	 * Add a to recipient
	 * @param name: The recipient name .
	 * @param address: The recipient simpleMail address .
	 * @return this
	 */
	public Email to(String address){
		return to ( address , "" );
	}
	
	/**
	 * Add array of cc recipient
	 * @param String[]
	 * @return this 
	 * */
	public Email ccs(String... addresses){
		
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
	 * @param address: The recipient simpleMail address .
	 * @return this
	 */
	public Email cc(String address){
		return cc ( address , "");
	}
	
	/**
	 * Add a cc recipient
	 * @param name: The recipient name .
	 * @param address: The recipient simpleMail address .
	 * @return this
	 */
	public Email cc(String address, String name){
		simpleMail.addRecipient(name, address, RecipientType.CC);
		return this;
	}
	
	/**
	 * Add array of bcc recipient
	 * @param String[]
	 * @return this 
	 * */
	public Email bccs(String... addresses){
		
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
	 * @param address: The recipient simpleMail address .
	 * @return this
	 */
	public Email bcc(String address){
		return bcc( address , "");
	}
	/**
	 * Add a bcc recipient
	 * @param name: The recipient name .
	 * @param address: The recipient simpleMail address .
	 * @return this
	 */
	public Email bcc(String address, String name){
		simpleMail.addRecipient(name, address, RecipientType.BCC);
		return this;
	}
	
	/**
	 * Add an attachment
	 * @param File
	 */
	public Email attach(File attachment){
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
	public Email attach( File attachment , String fileName){
		
		if ( attachment != null ){
			FileDataSource datasource = new FileDataSource ( attachment );
			simpleMail.addAttachment(fileName, datasource);
		}
		
		return this;
	}
	
	/**
	 * Add an attachment
	 * @param String url
	 */
	public Email attach (String url){
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
	public Email attach ( InputStream is , String attachmentName ){
		if (is != null){
			try {
				ByteArrayDataSource bads = NamedByteArrayDataSource.get ( is, attachmentName );
				simpleMail.addAttachment(attachmentName, bads);
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
	public Email subject(String subject){
		simpleMail.setSubject(subject);
		return this;
	}
	
	/**
	 * Set the text
	 * @param String
	 * @return this
	 */
	public Email text(String text){
		simpleMail.setText(text);
		return this;
	}
	
	/**
	 * Set the text
	 * @param Txt
	 * @return this
	 */
	public Email text(Txt template){
		return text(template.body());
	}
	
	/**
	 * Set the html
	 * @param String
	 * @return this
	 */
	public Email html(String html){
		if (interpretCss){
			html = inlineCss(html);
		}
		if (embedImages){
			html = embedImages(html);
		}
		
		
		simpleMail.setTextHTML(html);
		return this;
	}
	
	/**
	 * Set the html
	 * @param Html
	 * @return this
	 */
	public Email html(Html template){
		
		return html ( template.body() );
	}
	
	/**
	 * Set at once Html and text :
	 * The Html will be cleaned and inject into simpleMail text
	 * @param String
	 * @return this
	 */
	public Email textAndHtml(String html){
		
		html(html);
		
		String text = html.replaceAll("<\\s*/?br\\s*>", "\n") // replace all <br/> by newline 
						  .replaceAll("<.*?>", ""); // strip all html tags
		text(StringEscapeUtils.unescapeHtml(text));
		return this;
	}
	
	/**
	 * Set at once Html and text :
	 * The Html will be cleaned and inject into simpleMail text
	 * @param Html
	 * @return this
	 */
	public Email textAndHtml(Html template){
		return textAndHtml(template.body());
	}
	
	/**
	 * Send this simpleMail
	 */
	public void send(){
		if (!EmailPriority.NORMAL.equals(this.priority)){
			simpleMail.addHeader("X-Priority", this.priority.val());
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
			simpleMail.addEmbeddedImage(cid, NamedByteArrayDataSource.get(is, filename));
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
		
		if (simpleMail.getFromRecipient() != null){
			sb.append("[from]    ").append ( simpleMail.getFromRecipient().toString ()  );
			sb.append(newline);
		}
		if (simpleMail.getReplyToRecipient () != null){
			sb.append("[ReplyTo] ").append ( simpleMail.getReplyToRecipient ().toString () );
			sb.append(newline);
		}
		if (simpleMail.getRecipients () != null){
			List<Recipient> tos 	= new ArrayList<Recipient> ();
			List<Recipient> ccs 	= new ArrayList<Recipient> ();
			List<Recipient> bccs 	= new ArrayList<Recipient> ();
				
			for (Recipient recipient : simpleMail.getRecipients ()){
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
				sb.append ( "[to]      " ).append( Email.join(tos, " , ") ).append(newline);
			}
			if ( ! ccs.isEmpty () ){
				sb.append ( "[cc]      " ).append(  Email.join(ccs, " , ") ).append(newline);
			}
			if ( ! bccs.isEmpty () ){
				sb.append ( "[bcc]     " ).append(  Email.join(bccs, " , ") ).append(newline);
			}
			sb.append ( newline );
		}
		
		if ( simpleMail.getSubject () != null && ! simpleMail.getSubject ().isEmpty () ){
			sb.append("[subject] ").append ( simpleMail.getSubject () ).append ( newline ).append ( newline );
		}
			
		if ( simpleMail.getAttachments () != null && !simpleMail.getAttachments ().isEmpty ()){
			sb.append ( "[Attachement] " ).append(  Email.join(simpleMail.getAttachments(), " , ") ).append(newline);
		}
		
		if ( simpleMail.getEmbeddedImages () != null && ! simpleMail.getEmbeddedImages ().isEmpty () ) {
			sb.append ( "[Embedded images] " ).append(  Email.join(simpleMail.getEmbeddedImages(), " , ") ).append(newline);
		} 
		
		sb.append(newline);
		
		if ( simpleMail.getText () != null && ! simpleMail.getText ().isEmpty () ){
			sb.append("[Text] ").append ( simpleMail.getText () ).append ( newline ).append ( newline );
		}
		if ( simpleMail.getTextHTML () != null && ! simpleMail.getTextHTML ().isEmpty () ){
			sb.append("[Html] ").append ( simpleMail.getTextHTML () ).append ( newline ).append ( newline );
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
