<h1>mailkit : Mailer for Playframework 2.x</h1>
<i>Status RC-1</i>

<h2>About</h2>
This module eases the creation and delivery of email messages from your Play! 2 applications.
We built it based on a <a href="http://code.google.com/p/simple-java-mail/" target="_blank">simple-java-mail</a> fork.

<h2>Setup SMTP server connection</h2>
To begin with, you must declare SMTP configuration using the following set of parameters in your configuration file (<code>conf/application.conf</code>)

<pre><code>mail.smtp.mock=Boolean (Does the module send real mails or just dump it to the logs)
mail.smtp.host=String (SMTP server host, for example mail.google.com)
mail.smtp.port=Integer (SMTP server port, default : 25)
mail.smtp.user=String (SMTP user identifier, required for ssl or tls)
mail.smtp.pass=String (SMTP user password, required for ssl or tls)
mail.smtp.channel=String (SMTP transport strategy, values are ssl,tls,plain. default :plain)
</code></pre>

<h2>Usage</h2>

<b>1.</b> Send a mail in <b>1</b> line
<pre><code>new Email().from ( "from@from.com" ).to ( "to@to.com" ).subject ( "Subject" ).textAndHtml ( "Content" ).send ();</code></pre>

<b>2.</b> As usual, you can specify TOs, CCs, BCCs, REPLYTO addresses.
For each recipient, you can specify a name .
<pre><code>Email email = new Email ();
email.from ( "from@from.fr", "from" );
email.replyTo ( "replyto@replyto.fr", "replyto" );
email.to ( "to1@to1.fr", "to1" );
email.tos ( "to2@to2.fr", "to3@to3.fr");
email.cc ( "cc@cc.fr" );
email.bcc ( "bcc@bcc.fr","bcc" );
email.subject ( "simplemail" ).textAndHtml ( "Simple test" ).send ();
</code></pre>

<b>3.</b> Setting html content will automatically inline css directives.
For example, this implementation:
<pre><code>Email email = new Email ();
email.from ( "from@from.fr", "from" );
email.replyTo ( "replyto@replyto.fr", "replyto" );
email.to ( "to1@to1.fr", "to1" );
email.subject ( "simplemail" );
email.html ( "&lt;style&gt;p { border : 1px solid red;}&lt;/style&gt;&lt;body&gt;&lt;p&gt;Simple test&lt;/p&gt;&lt;/body&gt;" ).send ();</code></pre>
Will send a mail with this html content :
<pre><code>&lt;html&gt;
 &lt;head&gt;&lt;/head&gt;
 &lt;body&gt;
  &lt;p style="border : 1px solid red;"&gt;Simple test&lt;/p&gt;
 &lt;/body&gt;
&lt;/html&gt;</code></pre>

<b>4.</b> You can also embed images (absolute or relative) as cid with one method call !
<pre><code>Email email = new Email ();
email.from ( "from@from.fr" );
email.to ( "replyto@replyto.fr", "replyto" );
email.subject ( "simplemail" );
email.setEmbedImages ( true );
email.html ( "&lt;body&gt;&lt;p&gt;Simple test &lt;img src=\"/assets/images/favicon.png\"/&gt;&lt;/p&gt;&lt;/body&gt;" ).send ();</code></pre>

<b>5.</b> You can use this module both in java and scala ! 
<pre><code>object ScalaController extends Controller {

  def index = Action {implicit request =>
  	  val email = new Email ();
	  email.from ( "from@from.fr );
	  email.to ( "to@to.fr" );
	  email.subject ( "simplemail" );
	  email.html ( "Html content" ).send ();
    Ok("It works!")
  }    
}</code></pre>

<i>Be careful, one method call is changing in scala !!</i>
<pre><code>email.setEmbedImages ( true, "http://" + request.host );
</code></pre>
