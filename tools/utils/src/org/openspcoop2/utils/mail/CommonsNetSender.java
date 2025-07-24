/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.openspcoop2.utils.mail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import jakarta.mail.Multipart;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMultipart;
import javax.net.ssl.SSLContext;

import org.apache.commons.io.output.WriterOutputStream;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.smtp.AuthenticatingSMTPClient;
import org.apache.commons.net.smtp.SMTPClient;
import org.apache.commons.net.smtp.SMTPReply;
import org.apache.commons.net.smtp.SimpleSMTPHeader;
import org.slf4j.Logger;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.SSLUtilities;

/**
 * CommonsNetSender
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CommonsNetSender extends Sender {

	protected CommonsNetSender(Logger log) {
		super(log);
	}

	private static final String IN_PROGRESS_SUFFIX = "] ...";
	private static final String FAILED_SUFFIX = "] failed";
	
	@Override
	public void send(Mail mail, boolean debug) throws UtilsException {

		AuthenticatingSMTPClient client = null;
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		try {			
			if(mail.getSslConfig()!=null){
				StringBuilder bf = new StringBuilder();
				SSLContext sslContext = SSLUtilities.generateSSLContext(mail.getSslConfig(), bf);
				if(debug)
					this.logDebug(bf.toString());
				/**client.setSocketFactory(sslContext.getSocketFactory());*/
				client = new AuthenticatingSMTPClient(false, sslContext);
				client.setCharset(Charset.forName(mail.getEncoding()));
			}
			else {
				client = new AuthenticatingSMTPClient(mail.getEncoding());
			}
			
			
			 if (debug)
				 client.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(bout), true));

			client.setDefaultTimeout(this.getReadTimeout());
			client.setConnectTimeout(this.getConnectionTimeout());
			
			this.logDebug("Connect to ["+mail.getServerHost()+":"+mail.getServerPort()+IN_PROGRESS_SUFFIX);
			client.connect(mail.getServerHost(), mail.getServerPort());
			this.logDebug("Connected to ["+mail.getServerHost()+":"+mail.getServerPort()+"]");

			boolean esito = client.login();
			checkReply(client, esito, "Login failed");

			client.helo("[" + client.getLocalAddress().getHostAddress() + "]");
			
			if(mail.isStartTls()){
				esito = client.execTLS();
				checkReply(client, esito, "STARTTLS was not accepted");
			}

			String replyString = client.getReplyString();
			if(debug){
				this.logDebug("ReplyString: "+replyString);
			}
			int reply = client.getReplyCode();
			if(!SMTPReply.isPositiveCompletion(reply)) {
				throw new UtilsException("SMTP server refused connection "+ client.getReply() + client.getReplyString());
			}

			if(mail.getUsername()!=null){
				if(debug){
					this.logDebug("Authenticating ["+mail.getUsername()+IN_PROGRESS_SUFFIX);
				}
				client.helo("[" + client.getLocalAddress().getHostAddress() + "]");

				esito =  client.auth(AuthenticatingSMTPClient.AUTH_METHOD.LOGIN, mail.getUsername(), mail.getPassword());
				checkReply(client, esito, "Authentication failed");
				if(debug){
					this.logDebug("Authenticating ["+mail.getUsername()+"] ok");
				}
			}

			if(debug){
				this.logDebug("Set sender ["+mail.getFrom()+IN_PROGRESS_SUFFIX);
			}
			esito = client.setSender(mail.getFrom());
			checkReply(client, esito, "Set sender["+mail.getFrom()+FAILED_SUFFIX);
			
			if(debug){
				this.logDebug("Set to ["+mail.getTo()+IN_PROGRESS_SUFFIX);
			}
			esito = client.addRecipient(mail.getTo());
			checkReply(client, esito, "Set to["+mail.getTo()+FAILED_SUFFIX);
			
			List<String> ccList = mail.getCc();
			if(ccList!=null && !ccList.isEmpty()){
				for (String cc : ccList) {
					if(debug){
						this.logDebug("Add cc ["+cc+IN_PROGRESS_SUFFIX);
					}
					esito = client.addRecipient(cc);
					checkReply(client, esito, "Set cc["+cc+FAILED_SUFFIX);
				}
			}

			boolean attach = (mail.getBody().getAttachments()!=null && !mail.getBody().getAttachments().isEmpty());
			
			Writer writer = client.sendMessageData();
			if(writer!=null){
				
				if(debug){
					this.logDebug("Send ...");
				}
				
				// HEADER
				if(debug){
					this.logDebug("Subject ["+mail.getSubject()+IN_PROGRESS_SUFFIX);
				}
				SimpleSMTPHeader header = new SimpleSMTPHeader(mail.getFrom(), mail.getTo(), mail.getSubject());
						
				if(debug){
					this.logDebug("Body ("+mail.getBody().getContentType()+") ["+mail.getBody().getMessage()+IN_PROGRESS_SUFFIX);
				}
				
				TimeZone tz = TimeZone.getTimeZone( "GMT" );
				Date now = new Date();
				header.addHeaderField("Date", formatSMTPDate( now, tz ));
				
				if(mail.getUserAgent()!=null) {
					header.addHeaderField("User-Agent", mail.getUserAgent());
				}
				
				if(mail.getMessageIdDomain()!=null) {
					header.addHeaderField("Message-ID", "<"+UUID.randomUUID().toString()+"@"+mail.getMessageIdDomain()+">");
				}
				
				if(mail.getContentLanguage()!=null) {
					header.addHeaderField("Content-Language", mail.getContentLanguage());
				}
							
				ccList = mail.getCc();
				if(ccList!=null && !ccList.isEmpty()){
					for (String cc : ccList) {
						header.addCC(cc);	
					}
				}
				
				Multipart multipart = null;
				if(attach){
					 multipart = new MimeMultipart();
					 
					 if(mail.getBody().getMessage()!=null){
						 MimeBodyPart messagePart = new MimeBodyPart();
						 messagePart.setText(mail.getBody().getMessage());
						 if(mail.getBody().getContentType()!=null)
							 messagePart.setHeader(HttpConstants.CONTENT_TYPE, mail.getBody().getContentType());
						 multipart.addBodyPart(messagePart);
					 }
					 
					 for (MailAttach mailAttach : mail.getBody().getAttachments()) {
						 // NOTA: funziona solo per gli attachments testuali!
						 // motivo: usa un writer che corrompe poichè lavora su stringhe
						 MimeBodyPart attachmentPart = new MimeBodyPart();
						 if(mailAttach instanceof MailTextAttach text){
							 attachmentPart.setText(text.getContent());
						 }
						 else{
							 String msg = "La libreria CommonsNet non funziona correttamente con gli attachments di tipo binario. Usare il sender di tipo CommonsMail";
							 this.log.warn(msg);
							 System.out.println(msg);
							 MailBinaryAttach bin = (MailBinaryAttach) mailAttach;
							 attachmentPart.setContent(bin.getContent(), mailAttach.getContentType(this.log));
						 }
						 attachmentPart.setFileName(mailAttach.getName());
						 attachmentPart.setHeader(HttpConstants.CONTENT_TYPE, mailAttach.getContentType(this.log));
						 multipart.addBodyPart(attachmentPart);
					}
					 
				}
				
				if(!attach){
					if(mail.getBody().getContentType()!=null){
						 header.addHeaderField(HttpConstants.CONTENT_TYPE, mail.getBody().getContentType());
					}
				}
				else{
					header.addHeaderField(HttpConstants.CONTENT_TYPE, multipart.getContentType());
				}
				
				writer.write(header.toString());
				
				if(!attach){
					writer.write(mail.getBody().getMessage());
				}
				else{
					WriterOutputStream ww = 
							WriterOutputStream.builder()
							  .setWriter(writer)
	                          .setCharset(org.openspcoop2.utils.resources.Charset.UTF_8.getValue())
	                          .get();
					multipart.writeTo(ww);
					ww.flush();
					ww.close();
				}
		          				
				writer.close();
				if(!client.completePendingCommand()) {// failure
					throw new UtilsException("Failure to send the email "+ client.getReply() + client.getReplyString());
				}
				
			} else {
				throw new UtilsException("Failure to send the email "+ client.getReply() + client.getReplyString());
			}
			
			if(debug){
				this.logDebug("Send finished");
			}
			
		} catch (Exception e) {
			throw new UtilsException(e.getMessage(),e);
		} finally {
			try{
				client.logout();
			}catch(Exception e){
				// ignore
			}
			try{
				client.disconnect();
			}catch(Exception e){
				// ignore
			}
			if(bout!=null && bout.size()>0) {
				this.logDebug("Messages exchanged: \n"+bout.toString());
			}
		}
	}

	private static String formatSMTPDate( Date date, TimeZone tz )
			throws IllegalArgumentException
		{
		SimpleDateFormat dateFormat;
		Locale loc = Locale.US;

		dateFormat = new SimpleDateFormat( "EEE", loc );
		dateFormat.setTimeZone( tz );
		String day = dateFormat.format( date );
		day = day.substring( 0, 3 );

		dateFormat = new SimpleDateFormat( "MMM", loc );
		dateFormat.setTimeZone( tz );
		String month = dateFormat.format( date );
		month = month.substring( 0, 3 );

		dateFormat = new SimpleDateFormat( "dd", loc );
		dateFormat.setTimeZone( tz );
		String dayNum = dateFormat.format( date );

		dateFormat = new SimpleDateFormat( "yyyy HH:mm:ss", loc );
		dateFormat.setTimeZone( tz );
		String rest = dateFormat.format( date );

		return day + ", " + dayNum + " " + month + " " + rest + " +0000" ;		

	}
	
	private static void checkReply(SMTPClient sc, boolean esito, String object) throws UtilsException, IOException {
		if (SMTPReply.isNegativeTransient(sc.getReplyCode())) {
			throw new UtilsException(object+", transient SMTP error " + sc.getReply() + sc.getReplyString());
		} else if (SMTPReply.isNegativePermanent(sc.getReplyCode())) {
			throw new UtilsException(object+", permanent SMTP error " + sc.getReply() + sc.getReplyString());
		}
		else if(!esito){
			throw new UtilsException(object+", ["+esito+"] " + sc.getReply() + sc.getReplyString());
		}
	}
}
